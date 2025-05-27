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

import com.opentext.exstream.sdk.exstreamApi.spring.ExstreamApiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * Builder class for OTDS URLs. Reads the following environmental properties:<br>
 * otds.url: The URL for OTDS.<br>
 * @see OtdsService
 */
@Component
public class OtdsUriBuilder {
	private static final Logger logger = LoggerFactory.getLogger(OtdsUriBuilder.class);

    @Autowired
    ExstreamApiConfiguration configuration;

    @Value("${otds.url:http://localhost/otds}")
	private String otdsUrl;
    private static final String OTDS_API_PATH = "otdsws";
	private static final String TENANT_PREFIX = "otdstenant";
	private static final String OAUTH2_TOKEN_PATH = "/oauth2/token";

    private UriComponentsBuilder uriBuilder;

	public OtdsUriBuilder() {
	}

    @PostConstruct
    private void logConfig() {
        logger.info("otdsUrl={}", this.otdsUrl);
    }

	/**
	 * Build a URI for the OTDS token API endpoint for a given tenantId.
	 * @param tenantId Desired tenantId to get the URI for.
	 * @return A URI for the endpoint.
	 */
	public URI buildOtdsTokenUri(String tenantId) {
        UriComponentsBuilder builder = getBaseUriBuilder();

        if (!configuration.isOT2()) {
            builder.pathSegment(OTDS_API_PATH);
        }

        builder.pathSegment(TENANT_PREFIX, tenantId)
                .path(OAUTH2_TOKEN_PATH);

        return builder.build().toUri();
	}

    private UriComponentsBuilder getBaseUriBuilder() {
        if (uriBuilder == null) {
            uriBuilder = UriComponentsBuilder.fromHttpUrl(otdsUrl);
        }
        return uriBuilder.cloneBuilder();
    }
}
