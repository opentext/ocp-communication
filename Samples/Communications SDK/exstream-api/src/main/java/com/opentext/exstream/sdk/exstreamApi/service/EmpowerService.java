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

package com.opentext.exstream.sdk.exstreamApi.service;

import com.opentext.exstream.sdk.exstreamApi.model.dto.EmpowerServiceVersionInfo;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ServiceVersionInfo;
import com.opentext.exstream.sdk.exstreamApi.model.response.EmpowerResponse;
import com.opentext.exstream.sdk.exstreamApi.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Objects;

/**
 * Service layer for making API calls to Empower
 */
@Service
public class EmpowerService {
    private static final Logger logger = LoggerFactory.getLogger(EmpowerService.class);

    @Autowired
    EmpowerUriBuilder uriBuilder;
    @Autowired
    OtdsService otdsService;

    RestTemplate restTemplate;


    public EmpowerService() {
        restTemplate = RestTemplateUtils.buildRestTemplateWithLoggingAndErrorHandler();
    }

    /**
     * Get the Empower service version information
     * @return {@link ServiceVersionInfo} object from the Empower version response
     */
    public ServiceVersionInfo getVersion() {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildVersionUri();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Fetching version info from Empower: {}", uri.toString());
        ResponseEntity<EmpowerResponse<EmpowerServiceVersionInfo>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("Empower response: {}", response.getStatusCode());

        // Parse response
        EmpowerResponse<EmpowerServiceVersionInfo> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.body;
    }
}
