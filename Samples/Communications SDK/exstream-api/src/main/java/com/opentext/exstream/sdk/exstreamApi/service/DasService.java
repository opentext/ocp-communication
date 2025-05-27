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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opentext.exstream.sdk.exstreamApi.model.dto.*;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ImportPackageTypes;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ImportReplacementPolicy;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.WorkflowState;
import com.opentext.exstream.sdk.exstreamApi.model.request.WorkflowRequestBody;
import com.opentext.exstream.sdk.exstreamApi.model.response.*;
import com.opentext.exstream.sdk.exstreamApi.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;
import java.util.Objects;

/**
 * Service layer for making API calls to an Exstream DAS instance.
 */
@Service
public class DasService {
    private static final Logger logger = LoggerFactory.getLogger(DasService.class);

    // OTDS service for authentication
    @Autowired
    OtdsService otdsService;

    @Autowired
    DasUriBuilder uriBuilder;

    RestTemplate restTemplate;

    public DasService() {
        restTemplate = RestTemplateUtils.buildRestTemplateWithLoggingAndErrorHandler();
    }

    /**
     * Get a list of resources from DAS
     * @param domain The domain to get resources from
     * @param resourceFilter {@link ResourceFilter} to apply to the request
     * @return {@link Collection} of {@link DasResourceVersion} objects representing each resource from the response
     */
    public Collection<DasResourceVersion> listResources(String domain, ResourceFilter resourceFilter) {
        return listResources(domain, resourceFilter, new PageInfo().setDefaults());
    }

