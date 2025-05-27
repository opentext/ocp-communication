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
 * Builder class for Exstream Empower URLs. Reads the following environmental properties:<br>
 * exstream.url: The root URL for Exstream.<br>
 * otds.tenant: The name of the Empower tenant to access
 */
@Component
public class EmpowerUriBuilder extends ExstreamUriBuilder {
	private static final Logger logger = LoggerFactory.getLogger(EmpowerUriBuilder.class);

    @Value("${otds.tenant:sample}")
    private String empowerTenant;

    @Value("${otds.subscription.name:}")
    private String otdsSubscriptionName;

    @Value("${exstream.empower.url:http://localhost/}")
    protected String empowerUrl;

    @Autowired
    ExstreamApiConfiguration exstreamApiConfiguration;

    private static final String EMPOWER_PATH_PREFIX = "empower";

    private static final String DOCEDIT_PREFIX = "/api/v1/docedit";
    private static final String DOCUMENT_OPEN_SUFFIX = "open";

    private static final String VERSION_PREFIX = "/api/v1/version";

	public EmpowerUriBuilder() {
        super(EMPOWER_PATH_PREFIX);
	}

    @PostConstruct
    private void logConfig() {
        baseUrl = empowerUrl;
        logger.info("empowerUrl={}", this.getEmpowerUri());
        logger.info("empowerTenant={}", this.empowerTenant);
        logger.info("otdsSubscriptionName={}", this.otdsSubscriptionName);
    }

    /**
     * Get a URI for Exstream Empower
     * @return {@link URI} to Exstream Empower
     */
    public URI getEmpowerUri() {
        return getBaseUriBuilder().build().toUri();
    }

    /**
     * Build a URI for opening an Empower document by its document id.
     * @param documentId The document id of the Empower document to open.
     * @return {@link URI} to open the specified Empower document
     */
    public URI buildEmpowerOpenDocumentUri(String documentId) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(DOCEDIT_PREFIX)
                .pathSegment(documentId, DOCUMENT_OPEN_SUFFIX);

        if (exstreamApiConfiguration.isOT2()) {
            builder.queryParam("subscription", this.otdsSubscriptionName);
        } else if (exstreamApiConfiguration.isLocal()) {
            builder.queryParam("tenant", this.empowerTenant);
        }
        builder.queryParam("hosted", true);
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
