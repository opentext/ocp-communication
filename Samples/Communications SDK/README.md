# Core Communications SDK in Java

## Open in IDE

To work with the Core Communications SDK Sample code:

1.  Open your IDE.
2.  Import the sample code folder as a new project.
3.  Build the sample code project

**Note:** The sample code project was developed in IntelliJ IDEA, using Java 17.

## Change application properties

Edit the file located at sample/src/main/resources/application.properties and update the following values:

- **otds.url**— Update with the address of your OTDS instance. When you try to login to your tenant the EXS_OTDS_URL will be the portion of the URL before **/otdstenant**.
- **otds.tenant**—Update with the portion of the login URL that starts after **/otdstenant/** and completes just before **/login**.
- **otds.username**— Update with the username of a user with the tenantadmin role for your Core Communications instance.
- **otds.password**— Update with the password for your user with the tenantadmin role.
- **otds.subscription.name**—Update with the name of your subscription in OCP.
- **otds.serviceClientId**-Update with the client ID of an existing Service Client with the tenantadmin role. Refer to the OpenText Admin Center online help for instructions on creating service clients.
- **otds.serviceClientSecret**-Update with the client secret of an existing Service Client with the tenantadmin role. Refer to the OpenText Admin Center online help for instructions on creating service clients.
- **exstream.url**—Update with the address of your Core Communications instance. When you have logged in to your Core Communications instance the exstream.url will be the portion of the URL before **/design**.
- **sampleApp.domain**—Update with the Core Communications domain that you will be using for your work. The domain is the string under your username in the upper left of the Core Communications UI after you have logged in and selected a domain.
- **sampleApp.empower.user**— Update with the client ID of an existing Service Client with the empower_integrator role for your Core Communications instance. Refer to the OpenText Admin Center online help for instructions on creating service clients.


## How to authenticate

The Core Communications API uses OAuth2 tokens provided by OTDS to authenticate users. Each method in the Core Communications API has an **Authorization** header that contains a bearer token returned from OTDS. In addition, you can create API service credentials in Admin Center. You might use API service credentials when you don't want to authenticate using a user/password. In the SDK we provide examples of the orchestration service using this authentication method.

