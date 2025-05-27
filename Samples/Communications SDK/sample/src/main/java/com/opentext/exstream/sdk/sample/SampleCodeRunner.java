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

package com.opentext.exstream.sdk.sample;

import com.opentext.exstream.sdk.exstreamApi.model.dto.*;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.WorkflowState;
import com.opentext.exstream.sdk.exstreamApi.model.response.ImportResponse;
import com.opentext.exstream.sdk.exstreamApi.service.*;
import com.opentext.exstream.sdk.exstreamApi.utils.ExstreamApiUtils;
import com.opentext.exstream.sdk.sample.utils.SampleClassPathResources;
import com.opentext.exstream.sdk.sample.utils.SampleUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;

import java.io.File;
import java.util.*;

@SpringBootApplication(
        scanBasePackages = {"com.opentext.exstream.sdk" }
)
public class SampleCodeRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${sampleApp.domain}")
    String domain;
    @Value("${sampleApp.empower.user}")
    String empowerUser;

    @Autowired
    DasService dasService;
    @Autowired
    OrchestrationService orchestrationService;
    @Autowired
    EmpowerService empowerService;
    @Autowired
    OtdsService otdsService;

    public static void main(String[] args) {
        SpringApplication.run(SampleCodeRunner.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("domain={}", domain);
        logger.info("empowerUser={}", empowerUser);
        // Validate that we can connect to all services before we start
        verifyConnections();

        // To run specific samples, call the functions that execute them here
        listDasDomains();
        importToDasExample1();
        importToDasExample2();
        importToDasExample3();
        importToDasExample4();
        listDasResources();
        listDasCommunications();
        listCommunicationsWithEmpowerQueues();
        changeWorkflowForDasResource();
        uploadNewContentForDasResource();
        uploadNewDriverFileToDas();
        getCommunicationSetIdForCommunicationInDas();
        getCommunicationsForSampleFileInDas();
        getDriverFileForCommunicationSetInDas();
        containsEmpowerOutputQueueInCommunicationSet();
        generateOutputWithOrchestrationFullResponse();
        generateOutputWithOrchestrationPdfOnly();
        generateEmpowerDocumentWithOrchestrationFullResponse();
        generateEmpowerDocumentWithOrchestrationDocumentIdOnly();
        fulfillEmpowerDocumentWithOrchestrationFullResponse();
        fulfillEmpowerDocumentWithOrchestrationPdfOnly();

        logger.info("Finished running sample code. Web server will continue to run until killed.");
        logger.info("Sample web page with Empower iframe example: http://localhost:8080");
    }

    private void verifyConnections() {
        // Verify OTDS connection
        otdsService.getToken();

        // Verify DAS connection
        System.out.println("Connected to DAS, version: " + dasService.getVersion().getServiceVersion());

        // Verify Orchestration connection
        System.out.println("Connected to Orchestration, version: " + orchestrationService.getVersion().getServiceVersion());

        // Verify Empower connection
        System.out.println("Connected to Empower, version: " + empowerService.getVersion().getServiceVersion());
    }

    //region Common Requests

    // Get a list of domains from DAS
    private void listDasDomains() {
        // Call listDomains and print the results
        Collection<DasDomain> dasDomains = dasService.listDomains();
        System.out.println("DAS domain list:");
        dasDomains.forEach(d -> System.out.println(d.toString()));
    }

    // Import an export file into DAS - Example 1
    private void importToDasExample1() {
        importToDas(domain, SampleClassPathResources.EXPORT_PACKAGE_CLAIM_ACKNOWLEDGEMENT);
    }

    // Import an export file into DAS - Example 2
    private void importToDasExample2() {
        importToDas(domain, SampleClassPathResources.EXPORT_PACKAGE_CLAIM_ESTIMATE_LETTER);
    }

    // Import an export file into DAS - Example 3
    private void importToDasExample3() {
        importToDas(domain, SampleClassPathResources.EXPORT_PACKAGE_GENERIC_EMPOWER_FULFILLMENT);
    }

    // import an export file into DAS - Example 4
    private void importToDasExample4() {
        importToDas(domain, SampleClassPathResources.EXPORT_PACKAGE_SAMPLE_FILE_FOR_WORKFLOW_AND_UPDATE);
    }

    // Helper method for importing to DAS and printing the results
    private void importToDas(String domain, ClassPathResource exportFile) {
        // Import the export file
        ImportResponse response = dasService.importPackage(domain, exportFile);

        // Print the resources that were imported
        System.out.println("Resources imported from " + exportFile.getFilename() + ":");
        response.importedResources.forEach(r -> System.out.println(r.toString() + "\n"));
        System.out.println("Total imported resources: " + response.importedResources.size() + "\n");
        System.out.println("Existing resources, not imported:");
        response.existingResources.forEach(r -> System.out.println(r.toString() + "\n"));
        System.out.println("Total existing resources: " + response.existingResources.size() + "\n");
        System.out.println("Conflicted resources imported:");
        response.conflictedResources.forEach(r -> System.out.println(r.toString() + "\n"));
        System.out.println("Total conflicted resources: " + response.conflictedResources.size() + "\n");
        System.out.println("Ignored resources during import:");
        response.ignoredResources.forEach(r -> System.out.println(r.toString() + "\n"));
        System.out.println("Total ignored resources: " + response.ignoredResources.size() + "\n");
    }

    // Get a list of resources from DAS
    private void listDasResources() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Set up the ResourceFilter for the list.
        // Get the latest approved version of resources in DAS
        ResourceFilter filter = new ResourceFilter()
                .setLatestVersion(true)              // gets the latest version of the resources
                .addState(WorkflowState.APPROVED);   // gets the approved version of the resources

        // Call listResources and print the results
        Collection<DasResourceVersion> resources = dasService.listResources(domain, filter);
        resources.forEach(r -> System.out.println(r.toString() + "\n"));
    }

    // Get a list of "Communications" from DAS
    private void listDasCommunications() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Set up the ResourceFilter for the list.
        // Get the latest approved version of extrapplication resources in DAS
        ResourceFilter filter = new ResourceFilter()
                .setTypes(List.of(ResourceType.exstrapplication)) // Communications have type "exstrapplication" in DAS
                .setLatestVersion(true)                           // gets the latest version of the resources
                .addState(WorkflowState.APPROVED);                // gets the approved version of the resources

        // Call listResources and print the results
        Collection<DasResourceVersion> resources = dasService.listResources(domain, filter);
        //resources.forEach(r -> System.out.println(r.toString() + "\n"));
    }

    private void listCommunicationsWithEmpowerQueues(){
        ResourceFilter filter = new ResourceFilter()
                .setTypes(List.of(ResourceType.exstrcommunicationset)) // Communication sets have type "exstrcommunicationset" in DAS
                .setLatestVersion(true)                           // gets the latest version of the resources
                .addState(WorkflowState.APPROVED);                // gets the approved version of the resources

        // Get the list of communication sets
        Collection<DasResourceVersion> resources = dasService.listResources(domain, filter);

        // Remove any communication sets that do not have an Empower output
        resources.removeIf(r -> dasService.getManifestForCommunicationSet(domain, r.id.toString()).hasEmpowerOutput() == false );
        System.out.println("The following communication sets have Empower Output : ");
        resources.forEach(r -> System.out.println(r.name + " - " + r.id.toString()));

        // Setup the filter to get communications that are associated with the remaining communication sets
        int linkDepth = 2;
        ResourceFilter resourceFilter = new ResourceFilter()
                .addRfilterType(ResourceType.exstrapplication);

        Collection<DasResourceVersion> communications = new ArrayList<>();

        resources.forEach(r -> {
            // Get the communications that are associated with the remaining communication sets
            Collection<DasResourceVersion> communicationSetLinks = dasService.recursiveListLinks(domain, r.id.toString(), linkDepth, resourceFilter);
            communications.addAll(communicationSetLinks);
        });

        System.out.println("Found the following communications have Empower output : ");
        communications.forEach(communication -> {
            // Print results
            System.out.println(communication.name + " - " + communication.id.toString());
        });

    }

    // Move a resource to a new workflow state in DAS
    // Note: if this method is run multiple times, changing a draft to a draft is a no-op and will succeed.
    private void changeWorkflowForDasResource() {
        // Pre-req: Ensure the sample file exists. This import is optional if it already does.
        // Uncomment the line below to run the necessary import or run importToDasExample4 before this method
        // importToDasExample4();

        // Call changeWorkflowState on the sample file that was imported by specifying its id
        final String resourceId = "9c6f9a92-fa99-4a63-81e3-b6ad2200f5e9";
        DasResourceVersion resourceVersion = dasService.changeWorkflowState(domain, resourceId, WorkflowState.DRAFT, "A comment about this workflow change", false);

        // Print the resource
        System.out.println("Changed workflow to DRAFT on resource " + resourceId + ":");
        System.out.println(resourceVersion.toString());
    }

    // Upload new content for a resource in DAS
    private void uploadNewContentForDasResource() {
        // Pre-req: Ensure the sample file exists and is in the draft state before attempting to update it.
        // Run the changeWorkflowForDasResource example before this one to ensure that it is.
        // changeWorkflowForDasResource();

        // Call updateResourceContent on the sample file that was imported by specifying its id
        final String resourceId = "9c6f9a92-fa99-4a63-81e3-b6ad2200f5e9";
        DasResourceVersion resourceVersion = dasService.updateResourceContent(domain, resourceId, SampleClassPathResources.UPLOAD_NEW_CONTENT_FOR_DAS_RESOURCE_JSON);

        // Print the resource
        System.out.println("Changed content of resource " + resourceId + ":");
        System.out.println(resourceVersion.toString());
    }

    // Upload new "driver file" into DAS
    private void uploadNewDriverFileToDas() {
        // Call createResource
        DasResourceVersion resourceVersion = dasService.createResource(domain, "UploadedSampleFile", ResourceType.samplefile, "driver", SampleClassPathResources.UPLOAD_NEW_DRIVER_FILE_TO_DAS_JSON);

        // Print the resource
        System.out.println("New driver file created:");
        System.out.println(resourceVersion.toString());
    }

    // Get the communication set id using the communication id
    private UUID getCommunicationSetIdForCommunicationInDas() {
        // Use the id of the communication whose communication set is being queried for
        return getCommunicationSetIdForCommunicationInDas("e0421aa8-c1ce-41d9-a10a-cb830ebd4beb");
    }

    // Get the communication set id using the communication id
    private UUID getCommunicationSetIdForCommunicationInDas(String communicationId) {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Create the link parameters
        final int linkDepth = 2; // The query depth. "2" should always get the communication set when using the communication id as the subject

        // Create the ResourceFilter
        // Only get linked resources that are the exstrcommunicationset type
        ResourceFilter resourceFilter = new ResourceFilter()
                                            .addType(ResourceType.exstrcommunicationset);
        // Call listLinks for the communication
        Collection<DasLink> links = dasService.listLinks(domain, communicationId, linkDepth, resourceFilter);

        // Print results
        System.out.println("Found the following communication sets linked to the communication with id " + communicationId + ":");
        links.forEach(l -> System.out.println(l.linkObjectId));

        // Assuming only one communication set for a communication in the samples
        return links.stream().findFirst().map(l -> l.linkObjectId).orElse(null);
    }

    // Get a list of communications that use the specified sample file
    private List<UUID> getCommunicationsForSampleFileInDas() {
        return getCommunicationsForSampleFileInDas("a9c91f24-1158-4e0b-a458-5d483f0a8597");
    }

    // Get a list of communications that use the specified sample file
    private List<UUID> getCommunicationsForSampleFileInDas(String sampleFileId) {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Create the link parameters
        final int communicationLinkDepth = 10; // The query depth. "10" should be more than sufficient to get the communication when using the sample file id as the object

        // Create the ResourceFilter
        ResourceFilter resourceFilter = new ResourceFilter()
                .addRfilterState(WorkflowState.APPROVED)
                .addRfilterType(ResourceType.exstrapplication);

        // Call recursiveListLinks for the sample file
        Collection<DasResourceVersion> sampleFileLinks = dasService.recursiveListLinks(domain, sampleFileId, communicationLinkDepth, resourceFilter);
        List<UUID> communicationIds = new ArrayList<UUID>();
        sampleFileLinks.forEach(l -> {
            if (l.type == ResourceType.exstrcommunicationset) {
                communicationIds.add(l.id);
            }
        });

        System.out.println("Found the following communications linked to the sample file with id " + sampleFileId + ":");
        communicationIds.forEach(l -> System.out.println(l.toString()));

        // Return the list of Communication Ids linked to the sample file
        return communicationIds;
    }

    // Get the driver file using the communication set id
    private String getDriverFileForCommunicationSetInDas() {
        // Pass the communication set id to get the driver file for
        return getDriverFileForCommunicationSetInDas("619bd602-05de-4c30-829b-7ed074263dff");
    }

    // Get the driver file using the communication set id
    private String getDriverFileForCommunicationSetInDas(String communicationSetId) {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Call getManifestForCommunicationSet
        DasManifest manifest = dasService.getManifestForCommunicationSet(domain, communicationSetId);

        // Print the driver file name
        if (manifest.dsnlist == null || manifest.dsnlist.isEmpty()) {
            System.out.println("No driver files found for communication set with ID " + communicationSetId);
            return null;
        } else {
            System.out.println("Driver file for communication set ID " + communicationSetId + ":");
            final DasDataSource driverFile = manifest.dsnlist.get(0);
            System.out.println(driverFile.toString());
            return driverFile.prodDsn;
        }
    }

    private boolean containsEmpowerOutputQueueInCommunicationSet() {
        return containsEmpowerOutputQueueInCommunicationSet("c9f0104b-036e-414e-9d3f-2ec0a5ed3ae6");
    }

    private boolean containsEmpowerOutputQueueInCommunicationSet(String communicationSetId) {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 before this method
        // importToDasExample1();

        // Call getManifestForCommunicationSet
        DasManifest manifest = dasService.getManifestForCommunicationSet(domain, communicationSetId);

        boolean hasEmpowerOutput = manifest.hasEmpowerOutput();
        if (hasEmpowerOutput) {
            System.out.println("Communication set " + communicationSetId + " contains an Empower output queue");
        } else {
            System.out.println("Communication set " + communicationSetId + " does not contain an Empower output queue");
        }

        return hasEmpowerOutput;
    }

    // Generate output using Orchestration - Full response
    private void generateOutputWithOrchestrationFullResponse() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 and importToDasExample2 before this method
        // importToDasExample1();
        // importToDasExample2();

        final String communicationId = "e0421aa8-c1ce-41d9-a10a-cb830ebd4beb";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());

        // Call generateOnDemandOutputWithFullResponse to get the full response from Orchestration
        List<OrchestrationResponseData> responseDataList = orchestrationService.generateOnDemandOutputWithFullResponse(domain, communicationId, driverFileDsn, SampleClassPathResources.CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_PDF_OUTPUT, MediaType.APPLICATION_JSON);

        // Print the response data. Also save the file to ./output
        if (responseDataList.isEmpty()) {
            System.out.println("No output files generated for communication with ID " + communicationId);
        } else {
            System.out.println("Output files generated by Exstream Orchestration:");
            responseDataList.forEach(r -> {
                System.out.println(r.toString().trim());
                final File outputFile = SampleUtils.saveContentToOutputFolder(String.join(".", List.of(r.getFileName(), r.getFileExtension())), r.getContent());
                System.out.println("Local file: \"" + outputFile.getAbsolutePath() + "\"");
            });
        }
    }

    // Generate output using Orchestration - PDF Only
    private void generateOutputWithOrchestrationPdfOnly() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 and importToDasExample2 before this method
        // importToDasExample1();
        // importToDasExample2();

        final String communicationId = "e0421aa8-c1ce-41d9-a10a-cb830ebd4beb";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());

        // call generateOnDemandOutputWithContentResponse. To get PDF only output, you use the overload of the method that accepts the media type to
        // use for the Accept header to limit the type of output returned. This will also return the content directly instead of
        // it being contained in an OrchestrationResponseData.
        byte[] pdfBytes = orchestrationService.generateOnDemandOutputWithContentResponse(domain, communicationId, driverFileDsn, SampleClassPathResources.CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_PDF_OUTPUT, MediaType.APPLICATION_JSON, MediaType.APPLICATION_PDF);

        // Print the response data. Also save the file to ./output
        System.out.println("PDF file generated by Exstream Orchestration:");
        final File outputFile = SampleUtils.saveContentToOutputFolder("generateOutputWithOrchestrationPdfOnlyExample.pdf", pdfBytes);
        System.out.println("Saved file: \"" + outputFile.getAbsolutePath() + "\"");
    }

    // Generate Empower document using Orchestration - Full response
    private void generateEmpowerDocumentWithOrchestrationFullResponse() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 and importToDasExample2 before this method
        // importToDasExample1();
        // importToDasExample2();

        final String communicationId = "243611df-7f67-4816-b1c7-5f6617d5afa1";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());

        // Call generateOnDemandEmpowerOutputWithFullResponse to get the full response from Orchestration
        List<OrchestrationResponseData> responseDataList = orchestrationService.generateOnDemandEmpowerOutputWithFullResponse(domain, communicationId, driverFileDsn, empowerUser, SampleClassPathResources.CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_EMPOWER_OUTPUT, MediaType.APPLICATION_JSON);

        // Print the response data.
        if (responseDataList.isEmpty()) {
            System.out.println("No output files generated for communication with ID " + communicationId);
        } else {
            System.out.println("Output files generated by Exstream Orchestration:");
            responseDataList.forEach(r -> {
                System.out.println(r.toString().trim());
                System.out.println("Empower doc id: " + ExstreamApiUtils.readEmpowerOutputChannelContent(r.getContent()).documentId);
            });
        }
    }

    // Generate Empower document using Orchestration - Document ID only
    private String generateEmpowerDocumentWithOrchestrationDocumentIdOnly() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample1 and importToDasExample2 before this method
        // importToDasExample1();
        // importToDasExample2();

        final String communicationId = "243611df-7f67-4816-b1c7-5f6617d5afa1";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());

        // Call generateOnDemandEmpowerOutputDocumentIdResponse to get the full response from Orchestration
        EmpowerOutputChannelContent responseData = orchestrationService.generateOnDemandEmpowerOutputDocumentIdResponse(domain, communicationId, driverFileDsn, empowerUser, SampleClassPathResources.CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_EMPOWER_OUTPUT, MediaType.APPLICATION_JSON);

        // Print the document id
        System.out.println("Empower document id: " + responseData.documentId);

        return responseData.documentId;
    }

    // Fulfill Empower output using Orchestration - Full response
    private void fulfillEmpowerDocumentWithOrchestrationFullResponse() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample2 and importToDasExample3 before this method
        // importToDasExample2();
        // importToDasExample3();

        final String communicationId = "21f9a9fe-b363-4f32-8303-6a8bf5f7009e";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());
        final String empowerDocumentId = generateEmpowerDocumentWithOrchestrationDocumentIdOnly();

        // Call fulfillOnDemandEmpowerDocumentWithFullResponse to get the full response from Orchestration
        List<OrchestrationResponseData> responseDataList = orchestrationService.fulfillOnDemandEmpowerDocumentWithFullResponse(domain, empowerDocumentId, communicationId, driverFileDsn, false);

        // Print the response data.
        if (responseDataList.isEmpty()) {
            System.out.println("No output files generated for communication with ID " + communicationId);
        } else {
            System.out.println("Output files generated by Exstream Orchestration:");
            responseDataList.forEach(r -> {
                System.out.println(r.toString().trim());
                final File outputFile = SampleUtils.saveContentToOutputFolder(String.join(".", List.of(r.getFileName(), r.getFileExtension())), r.getContent());
                System.out.println("Local file: \"" + outputFile.getAbsolutePath() + "\"");
            });
        }
    }

    // Fulfill Empower output using Orchestration - PDF only
    private void fulfillEmpowerDocumentWithOrchestrationPdfOnly() {
        // Pre-req: Ensure there are resources in DAS. This import is optional if resources already exist.
        // Uncomment the line below to run the necessary import or run importToDasExample2 and importToDasExample3 before this method
        // importToDasExample2();
        // importToDasExample3();

        final String communicationId = "21f9a9fe-b363-4f32-8303-6a8bf5f7009e";
        final UUID commSetId = getCommunicationSetIdForCommunicationInDas(communicationId);
        final String driverFileDsn = getDriverFileForCommunicationSetInDas(commSetId.toString());
        final String empowerDocumentId = generateEmpowerDocumentWithOrchestrationDocumentIdOnly();

        // call fulfillOnDemandEmpowerDocumentWithContentResponse. To get PDF only output, you use the overload of the
        // method that accepts the media type to use for the Accept header to limit the type of output returned.
        // This will also return the content directly instead of it being contained in an OrchestrationResponseData.
        byte[] pdfBytes = orchestrationService.fulfillOnDemandEmpowerDocumentWithContentResponse(domain, empowerDocumentId, communicationId, driverFileDsn, false, MediaType.APPLICATION_PDF);

        // Print the response data. Also save the file to ./output
        System.out.println("PDF file generated by Exstream Orchestration:");
        final File outputFile = SampleUtils.saveContentToOutputFolder("fulfillEmpowerDocumentWithOrchestrationPdfOnlyExample.pdf", pdfBytes);
        System.out.println("Saved file: \"" + outputFile.getAbsolutePath() + "\"");
    }

    //endregion
}
