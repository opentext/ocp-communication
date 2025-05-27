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

import com.opentext.exstream.sdk.exstreamApi.model.dto.PageInfo;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ResourceFilter;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ImportPackageTypes;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.spring.ExstreamApiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * Builder class for Exstream DAS URLs. Reads the following environmental properties:<br>
 * exstream.url: The root backend URL for Exstream.
 * @see DasService
 */
@Component
public class DasUriBuilder extends ExstreamUriBuilder {
    private static final Logger logger = LoggerFactory.getLogger(DasUriBuilder.class);
    private static final String DESIGN_PATH_PREFIX = "design";
    private static final String RESOURCES_PREFIX = "/api/v1/resources";
    private static final String LINKS_PREFIX = "/api/v1/links";
    private static final String RESOURCES_WORKFLOW_STATE_PATH = "state";
    private static final String RESOURCES_WORKFLOW_CONTENT_PATH = "content";
    private static final String IMPORT_PREFIX = "/api/v1/import";
    private static final String DOMAINS_PREFIX = "/api/v1/domains";
    private static final String MANIFESTS_PREFIX = "/api/v1/manifests";
    private static final String MANIFESTS_COMMUNICATION_SET_PATH = "communication-set";
    private static final String VERSION_PREFIX = "/api/v1/version";

    @Autowired
    ExstreamApiConfiguration exstreamApiConfiguration;

    @Value("${exstream.das.url:http://localhost/}")
    protected String dasUrl;

    @Value("${otds.tenant:sample}")
    private String tenant;

    @Value("${otds.subscription.name:}")
    private String otdsSubscriptionName;

    public DasUriBuilder() {
        super(DESIGN_PATH_PREFIX);
    }
    @PostConstruct
    private void logConfig() {
        baseUrl = dasUrl;
        logger.info("dasUrl={}", this.getBaseUriBuilder().toUriString());
    }