The Core Communications SDK includes classes to handle OTDS authentication for the sample application. The OTDS related classes are simplified to make reading the code easier. You should reference the [OTDS SDK](https://developer.opentext.com/products_services/bb4a87a8-62f4-4f81-a16e-2a258e7a51f1) for examples of properly implementing OTDS authentication in your application.

The OTDS related classes in the Core Communications SDK cache the token returned from OTDS to make the code simpler. If your debugging session lasts longer than the token timeout (15 minutes by default) you will need to restart the sample application to get a new token.

The OTDS related classes in the Core Communications SDK use the variables that were setup in the [Change_application_properties](#_Change_application_properties) section to request an authentication token from OTDS. If you are seeing authentication errors verify that you have set the application properties correctly.

In your application you will need to request the OTDS token and pass it in the headers of each request.

## Core Communications API

The Core Communications SDK sample code is divided into two sections, the Core Communications API and the Java sample code.

We encourage you to use the Core Communications API portion of the sample code in your own project, after replacing the OTDS related classes as described in the [How_to_authenticate](#how-to-authenticate) section. Using the Core Communications API portion of the sample code will save implementation time and allow for better support for your integration.

## Core Communications Sample Code

The Core Communications SDK Sample code demonstrates how to use the Core Communications API to access the features of Core Communications. The run method in SampleCodeRunner.java contains method calls to each of the samples provided in the SDK. The sample code can be executed with the command **gradlew bootRun** from the command line, but the intent is to run these samples in a debugger.

The Core Communications SDK Sample code also includes an embedded web server that demonstrates how to host Interactive Editor in an iframe. The web server is started automatically when you execute the sample code.

## Domains

### What domains do

Domains are a method to group logically related sets of resources in the Dynamic Asset Service (DAS). Domains are often used to separate data sets for the development and production of communications. Domains can also be used to separate communications by business group (for example, Sales, Marketing, Customer Service). For more information about domains, see [Understanding tenants and domains](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/ccwc/v220400/ccwc-ugd/en/html/jsframe.htm?understandingtenantsanddomains) in the Core Communications Web Client help.

### How to get the list of domains

Many requests to DAS require you to pass the domain that a resource is part of. The **SampleCodeRunner::listDasDomains** method demonstrates how to get a list of domains using the Core Communications API. The **listDasDomains** method uses the **DasService::listDomains** method from the Core Communications API. The **DasService::listDomains** method does not require any parameters and returns a Collection of **DasDomain** objects. **DasDomain** objects are POJOs (Plain old Java Objects) that encapsulate the data relating to a domain in DAS. If domains are being used to separate data sets for development and production the **production** variable will indicate if a domain is intended for production use.

## Communications

### What makes up a communication

Even the simplest communication in Core Communications is made up of several different resources. Most of these resources will only be relevant to the communication designer, so this focuses on the resources that are needed for producing output with Core Communications. The key resources for integrating with Core Communications are the communication, the communication set, and the driver file.

### Importing communications

One of the ways that communications can be loaded into Core Communications is through export packages. Export packages allow users to transfer communications developed in Core Communications CE into another tenant for use. The Core Communications SDK provides export packages for two sample communications to use in your development. There are also two additional export packages for other examples.

**Note:** The sample export packages are located in the \<*project directory*\>/sample/src/main/resources/samples folder.

The **SampleCodeRunner::importToDas** method shows how to use the **DasService::importPackage** method to import export packages into DAS. The **DasService::importPackage** method requires the domain that the import will be loaded into, and the contents of the export ZIP file. The sample code uses a default conflict policy of **SKIP** if there is a conflict detected during the import process. The Core Communications API has additional methods that allow you to specify alternate conflict handling scenarios. The sample code also uses a default value of **true** for the **commit** value of the import operation. The Core Communications API has additional methods that allow you to set **commit** to **false** if you want to do test imports without committing the changes. The **importPackage** method returns an **ImportResponse** object. The **ImportResponse** object is a POJO (Plain Old Java Object) that contains the results of the import operation.

The **SampleCodeRunner** will import four different export packages that provide resources that are used by the other examples in the sample code. The methods **importToDasExample1**, **importToDasExample2**, **importToDasExample3**, and **importToDasExample4** each make use of the **importToDas** method to import one of the sample export packages. You will need to import all four files to use all of the examples in the sample code.

### Getting a list of communications

The **SampleCodeRunner::listDasCommunications** method shows how to get a list of communications from DAS. This request requires the domain that you are querying and a filter that limits the list of resources that is returned.

To filter for **Communications** you will need to create a **ResourceFilter** object and use the **setTypes** method to a **List** containing the type **ResourceType.exstrapplication**. DAS uses the type **exstrapplication** to identify communication objects. See the Swagger page—{{exstream.url}}/design/swagger-ui/index.html\#/resources-controller/getResources— or the **ResourceType** enumeration for a full list of valid types.

For most integrations, you will want to see the list of communications that have successfully completed the approval workflow, and you will only want to see the most recent version that has been approved. This can be accomplished by calling the **ResourceFilter::addState** method with the value **WorkflowState.APPROVED** and calling **ResourceFilter::setLatestVersion** method with the value of **true**.

DAS can contain a large number of resources, so requests like **DasService::listResources** have parameters that allow you to control the pagination of results. The **PageInfo::setCount** method is used to set the number of resources returned in a request. The **PageInfo::setOffset** is used to determine which page of results to return. The **PageInfo::offset** value is **0** indexed, so the first page of results is requested by calling **setOffset** with a value of **0**.

If you do not see any results, then follow the directions in the [Importing_communications](#_Importing_communications) section to call the **SampleCodeRunner::importToDasExample1** and **SampleCodeRunner::importToDasExample2** methods.

### Getting a list of communications associated with a sample file

In your user interface you may want to display a list of communications that are associated with a given sample file; see the [Data mapping](#data-mapping) section for more details.  The **Get a list of communications associated with a sample file** request demonstrates how to get a list of communications associated with a sample file. This request requires the domain that you are querying and a filter that limits the list of resources that is returned.

To filter for **Communications** you will need to create a **ResourceFilter** object and use the **addRfilterType** method to add the type **ResourceType.exstrapplication**. DAS uses the type **exstrapplication** to identify communication objects. See the Swagger page—{{exstream.url}}/design/swagger-ui/index.html\#/resources-controller/getResources— or the **ResourceType** enumeration for a full list of valid types.

For most integrations, you will want to see the list of communications that have successfully completed the approval workflow, and you will only want to see the most recent version that has been approved. This can be accomplished by calling the **ResourceFilter::addRfilterState** method with the value **WorkflowState.APPROVED**.

DAS can contain a large number of resources, so requests like **DasService::listResources** have parameters that allow you to control the pagination of results. The **PageInfo::setCount** method is used to set the number of resources returned in a request. The **PageInfo::setOffset** is used to determine which page of results to return. The **PageInfo::offset** value is **0** indexed, so the first page of results is requested by calling **setOffset** with a value of **0**.

If you do not see any results, then follow the directions in the [Importing communications](#importing-communications) section to call the **SampleCodeRunner::importToDasExample1** and **SampleCodeRunner::importToDasExample2** methods.

### Getting a list of communications that produces a specified output type

In your user interface you may need to find communications that produce a certain output type. For example, you may want to find and display communications that produce Interactive output. This will require multiple API calls to retrieve communications that produce that output. The **SampleCodeRunner::listDasCommunicationsWithEmpowerQueues** demonstrates a set of calls for retrieving such a list.

The **SampleCodeRunner::containsEmpowerOutputQueueInCommunicationSet** utilizes the **DasService::getManifestForCommunicationSet** method to return a **DasManifest** object associated with the specified communication set that contains a list of **DasQueue** objects. The **DasQueue** object contains information about an individual queue used in a communication set. The **DasQueue** object possesses a property called **driver** which is used to contain the output type associated with that **DasQueue**.


## Resources

To learn more about resources in Core Communications, see [Viewing resources in asset library views](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/ccwc/v220400/ccwc-ugd/en/html/jsframe.htm?usingassetlibraryviews) in the Core Communications Web Client help.

### Getting resources through API

There are times when you may need to interact with non-communication resource objects in DAS. The most common object that you will need to interact with is a driver file which provides an example of the layout of the data that your integration will send to Core Communications when you produce output (see [Generating_output](#_Generating_output) for more information on driver files). DAS has a common set of methods for interacting with all resources that are stored in DAS. The method **DasService::listResources** is used to get a list of resources from DAS.

For most integrations, you will want to see the list of resources that have successfully completed the approval workflow, and you will only want to see the most recent version that has been approved. This can be accomplished by creating a **ResourceFilter** object and calling the **ResourceFilter::addState** method with the value **WorkflowState.APPROVED** and calling **ResourceFilter::setLatestVersion** method with the value of **true**.

If you want to get a particular type of resource from DAS, you can call the **ResourceFilter::setTypes** method with one of the values as described on the Swagger page: {{exstream.url}}/design/swagger-ui/index.html\#/resources-controller/getResources — or the **ResourceType** enumeration for a full list of valid types.

DAS can contain a large number of resources, so requests like **DasService::listResources** have parameters that allow you to control the pagination of results. The **PageInfo::setCount** method is used to set the number of resources returned in a request. The **PageInfo::setOffset** is used to determine which page of results to return. The **PageInfo::offset** value is 0 indexed, so the first page of results is requested by calling **setOffset** with a value of **0**.

If you do not see any results, then follow the directions in the [Importing_communications](#_Importing_communications) section to call the **SampleCodeRunner::importToDasExample1** and **SampleCodeRunner::importToDasExample2** methods.

### Workflow management

DAS uses approval workflows to ensure that work-in-progress materials are properly reviewed prior to being used in production output. DAS has three workflow types: DAS has three workflow types: Standard, Simple and Advanced (see [Managing approval workflows](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/ccwc/v220400/ccwc-ugd/en/html/jsframe.htm?managingapprovalworkflows) in the Core Communications Web Client help. DAS allows integrators to move objects through the workflow process using the API. The **DasService::changeWorkflowState** method is used to move objects through a workflow process. In the standard workflow the valid state values are DRAFT, REVIEW, APPROVED, and REJECTED. In the simplified workflow the valid state values are DRAFT and APPROVED. Moving an object from APPROVED to DRAFT creates a new version of the object. Valid state changes for the standard workflow are DRAFT to REVIEW, REVIEW to APPROVED, APPROVED to DRAFT, REVIEW to REJECTED, and REJECTED to DRAFT.

If you do not see a successful result, then follow the directions in the [Importing_communications](#_Importing_communications) section to run the request **Import an export package into DAS - Example 4**.


### Updating resources

To update a resource in DAS the resource must be in the DRAFT state (see [Workflow_management](#_Workflow_management)). Once a resource is in the DRAFT state, the **DasService::updateResourceContent** method can be used to upload new content for the resource. The **DasService::updateResourceContent** method requires the domain name and resource ID of the resource being updated, and the new content for the resource.

If you do not see a successful result, then follow the directions in the [Workflow management](#_Workflow_management) section to import the base data and set the correct workflow state.

After updating the resource, you can use the **DasService::changeWorkflowState** method to move the resource through the workflow.

## Generating output

The goal of integrating with Core Communications is to generate customized output.

### Data mapping

To generate customized output, the data in your sample file must be mapped to variables in Core Communications. See [Configuring data sources](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/ccwc/v220400/ccwc-ugd/en/html/jsframe.htm?managingdatasources) in the Core Communications Web Client help.

You may want to generate the driver file from your application. The **DasService::createResource** method is used to create a new driver file resource in DAS. The **DasService::createResource** method requires the domain that the resource should be created in, the name you want assigned to the resource, the resource type, the resource sub-type, and the contents that will be assigned to the resource. The **resource type** parameter must be **ResourceType.samplefile**, and the **subtype** parameter must be the string **driver**.

### Driver data source

The methods to generate output from Core Communications require a **driverDataSource** parameter. To get the driver data source for a communication use the communication ID that was returned from the requests detailed in the [Getting a list of communications](#getting-a-list-of-communications) section, then use the **DasService::listLinks** method to get the communication set ID and then use the **DasService::getManifestForCommunicationSet** method to get the driverDataSource name.

The **DasService::listLinks** method requires the domain, the ID of the subject resource, the version of the subject resource, the link depth, and a **ResourceFilter**. Links in DAS are unidirectional, from the subject to an object. To filter for **“Communication sets”** you will need to create a **ResourceFilter** object and use the **setTypes** method to a **List** containing the type **ResourceType.exstrcommunicationset**. Using the filters supplied in the example, **SampleCodeRunner::getCommunicationSetIdForCommunicationInDas**, there will only be one element returned in the **Collection** of **DasLink** objects. A **DasLink** object is a POJO (Plain Old Java Object) that contains the information about the returned links. The **DasLink.linkSubjectId** should be the same ID that was submitted in the request, and the **DasLink.linkObjectId** is the ID of the communication set that we are interested in. See [Setting up data sources](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/ccwc/v220400/ccwc-ugd/en/html/jsframe.htm?settingupdatasources) in the Core Communications Web Client help for more details on driver data sources.

If you do not see a successful result, then follow the directions in the Importing communications section to run the request **SampleCodeRunner::ImportToDasSample1**.

After obtaining the communication set ID, the **DasService::getManifestForCommunicationSet** method is used to get the **driverDataSource** name. The **DasService::getManifestForCommunicationSet** method requires the domain and the communication set ID and returns a **DasManifest** object that contains a **List** of **DasDataSource** objects. The **DasDataSource** object is a POJO (Plain Old Java Object) that contains the information about data sources used by a communication. The value of **DataDataSource.prodDsn** member variable is the value that should be supplied when the **driverDataSource** is requested for a given communication.

### Generating PDF output

The Orchestration service handles producing the output from a communication. The **SampleCodeRunner::generateOutputWithOrchestrationFullResponse** and **SampleCodeRunner:: generateOutputWithOrchestrationPdfOnly** methods demonstrate how to interact with the Orchestration service to produce PDF output.

The **SampleCodeRunner::generateOutputWithOrchestrationFullResponse** makes use of the **OrchestrationService::generateOnDemandOutputWithFullResponse** method. The **OrchestrationService::generateOnDemandOutputWithFullResponse** method requires the domain, the communication id, the driver file DSN, the contents of the driver file, and the media type of the driver file. The driver file DSN can be obtained using the instructions in the [**Driver data source**](#_Driver_data_source) section. The contents of the driver file should have the same format as the driver file resource that was created using the instructions in the [Data_mapping](#_Data_mapping) section. The media type of the driver file should be **MediaType.APPLICATION_JSON** or **MediaType.APPLICATION_XML** depending on the format of the driver file that is being passed.

The **OrchestrationService::generateOnDemandOutputWithFullResponse** method returns a **List** of **OrchestrationResponseData** objects. The **OrchestrationResponseData** object is a POJO (Plain Old Java Object) that contains the data returned from the Orchestration service. The **OrchestrationResponseData::getFilename** method will provide the name of the output file as generated by Core Communications. The **OrchestrationResponseData::getFileExtension** method will return the three letter extension typically used on Windows systems for the type of file that is returned. The results of the **getFilename** and **getFileExtension** methods are usually concatenated together to form the full filename when writing an output file to disk. The **OrchestrationResponseData::getContent** method returns the Base64 encoded contents of the output file. You are responsible for Base64 decoding the contents before writing to disk.

The **OrchestrationService::generateOnDemandOutputWithContentResponse** method uses accept headers to return just the contents of the output file as the result of the output generation. The **OrchestrationService::generateOnDemandOutputWithContentResponse** method takes the same parameters as the **OrchestrationService::generateOnDemandOutputWithFullResponse** method above, but also takes an additional parameter that specifies the type of content that is expected to be returned. In almost all cases this will be **MediaType.APPLICATION_PDF**\*.\*

### Generating Interactive documents

Generating output using the Interactive Editor requires additional steps, but allows for communications that can include additional user input beyond what is provided in the driver input file. If you have an Interactive compatible communication, see [Designing for Empower](http://wlprodinfprd01.opentext.net/Docs/dir2kcLive/piroot/cccd/v220400/cccd-ugd/en/html/jsframe.htm?designingforempower) in the Core Communications Designer help, you need to generate an Interactive document that can be used by Interactive Editor. The **SampleCodeRunner::generateEmpowerDocumentWithOrchestrationFullResponse** and **SampleCodeRunner:: generateEmpowerDocumentWithOrchestrationDocumentIdOnly** methods demonstrate how to interact with the Orchestration service to generate Interactive documents.

The **SampleCodeRunner::generateEmpowerDocumentWithOrchestrationFullResponse** method makes use of the **OrchestrationService::generateOnDemandEmpowerOutputWithFullResponse** method. The **OrchestrationService:: generateOnDemandEmpowerOutputWithFullResponse** method requires the domain, the communication ID, the driver file DSN, the Interactive user, the contents of the driver file, and the media type of the driver file. The driver file DSN can be obtained using the instructions in the [**Driver data source**](#_Driver_data_source) section. The contents of the driver file should have the same format as the driver file resource that was created using the instructions in the [Data_mapping](#_Data_mapping) section. The media type of the driver file should be **MediaType.APPLICATION_JSON** or **MediaType.APPLICATION_XML** depending on the format of the driver file that is being passed. The Interactive user is the username of the account that will upload the generated output to Interactive. Note that the Interactive user should be a system account, not the account of the logged-in user. This will allow the end user to have fewer permissions than would be required if the logged-in username was passed.

The **OrchestrationService::generateOnDemandEmpowerOutputWithFullResponse** method returns a **List** of **OrchestrationResponseData** objects. The **OrchestrationResponseData** object is a POJO (Plain Old Java Object) that contains the data returned from the Orchestration service. The **OrchestrationResponseData::getContent** method returns the Base64 encoded ID of the document that was uploaded to the Interactive server. The Core Communications API provides a utility method, **ExstreamApiUtils::readEmpowerOutputChannelContent,** that simplifies the process of decoding the Base64 encoded content.

If you do not see a successful result, then follow the directions in the [Importing_communications](#_Importing_communications) section to run the method **SampleCodeRunner::importToDasExample2**.

The **OrchestrationService:: generateEmpowerDocumentWithOrchestrationDocumentIdResponse** method uses accept headers to return just the ID of document uploaded to the Interactive server as the result of the output generation. The **OrchestrationService::generateEmpowerDocumentWithOrchestrationDocumentIdResponse** method takes the same parameters as the **OrchestrationService::generateEmpowerDocumentWithOrchestrationFullResponse** method above, but also takes an additional parameter that specifies the type of content that is expected to be returned. The expected response type is **MediaType.APPLICATION_JSON**\*.\*

### Interacting with Interactive (Empower)

Once the Interactive document has been generated users can interact with the document by visiting {{exstream.url}}/empower/resource/docedit/**:document_id**/open. The **document_id** is returned from the **Generate Interactive document using Orchestration - Full request** (or **Generate Interactive document using Orchestration - Document ID only**) request. See the [Generating Interactive documents](#_Generating_Empower_documents) section for additional details.

However, most integrations will provide Interactive Editor to their users via an iframe. The SDK provides an example of displaying Interactive Editor in an iframe. To access the demonstration page, start the sample as described in the [Core Communications Sample Code](#exstream-sample-code) section and then open <http://localhost:8080> in your web browser.

The sample demonstrates using the Interactive JavaScript library to generate, save, and publish Interactive output and demonstrates how to display Interactive Editor in an iframe. After loading the sample page at <http://localhost:8080> the first step is the **Generate** an Interactive document. After you have clicked the **Generate** button then the options to **Save** and **Publish**, along with the iframe will appear.

If you receive an HTTP status code 500 error it is likely that your authentication token has timed out. You will need to restart the sample application to continue working. If you receive a “refused to connect” message or an empty iframe you will need to add **localhost:8080** to the **allowed.origins** list in your Interactive deployment configuration.

### Fulfilling Interactive output

After you have generated the Interactive output and interacted with the Interactive editor, you can fulfill the output. The **SampleCodeRunner::fulfillEmpowerDocumentWithOrchestrationFullResponse** and **SampleCodeRunner::fulfillEmpowerDocumentWithOrchestrationPdfOnly** methods demonstrate how to interact with the Orchestration service to fulfill Interactive documents.

The **SampleCodeRunner::fulfillEmpowerDocumentWithOrchestrationFullResponse** method makes use of the **OrchestrationService::fulfillOnDemandEmpowerDocumentWithFullResponse** method. The **OrchestrationService:: fulfillOnDemandEmpowerDocumentWithFullResponse** method requires the domain, the Interactive document id, the communication id, the driver file DSN, and **preserveDocuments**, a boolean that indicates if the Interactive document should be retained on the server. The Interactive document ID was created during the generate Interactive document action (see [Generating_Empower_documents](#_Generating_Empower_documents) for details). The driver file DSN can be obtained using the instructions in the [Driver data source](#_Driver_data_source) section. The **preserveDocuments** value determines if Interactive will maintain the communication in the Interactive database. In most cases you will want **preserveDocuments** set to false.

The **OrchestrationService:: fulfillOnDemandEmpowerDocumentWithFullResponse** method returns a **List** of **OrchestrationResponseData** objects. The **OrchestrationResponseData** object is a POJO (Plain Old Java Object) that contains the data returned from the Orchestration service. The **OrchestrationResponseData::getFilename** method will provide the name of the output file as generated by Core Communications. The **OrchestrationResponseData::getFileExtension** method will return the three letter extension typically used on Windows systems for the type of file that is returned. The results of the **getFilename** and **getFileExtension** methods are usually concatenated together to form the full filename when writing an output file to disk. The **OrchestrationResponseData::getContent** method returns the Base64 encoded contents of the output file. You are responsible for Base64 decoding the contents before writing to disk.

If you do not see a successful result, then follow the directions in the [Importing communications](#_Importing_communications) section to run the method **SampleCodeRunner::importToDasExample3**.

The **OrchestrationService::fulfillOnDemandEmpowerDocumentWithContentResponse** method uses accept headers to return just the contents of the output file as the result of the output generation. The **OrchestrationService:: fulfillOnDemandEmpowerDocumentWithContentResponse** method takes the same parameters as the **OrchestrationService:: fulfillOnDemandEmpowerDocumentWithFullResponse** method above, but also takes an additional parameter that specifies the type of content that is expected to be returned. In almost all cases this will be **MediaType.APPLICATION_PDF**.
