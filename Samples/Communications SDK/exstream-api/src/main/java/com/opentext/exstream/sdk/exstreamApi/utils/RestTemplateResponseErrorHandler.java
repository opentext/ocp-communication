/*
 * Copyright 2023 Open Text Corporation, All Rights Reserved.
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.opentext.exstream.sdk.exstreamApi.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.opentext.exstream.sdk.exstreamApi.model.response.ExstreamErrorResponse;
import com.opentext.exstream.sdk.exstreamApi.model.response.OrchestrationErrorResonse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return (response.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR
                || response.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws RestClientException, IOException {
        try {
            // Try to parse an ExstreamErrorResponse. There isn't one for every single error case, so the response
            // may be in a different format. In that case the response will fail to parse.
            ObjectMapper mapper = new ObjectMapper();
            ExstreamErrorResponse errorResponse = null;
            try {
                errorResponse = mapper.readValue(response.getBody(), ExstreamErrorResponse.class);
                logger.error("Received error response. Status code: {}", response.getRawStatusCode());
                logger.error("\n" + errorResponse.toString());
                if (response.getStatusCode() == HttpStatus.CONFLICT && errorResponse.errorCode == 309016) {
                    logger.error("If you're attempting to import a package with the conflict resolution policy ERROR, and you have conflicting resources, then the import cannot be completed. Either remove the conflicting resource from DAS, or use a different resolution policy (SKIP or REPLACE) and retry the import.");
                }
            } catch (InvalidFormatException e) {
                // Orchestration errors can come back with the full response data still, but just have status: error
                OrchestrationErrorResonse orchestrationErrorDataResponse = mapper.readValue(response.getBody(), OrchestrationErrorResonse.class);
                logger.error("Received Orchestration error data response. Status code: {}", response.getRawStatusCode());
                logger.error("\n" + orchestrationErrorDataResponse.toString());
                if (orchestrationErrorDataResponse.status.equals("error") &&
                    !orchestrationErrorDataResponse.data.isEmpty() &&
                    orchestrationErrorDataResponse.data.get(0).statusCode.equals(Integer.toString(HttpStatus.UNAUTHORIZED.value()))) {
                    logger.error("401 - Unauthorized. If you're attempting to generate Empower output with Exstream Orchestration and getting this error, your empowerUser is probably wrong. Try a different user for the empowerUser that has permission to import Empower documents.");
                }
            }
            throw new RestClientException("The Exstream API returned an error response. See log for details.");
        } catch (IOException e) {
            // If we can't parse the error, log additional information for a few cases and rethrow the exception
            logger.error("The Exstream API returned an error but the response could not be parsed. Status code: {}", response.getRawStatusCode());
            if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
                logger.error("400 - Bad Request. One of the parameters in the request is likely an incorrect value. Double check the values and try again once the error is corrected.");
            }
            throw e;
        }
    }
}
