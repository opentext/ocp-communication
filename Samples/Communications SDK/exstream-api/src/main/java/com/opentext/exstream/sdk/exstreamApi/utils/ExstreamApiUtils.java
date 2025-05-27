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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opentext.exstream.sdk.exstreamApi.model.dto.EmpowerOutputChannelContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Base64;

public class ExstreamApiUtils {
    private static final Logger logger = LoggerFactory.getLogger(ExstreamApiUtils.class);
    /**
     * Parse an Empower document id from a base64 encoded string of JSON. The expected JSON is an object that contains a field named "documentId" with a value of a document id string.<br>
     * Example: <pre>{"documentId":"96d688ff-74df-43f6-8868-be64c04c2a23"}</pre>
     * @param encodedContent A base64 encoded string
     * @return The extracted document id
     */
    public static EmpowerOutputChannelContent readEmpowerOutputChannelContent(String encodedContent) {
        String decodedContent = new String(Base64.getDecoder().decode(encodedContent.getBytes()));
        try {
            return new ObjectMapper().readValue(decodedContent, EmpowerOutputChannelContent.class);
        } catch (JsonProcessingException e) {
            logger.error("Could not deserialize JSON to EmpowerOutputChannelContent:\n{}", decodedContent);
            throw new RuntimeException(e);
        }
    }
}