    /**
     * Build a URI for the import package endpoint
     * @param domain The domain id to access.
     * @param packageType The type of import package
     * @param commit True/false flag indicating whether to commit the import
     * @return {@link URI} for the endpoint
     */
    public URI buildImportPackageUri(String domain, ImportPackageTypes packageType, boolean commit) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(IMPORT_PREFIX)
                .pathSegment(packageType.name().toLowerCase(), domain);
        if (commit) {
            builder.queryParam("commit", String.valueOf(true));
        }
        return builder.build().toUri();
    }

    /**
     * Build a URI for the resources endpoint
     * @param domain The domain id to access.
     * @param resourceFilter The resource filter. {@link ResourceFilter} properties become filter query parameters
     * @param pageInfo The page info. {@link PageInfo} properties become query parameters
     * @return {@link URI} for the endpoint
     */
    public URI buildResourcesUri(String domain, ResourceFilter resourceFilter, PageInfo pageInfo) {
        UriComponentsBuilder builder = getBaseUriBuilder()
            .path(RESOURCES_PREFIX)
            .pathSegment(domain);

        MultiValueMap<String, String> queryParamMap = resourceFilter.getQueryParamMap();
        if (!queryParamMap.isEmpty()) {
            builder.queryParams(queryParamMap);
        }
        if (pageInfo != null) {
            builder.queryParam("count", Integer.toString(pageInfo.getCount()));
            builder.queryParam("offset", Integer.toString(pageInfo.getOffset()));
        }
        return builder.build().toUri();
    }

    /**
     * Build a URI for the resources workflow state endpoint
     * @param domain The domain id to access
     * @param resourceId The resource ID to modify
     * @return {@link URI} for the endpoint
     */
    public URI buildResourcesWorkflowStateUri(String domain, String resourceId) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(RESOURCES_PREFIX)
                .pathSegment(domain, resourceId, RESOURCES_WORKFLOW_STATE_PATH);
        return builder.build().toUri();
    }

    /**
     * Build a URI for the resources content endpoint
     * @param domain The domain id to access
     * @param resourceId The resource ID to modify
     * @return {@link URI} for the endpoint
     */
    public URI buildResourcesContentUri(String domain, String resourceId) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(RESOURCES_PREFIX)
                .pathSegment(domain, resourceId, RESOURCES_WORKFLOW_CONTENT_PATH);
        return builder.build().toUri();
    }

    /**
     * Build a URI for the resources content endpoint
     * @param domain The domain id to access
     * @param resourceName The resource name. Required.
     * @param resourceType The resource type. Required.
     * @param resourceSubtype The resource subtype. Optional.
     * @return {@link URI} for the endpoint
     */
    public URI buildResourcesContentUri(String domain, String resourceName, ResourceType resourceType, String resourceSubtype) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(RESOURCES_PREFIX)
                .pathSegment(domain, RESOURCES_WORKFLOW_CONTENT_PATH)
                .queryParam("name", resourceName)
                .queryParam("type", resourceType);
        if (resourceSubtype != null && !resourceSubtype.isBlank()) {
            builder.queryParam("subtype", resourceSubtype);
        }
        return builder.build().toUri();
    }

    /**
     * Build a URI for the links endpoint
     * @param domain The domain id to access.
     * @param linkSubjectId The link subject ID to query for
     * @param linkSubjectVersion The version of the link subject. Null will query for the latest version.
     * @param linkDepth The maximum link depth for a recursive traversal of a link tree
     * @param resourceFilter The resource filter. {@link ResourceFilter} properties become filter query parameters
     * @return {@link URI} for the endpoint
     */
    public URI buildLinksUri(String domain, String linkSubjectId, Integer linkSubjectVersion, int linkDepth, ResourceFilter resourceFilter) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(LINKS_PREFIX)
                .pathSegment(domain);

        // Link params
        builder.queryParam("linkSubjectId", linkSubjectId)
               .queryParam("linkDepth", linkDepth);

        if (linkSubjectVersion == null) {
            builder.queryParam("linkSubjectVersion", linkSubjectVersion);
        }

        // Resource filter
        MultiValueMap<String, String> queryParamMap = resourceFilter.getQueryParamMap();
        if (!queryParamMap.isEmpty()) {
            builder.queryParams(queryParamMap);
        }

        return builder.build().toUri();
    }

    public URI buildRecursiveLinksUri(String domain, String linkObjectId, int linkDepth, ResourceFilter resourceFilter) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(LINKS_PREFIX)
                .pathSegment(domain)
                .pathSegment("resources");

        // Link params
        builder.queryParam("linkObjectId", linkObjectId)
                .queryParam("linkDepth", linkDepth);

        // Resource filter
        MultiValueMap<String, String> queryParamMap = resourceFilter.getQueryParamMap();
        if (!queryParamMap.isEmpty()) {
            builder.queryParams(queryParamMap);
        }

        return builder.build().toUri();
    }

    /**
     * Build a URI for the manifests for communication sets endpoint
     * @param domain The domain to access
     * @param communicationSetId The communication set ID to query for
     * @return {@link URI} for the endpoint
     */
    public URI buildManifestsForCommunicationSetsUri(String domain, String communicationSetId) {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(MANIFESTS_PREFIX)
                .pathSegment(domain, MANIFESTS_COMMUNICATION_SET_PATH, communicationSetId);
        return builder.build().toUri();
    }

    /**
     * Build a URI for the domains endpoint
     * @return {@link URI} for the endpoint
     */
    public URI buildDomainsUri() {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path(DOMAINS_PREFIX);
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

	public URI buildFrontEndUri() {
        UriComponentsBuilder builder = getBaseUriBuilder()
                .path("/");
        if (exstreamApiConfiguration.isOT2()) {
            builder.queryParam("subscription-name", this.otdsSubscriptionName);
        } else if (exstreamApiConfiguration.isLocal()) {
            builder.queryParam("tenant", this.tenant);
        }

        return builder.build().toUri();
	}
}