    /**
     * Get a list of resources from DAS
     * @param domain The domain to get resources from
     * @param resourceFilter {@link ResourceFilter} to apply to the request
     * @param pageInfo {@link PageInfo} to send with the request
     * @return {@link Collection} of {@link DasResourceVersion} objects representing each resource from the response
     */
    public Collection<DasResourceVersion> listResources(String domain, ResourceFilter resourceFilter, PageInfo pageInfo) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildResourcesUri(domain, resourceFilter, pageInfo);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Fetching resources from DAS: {}", uri.toString());
        ResponseEntity<ExstreamPageResponse<DasResourceVersion>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamPageResponse<DasResourceVersion> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Get a list of domains from DAS
     * @return {@link Collection} of {@link DasDomain} objects from DAS
     */
    public Collection<DasDomain> listDomains() {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildDomainsUri();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Fetching domains from DAS: {}", uri.toString());
        ResponseEntity<ExstreamPageResponse<DasDomain>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamPageResponse<DasDomain> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Imports an export file to DAS. The {@link ImportReplacementPolicy} ERROR is used by default.
     * @param domain The domain to import into.
     * @param exportPackageBody The export file that is being imported. The object should be a {@link org.springframework.core.io.Resource}, {@link java.io.File}, or byte array.
     * @return The {@link ImportResponse} from DAS.
     */
    public ImportResponse importPackage(String domain, Object exportPackageBody) {
        return importPackage(domain, exportPackageBody, ImportPackageTypes.DAS, ImportReplacementPolicy.ERROR, true);
    }

    /**
     * Imports an export file to DAS
     * @param domain The domain to import into.
     * @param exportPackageBody The export file that is being imported. The object should be a {@link org.springframework.core.io.Resource}, {@link java.io.File}, or byte array.
     * @param packageType The package type being imported. See {@link ImportPackageTypes}.
     * @param generalReplacementPolicy The replacement policy to use for conflicting resources. See {@link ImportReplacementPolicy}
     * @param commit Flag to indicate whether to commit the import. Use false for a "dry run".
     * @return The {@link ImportResponse} from DAS.
     */
    public ImportResponse importPackage(String domain, Object exportPackageBody, ImportPackageTypes packageType, ImportReplacementPolicy generalReplacementPolicy, boolean commit) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Add export package file
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("exportPackage", exportPackageBody);

        // Add conflict settings file with the general replacement policy
        ImportResponse conflictSettings = new ImportResponse();
        conflictSettings.policies.generalPolicy = generalReplacementPolicy;
        ObjectMapper mapper = new ObjectMapper();
        byte[] conflictSettingsBytes;
        try {
            conflictSettingsBytes = mapper.writeValueAsBytes(conflictSettings);
        } catch (JsonProcessingException e) {
            logger.error("Could not serialize conflict settings", e);
            throw new RuntimeException(e);
        }

        // We must override getFilename here or else the conflict settings file won't be recognized as a valid part of the request
        ByteArrayResource conflictSettingsResource = new ByteArrayResource(Objects.requireNonNull(conflictSettingsBytes)) {
            @Override
            public String getFilename() {
                return "conflictSettings.json";
            }
        };
        body.add("conflictSettings", conflictSettingsResource);
        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // Get the URI
        final URI uri = uriBuilder.buildImportPackageUri(domain, packageType, commit);

        // Send the request
        logger.info("Importing package to DAS: {}", uri);
        ResponseEntity<ExstreamDataResponse<ImportResponse>> response = restTemplate.exchange(uri, HttpMethod.POST, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamDataResponse<ImportResponse> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Get the DAS service version information
     * @return {@link ServiceVersionInfo} object from the DAS version response
     */
    public ServiceVersionInfo getVersion() {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildVersionUri();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Fetching version info from DAS: {}", uri.toString());
        ResponseEntity<BackendServiceVersionInfo> response = restTemplate.exchange(uri, HttpMethod.GET, entity, BackendServiceVersionInfo.class);

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        BackendServiceVersionInfo responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody;
    }

    /**
     * Move a resource to a new workflow state.<br>
     * See {@link WorkflowState} for state values.<br>
     * Valid state changes are DRAFT to REVIEW, REVIEW to APPROVED, APPROVED to DRAFT, REVIEW to REJECTED.<br>
     * Moving an object from APPROVED to DRAFT creates a new version of the object.
     * @param domain The domain the resource is associated with
     * @param resourceId The ID of the resource being modified
     * @param targetState The desired state for the resource
     * @param workflowComment An optional comment describing the workflow change
     * @param shouldLockResource A flag that indicates whether to lock the resource while changing the state
     * @return {@link DasResourceVersion} object of the resource after the workflow change is complete
     */
    public DasResourceVersion changeWorkflowState(String domain, String resourceId, WorkflowState targetState, String workflowComment, boolean shouldLockResource) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WorkflowRequestBody> entity = new HttpEntity<>(new WorkflowRequestBody(targetState, workflowComment, shouldLockResource), headers);

        // Get the URI
        final URI uri = uriBuilder.buildResourcesWorkflowStateUri(domain, resourceId);

        // Send the request
        logger.info("Changing the workflow state for {} to {}: {}", resourceId, targetState, uri);
        ResponseEntity<ExstreamDataResponse<DasResourceVersion>> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamDataResponse<DasResourceVersion> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Upload new content for a resource
     * @param domain The domain the resource is associated with
     * @param resourceId The ID of the resource being modified
     * @param newContent The new content for the resource being updated
     * @return {@link DasResourceVersion} object of the resource after the update
     */
    public DasResourceVersion updateResourceContent(String domain, String resourceId, Object newContent) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Add the content to the body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", newContent);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // Get the URI
        final URI uri = uriBuilder.buildResourcesContentUri(domain, resourceId);

        // Send the request
        logger.info("Uploading new content for resource {}: {}", resourceId, uri);
        ResponseEntity<ExstreamDataResponse<DasResourceVersion>> response = restTemplate.exchange(uri, HttpMethod.PUT, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamDataResponse<DasResourceVersion> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Create a new resource in DAS
     * @param domain The domain the resource will be associated with. Required
     * @param resourceName The name for the new resource. Required.
     * @param resourceType The type of the new resource. Required.
     * @param resourceSubtype The subtype of the new resource. Optional.
     * @param resourceContent The new content for the resource being created. Optional.
     * @return {@link DasResourceVersion} object of the resource that was created
     */
    public DasResourceVersion createResource(String domain, String resourceName, ResourceType resourceType, String resourceSubtype, Object resourceContent) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // Add the content to the body
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resourceContent);

        HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(body, headers);

        // Get the URI
        final URI uri = uriBuilder.buildResourcesContentUri(domain, resourceName, resourceType, resourceSubtype);

        // Send the request
        logger.info("Creating new resource: {}", uri);
        ResponseEntity<ExstreamDataResponse<DasResourceVersion>> response = restTemplate.exchange(uri, HttpMethod.POST, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamDataResponse<DasResourceVersion> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Get a list of links from DAS
     * @param domain The domain to get links from
     * @param linkSubjectId The link subject ID to query for
     * @param linkDepth The maximum link depth for a recursive traversal of a link tree
     * @param resourceFilter {@link ResourceFilter} to apply when querying links
     * @return {@link Collection} of {@link DasLink} objects representing each link from the response
     */
    public Collection<DasLink> listLinks(String domain, String linkSubjectId, int linkDepth, ResourceFilter resourceFilter) {
        return listLinks(domain, linkSubjectId, null, linkDepth, resourceFilter);
    }

    /**
     * Get a list of links from DAS
     *
     * @param domain The domain to get links from
     * @param linkSubjectId The link subject ID to query for
     * @param linkSubjectVersion The version of the link subject. Null will query for the latest version.
     * @param linkDepth The maximum link depth for a recursive traversal of a link tree
     * @param resourceFilter {@link ResourceFilter} to apply when querying links
     * @return {@link Collection} of {@link DasLink} objects representing each link from the response
     */
    public Collection<DasLink> listLinks(String domain, String linkSubjectId, Integer linkSubjectVersion, int linkDepth, ResourceFilter resourceFilter) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildLinksUri(domain, linkSubjectId, linkSubjectVersion, linkDepth, resourceFilter);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Querying links from DAS: {}", uri.toString());
        ResponseEntity<ExstreamPageResponse<DasLink>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamPageResponse<DasLink> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    public Collection<DasResourceVersion> recursiveListLinks(String domain, String linkObjectId, int linkDepth, ResourceFilter resourceFilter) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildRecursiveLinksUri(domain, linkObjectId, linkDepth, resourceFilter);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Querying recursive links from DAS: {}", uri.toString());
        ResponseEntity<ExstreamPageResponse<DasResourceVersion>> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        ExstreamPageResponse<DasResourceVersion> responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody.data;
    }

    /**
     * Get the manifest for a communication set in DAS
     * @param domain The domain to get the manifest from
     * @param communicationSetId The communication set ID to get the manifest for
     * @return {@link DasManifest} object from the response
     */
    public DasManifest getManifestForCommunicationSet(String domain, String communicationSetId) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildManifestsForCommunicationSetsUri(domain, communicationSetId);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Getting the manifest for communication set {} from DAS: {}", communicationSetId, uri.toString());
        ResponseEntity<DasManifest> response = restTemplate.exchange(uri, HttpMethod.GET, entity, new ParameterizedTypeReference<>(){});

        // Log response code
        logger.info("DAS response: {}", response.getStatusCode());

        // Parse response
        DasManifest responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody;
    }
}
