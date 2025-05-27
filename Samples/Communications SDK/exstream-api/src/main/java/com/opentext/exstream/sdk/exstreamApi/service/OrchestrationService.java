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

import com.opentext.exstream.sdk.exstreamApi.model.dto.EmpowerOutputChannelContent;
import com.opentext.exstream.sdk.exstreamApi.model.dto.OrchestrationResponseData;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ServiceVersionInfo;
import com.opentext.exstream.sdk.exstreamApi.model.dto.BackendServiceVersionInfo;
import com.opentext.exstream.sdk.exstreamApi.model.request.EmpowerFulfillmentRequestBody;
import com.opentext.exstream.sdk.exstreamApi.model.response.OrchestrationDataListResponse;
import com.opentext.exstream.sdk.exstreamApi.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Service layer for making API calls to an Exstream Orchestration instance.
 */
@Service
public class OrchestrationService {
    private static final Logger logger = LoggerFactory.getLogger(OrchestrationService.class);
    private static final String COMMUNICATION_ID_HEADER = "communicationId";
    private static final String DRIVER_DATA_SOURCE_HEADER = "driverDataSource";
    private static final String EMPOWER_USER_HEADER = "empowerUser";
    private static final String PRESERVE_DOCUMENTS_HEADER = "preserveDocuments";

    // OTDS service for authentication
    @Autowired
    OtdsService otdsService;

    @Autowired
    OrchestrationUriBuilder uriBuilder;

    RestTemplate restTemplate;

    public OrchestrationService() {
        restTemplate = RestTemplateUtils.buildRestTemplateWithLoggingAndErrorHandler();
    }

