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

/*
 * Copyright © 2023 Open Text Corporation, All Rights Reserved.
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

package com.opentext.exstream.sdk.sample.controller;

import com.opentext.exstream.sdk.exstreamApi.model.dto.DasLink;
import com.opentext.exstream.sdk.exstreamApi.model.dto.DasManifest;
import com.opentext.exstream.sdk.exstreamApi.model.dto.EmpowerOutputChannelContent;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ResourceFilter;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.service.DasService;
import com.opentext.exstream.sdk.exstreamApi.service.DasUriBuilder;
import com.opentext.exstream.sdk.exstreamApi.service.EmpowerUriBuilder;
import com.opentext.exstream.sdk.exstreamApi.service.OrchestrationService;
import com.opentext.exstream.sdk.sample.utils.SampleClassPathResources;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collection;
import java.util.Objects;

/**
 * Controller for sample page
 */
@Controller
public class SamplePageController {
    private static final String MODEL_DAS_FRONTEND_URL = "dasFrontEndUrl";
    private static final String MODEL_EMPOWER_URL = "empowerUrl";
    private static final String MODEL_EMPOWER_ORIGIN_URL = "empowerOriginUrl";
    private static final String MODEL_FRAME_URL = "frameUrl";
    private static final String MODEL_DOCUMENT_ID = "documentId";

    @Autowired
    EmpowerUriBuilder empowerUriBuilder;

    @Autowired
    DasUriBuilder dasUriBuilder;

    @Autowired
    OrchestrationService orchestrationService;

    @Autowired
    DasService dasService;

    @Value("${sampleApp.domain}")
    String domain;
    @Value("${sampleApp.empower.user}")
    String empowerUser;

    /**
     * Open the sample Exstream integration webpage served by this application. The page provides controls to generate
     * an Empower document and display the Empower Editor in an iFrame. It also demonstrates integration with the Editor API
     * JavaScript callback functions to save and fulfill the document after editing.
     * @param model Model attributes for the sample page
     * @return The sample page template name that will be rendered by Spring and Thymeleaf
     */
    @GetMapping("/")
    public String viewSamplePage(Model model) {
        model.addAttribute(MODEL_EMPOWER_URL, empowerUriBuilder.getEmpowerUri());
        model.addAttribute(MODEL_EMPOWER_ORIGIN_URL, "*"); // Core Communications supports multiple hostnames, so allow a permissive origin url for non-authentication requests
        model.addAttribute(MODEL_DAS_FRONTEND_URL, dasUriBuilder.buildFrontEndUri());
        if (!model.containsAttribute(MODEL_FRAME_URL)) {
            if (model.containsAttribute(MODEL_DOCUMENT_ID)) {
                model.addAttribute(MODEL_FRAME_URL, empowerUriBuilder.buildEmpowerOpenDocumentUri((String)model.getAttribute(MODEL_DOCUMENT_ID)));
            }
        }

        return "sample";
    }

    /**
     * Generate an Empower document using the Claim Acknowledgement communication provided in the sample imports
     * @param communicationId The communication ID to generate output for
     * @param redirectAttributes Model attributes that will be maintained in a session across a redirect
     * @return Redirects to the sample page view
     */
    @PostMapping(value="/generate")
    public String generateDocument(String communicationId, RedirectAttributes redirectAttributes) {
        final String driverDataSource = getDriverFileForCommunication(domain, communicationId);

        EmpowerOutputChannelContent responseData = orchestrationService.generateOnDemandEmpowerOutputDocumentIdResponse(domain, communicationId, driverDataSource, empowerUser, SampleClassPathResources.CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_EMPOWER_OUTPUT, MediaType.APPLICATION_JSON);

        redirectAttributes.addFlashAttribute(MODEL_DOCUMENT_ID, responseData.documentId);
        return "redirect:/";
    }

    /**
     * Fulfill an Empower document using the EmpowerFulfillment communication provided in the sample imports
     * @param communicationId The communication ID to generate output for
     * @param documentId The Empower document ID to fulfill
     * @param redirectAttributes Model attributes that will be maintained in a session across a redirect
     * @return Redirects to the sample page view
     */
    @PostMapping(value="/fulfill")
    public String fulfillEmpowerDocument(String communicationId, String documentId, RedirectAttributes redirectAttributes) {
        final String driverDataSource = getDriverFileForCommunication(domain, communicationId);

        byte[] pdfBytes = orchestrationService.fulfillOnDemandEmpowerDocumentWithContentResponse(domain, documentId, communicationId, driverDataSource, false, MediaType.APPLICATION_PDF);

        String pdfUrl = "data:application/pdf;base64," + Base64.encodeBase64String(pdfBytes);
        redirectAttributes.addFlashAttribute(MODEL_FRAME_URL, pdfUrl);
        return "redirect:/";
    }

    /**
     * Helper method to get the driver file for a given communication
     * @param domain The DAS domain of the communication
     * @param communicationId The communication ID to get the driver file for
     * @return DSN string retrieved from the communication set linked to the communication with the given ID
     */
    private String getDriverFileForCommunication(String domain, String communicationId) {
        // Get the communication set id for the communication
        ResourceFilter resourceFilter = new ResourceFilter().addType(ResourceType.exstrcommunicationset);
        Collection<DasLink> links = dasService.listLinks(domain, communicationId, 2, resourceFilter);
        final String communicationSetId = Objects.requireNonNull(links.stream().findFirst().map(l -> l.linkObjectId).orElse(null)).toString();

        // Get the driver file DSN for the communication set
        DasManifest manifest = dasService.getManifestForCommunicationSet(domain, communicationSetId);
        return Objects.requireNonNull(Objects.requireNonNull(manifest.dsnlist).stream().findFirst().orElseThrow()).prodDsn;
    }
}
