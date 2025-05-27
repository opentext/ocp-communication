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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * Builder class for Exstream Orchestration URLs. Reads the following environmental properties:<br>
 * exstream.url: The root URL for Exstream.
 * @see OrchestrationService
 */
@Component
public class OrchestrationUriBuilder extends ExstreamUriBuilder {
	private static final Logger logger = LoggerFactory.getLogger(OrchestrationUriBuilder.class);

    private static final String ORCHESTRATION_PATH_PREFIX = "orchestration";
    private static final String VERSION_PREFIX = "/api/v1/version";
	private static final String ON_DEMAND_OUTPUT_PREFIX = "/api/v1/inputs/ondemand";
    private static final String ON_DEMAND_GENERATE_PATH = "generate";
    private static final String ON_DEMAND_FULFILL_PATH = "fulfill";
	private static final String ON_DEMAND_FULFILLMENT_PREFIX = "/api/v1/inputs/fulfillment/ondemand";

    @Value("${exstream.orchestration.url:http://localhost/}")
    private String orchestrationUrl;

	public OrchestrationUriBuilder() {
        super(ORCHESTRATION_PATH_PREFIX);
	}
    @PostConstruct
    private void logConfig() {
        baseUrl = orchestrationUrl;
        logger.info("orchestrationUrl={}", this.getBaseUriBuilder().toUriString());
    }

	/**
	 * Build a URI for the endpoint that will generate on-demand output
	 * @param domain The domain id to access.
	 * @return {@link URI} for the endpoint
	 */
	public URI buildOnDemandGenerateUri(String domain) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(ON_DEMAND_OUTPUT_PREFIX)
                .pathSegment(domain, ON_DEMAND_GENERATE_PATH);
        return builder.build().toUri();
	}

	/**
	 * Build a URI for the endpoint that will fulfill an Empower document
	 * @param domain The domain id to access.
     * @return {@link URI} for the endpoint
	 */
	public URI buildOnDemandFulfillmentUri(String domain) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(ON_DEMAND_FULFILLMENT_PREFIX)
                .pathSegment(domain, ON_DEMAND_FULFILL_PATH);
        return builder.build().toUri();
	}

    /**
     * Build a URI for the version endpoint
     * @return {@link URI} for the endpoint
     */
    public URI buildVersionUri() {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(VERSION_PREFIX);
        return builder.build().toUri();
    }
}