    /**
     * Get the Orchestration service version information
     *
     * @return {@link ServiceVersionInfo} object from the Orchestration version response
     */
    public ServiceVersionInfo getVersion() {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getToken()));

        final URI uri = uriBuilder.buildVersionUri();

        HttpEntity<?> entity = new HttpEntity<>(headers);

        // Send the request
        logger.info("Fetching version info from Orchestration: {}", uri.toString());
        ResponseEntity<BackendServiceVersionInfo> response = restTemplate.exchange(uri, HttpMethod.GET, entity, BackendServiceVersionInfo.class);

        // Log response code
        logger.info("Orchestration response: {}", response.getStatusCode());

        // Parse response
        BackendServiceVersionInfo responseBody = Objects.requireNonNull(response.getBody());
        logger.debug("Response data:\n{}", responseBody);

        return responseBody;
    }

    /**
     * Generate on-demand output from the Exstream Orchestration service.<br>
     * This method gets the full response data from the Orchestration service and returns the list of outputs.
     *
     * @param domain             Domain id to access.
     * @param communicationId    The id of the communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param driverData         The driver file data that will be sent to the Exstream Orchestration service.
     * @param requestContentType Content type of the driver data file.
     * @return {@link List}<{@link OrchestrationResponseData}> objects representing each output in the response.
     */
    public List<OrchestrationResponseData> generateOnDemandOutputWithFullResponse(String domain, String communicationId, String driverDataSource, Object driverData, MediaType requestContentType) {
        return generateOnDemandOutput(domain, communicationId, driverDataSource, Optional.empty(), driverData, requestContentType, Optional.empty(), OrchestrationDataListResponse.class).data;
    }

    /**
     * Generate on-demand output from the Exstream Orchestration service<br>
     * This method specifies an Accept header in the request that will direct the Exstream Orchestration service to only
     * return the content of the output file that matches the specified media type.<br>
     * Example: Passing "application/pdf" for the acceptsMediaType will get a raw PDF back in the response
     *
     * @param domain             Domain id to access.
     * @param communicationId    The id of the communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param driverData         The driver file data that will be sent to the Exstream Orchestration service.
     * @param requestContentType Content type of the driver data file.
     * @param acceptsMediaType   Content type to pass to the Accept header.
     * @return Byte array containing the binary data of the output file from the response
     */
    public byte[] generateOnDemandOutputWithContentResponse(String domain, String communicationId, String driverDataSource, Object driverData, MediaType requestContentType, MediaType acceptsMediaType) {
        return generateOnDemandOutput(domain, communicationId, driverDataSource, Optional.empty(), driverData, requestContentType, Optional.of(acceptsMediaType), byte[].class);
    }

    /**
     * Generate on-demand Empower output from the Exstream Orchestration service.<br>
     * This method gets the full response data from the Orchestration service and returns the list of outputs.
     *
     * @param domain             Domain id to access.
     * @param communicationId    The id of the communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param empowerUser        The user that should be used to import documents into Empower
     * @param driverData         The driver file data that will be sent to the Exstream Orchestration service.
     * @param requestContentType Content type of the driver data file.
     * @return {@link List}<{@link OrchestrationResponseData}> objects representing each output in the response.
     */
    public List<OrchestrationResponseData> generateOnDemandEmpowerOutputWithFullResponse(String domain, String communicationId, String driverDataSource, String empowerUser, Object driverData, MediaType requestContentType) {
        return generateOnDemandOutput(domain, communicationId, driverDataSource, Optional.of(empowerUser), driverData, requestContentType, Optional.empty(), OrchestrationDataListResponse.class).data;
    }

    /**
     * Generate on-demand Empower output from the Exstream Orchestration service<br>
     * This method specifies an Accept header in the request that will direct the Exstream Orchestration service to only
     * return the content with the Empower document id as JSON content.
     *
     * @param domain             Domain id to access.
     * @param communicationId    The id of the communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param empowerUser        The user that should be used to import documents into Empower
     * @param driverData         The driver file data that will be sent to the Exstream Orchestration service.
     * @param requestContentType Content type of the driver data file.
     * @return {@link EmpowerOutputChannelContent} object containing the document id of the generated document
     */
    public EmpowerOutputChannelContent generateOnDemandEmpowerOutputDocumentIdResponse(String domain, String communicationId, String driverDataSource, String empowerUser, Object driverData, MediaType requestContentType) {
        return generateOnDemandOutput(domain, communicationId, driverDataSource, Optional.of(empowerUser), driverData, requestContentType, Optional.of(MediaType.APPLICATION_JSON), EmpowerOutputChannelContent.class);
    }

    /**
     * Fulfill an Empower document with the Exstream Orchestration on-demand service.<br>
     * This method gets the full response data from the Orchestration service and returns the list of outputs.
     *
     * @param domain             Domain id to access.
     * @param empowerDocumentId  The id of the Empower document to fulfill
     * @param communicationId    The id of the fulfillment communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this
     *                           value from the communication set associated with the communication).
     * @param preserveDocuments  Flag to indicate whether to preserve the Empower document after fulfillment. Setting
     *                           this to false will remove the document from Empower upon fulfillment.
     * @return {@link List}<{@link OrchestrationResponseData}> objects representing each output in the response.
     */
    public List<OrchestrationResponseData> fulfillOnDemandEmpowerDocumentWithFullResponse(String domain, String empowerDocumentId, String communicationId, String driverDataSource, boolean preserveDocuments) {
        return fulfillOnDemandOutput(domain, empowerDocumentId, communicationId, driverDataSource, preserveDocuments, Optional.empty(), OrchestrationDataListResponse.class).data;
    }

    /**
     * Fulfill an Empower document with the Exstream Orchestration on-demand service.<br>
     * This method specifies an Accept header in the request that will direct the Exstream Orchestration service to only
     * return the content of the output file that matches the specified media type.<br>
     * Example: Passing "application/pdf" for the acceptsMediaType will get a raw PDF back in the response
     *
     * @param domain             Domain id to access.
     * @param empowerDocumentId  The id of the Empower document to fulfill
     * @param communicationId    The id of the fulfillment communication to generate output for.
     * @param driverDataSource   The prodDsn value of the driver file associated with the communication (retrieve this
     *                           value from the communication set associated with the communication).
     * @param preserveDocuments  Flag to indicate whether to preserve the Empower document after fulfillment. Setting
     *                           this to false will remove the document from Empower upon fulfillment.
     * @param acceptsMediaType     Content type to pass to the Accept header.
     * @return Byte array containing the binary data of the output file from the response
     */
    public byte[] fulfillOnDemandEmpowerDocumentWithContentResponse(String domain, String empowerDocumentId, String communicationId, String driverDataSource, boolean preserveDocuments, MediaType acceptsMediaType) {
        return fulfillOnDemandOutput(domain, empowerDocumentId, communicationId, driverDataSource, preserveDocuments, Optional.of(acceptsMediaType), byte[].class);
    }

    /**
     * Generate on-demand output from the Exstream Orchestration service
     * If the acceptsMediaType parameter is passed it will be included in an Accept header in the request
     * which will direct the Exstream Orchestration service to only return the content of the output file that matches
     * the specified media type.<br>
     * When acceptsMediaType is null, the full Orchestration response will be returned with all outputs.
     * Example: Passing "application/pdf" for the acceptsMediaType will get a raw PDF back in the response
     *
     * @param domain               Domain id to access.
     * @param communicationId      The id of the communication to generate output for.
     * @param driverDataSource     The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param empowerUser          The user that should be used to import documents into Empower
     * @param driverData           The driver file data that will be sent to the Exstream Orchestration service.
     * @param requestContentType   Content type of the driver data file.
     * @param acceptsMediaType     Content type to pass to the Accept header.
     * @param expectedResponseType Java type of the expected response data.
     * @return The response body
     */
    private <T> T generateOnDemandOutput(String domain, String communicationId, String driverDataSource, Optional<String> empowerUser, Object driverData, MediaType requestContentType, Optional<MediaType> acceptsMediaType, Class<T> expectedResponseType) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getServiceClientToken()));

        // Add headers to indicate which communication to generate output for
        headers.add(COMMUNICATION_ID_HEADER, communicationId);
        headers.add(DRIVER_DATA_SOURCE_HEADER, driverDataSource);
        acceptsMediaType.ifPresent(mediaType -> headers.setAccept(List.of(mediaType)));
        empowerUser.ifPresent(user -> headers.add(EMPOWER_USER_HEADER, user));

        // Add the driver data as the body
        headers.setContentType(requestContentType);
        HttpEntity<?> entity = new HttpEntity<>(driverData, headers);

        // Get the URI
        final URI uri = uriBuilder.buildOnDemandGenerateUri(domain);

        // Send the request
        logger.info("Requesting output from Exstream orchestration service: {}", uri);
        ResponseEntity<T> response = restTemplate.exchange(uri, HttpMethod.POST, entity, expectedResponseType);

        // Log response code
        logger.info("Orchestration response: {}", response.getStatusCode());

        T responseBody = Objects.requireNonNull(response.getBody());
        if (expectedResponseType != byte[].class) {
            logger.debug("Response data:\n{}", responseBody);
        } else {
            logger.debug("Response data: byte[] with length {}", ((byte[]) responseBody).length);
        }

        return responseBody;
    }

    /**
     * Fulfill an Empower document with the Exstream Orchestration on-demand service
     * If the acceptsMediaType parameter is passed it will be included in an Accept header in the request
     * which will direct the Exstream Orchestration service to only return the content of the output file that matches
     * the specified media type.<br>
     * When acceptsMediaType is null, the full Orchestration response will be returned with all outputs.
     * Example: Passing "application/pdf" for the acceptsMediaType will get a raw PDF back in the response
     *
     * @param domain               Domain id to access.
     * @param empowerDocumentId    The id of the Empower document to fulfill
     * @param communicationId      The id of the fulfillment communication to generate output for.
     * @param driverDataSource     The prodDsn value of the driver file associated with the communication (retrieve this value from the communication set associated with the communication).
     * @param preserveDocuments    Flag to indicate whether to preserve the Empower document after fulfillment. Setting
     *                             this to false will remove the document from Empower upon fulfillment.
     * @param acceptsMediaType     Content type to pass to the Accept header.
     * @param expectedResponseType Java type of the expected response data.
     * @return The response body
     */
    private <T> T fulfillOnDemandOutput(String domain, String empowerDocumentId, String communicationId, String driverDataSource, boolean preserveDocuments, Optional<MediaType> acceptsMediaType, Class<T> expectedResponseType) {
        // Build request
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(Objects.requireNonNull(otdsService.getServiceClientToken()));

        // Add headers to indicate which communication to generate output for
        headers.add(COMMUNICATION_ID_HEADER, communicationId);
        headers.add(DRIVER_DATA_SOURCE_HEADER, driverDataSource);
        headers.add(PRESERVE_DOCUMENTS_HEADER, Boolean.toString(preserveDocuments));
        acceptsMediaType.ifPresent(mediaType -> headers.setAccept(List.of(mediaType)));

        // Add the driver data as the body
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> entity = new HttpEntity<>(new EmpowerFulfillmentRequestBody(List.of(empowerDocumentId)), headers);

        // Get the URI
        final URI uri = uriBuilder.buildOnDemandFulfillmentUri(domain);

        // Send the request
        logger.info("Requesting fulfillment output from Exstream orchestration service: {}", uri);
        ResponseEntity<T> response = restTemplate.exchange(uri, HttpMethod.POST, entity, expectedResponseType);

        // Log response code
        logger.info("Orchestration response: {}", response.getStatusCode());

        T responseBody = Objects.requireNonNull(response.getBody());
        if (expectedResponseType != byte[].class) {
            logger.debug("Response data:\n{}", responseBody);
        } else {
            logger.debug("Response data: byte[] with length {}", ((byte[]) responseBody).length);
        }

        return responseBody;
    }
}
