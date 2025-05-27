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

package com.opentext.exstream.sdk.sample.utils;

import org.springframework.core.io.ClassPathResource;

/**
 * Class path accessors for resources located in the sample/src/main/resources/sample folder
 */
public class SampleClassPathResources {
    public static ClassPathResource EXPORT_PACKAGE_CLAIM_ACKNOWLEDGEMENT = new ClassPathResource("samples/ExportPackage-Claim Acknowledgement (CA)-9e49a94b-8860-4318-a2a0-15ae506e328b-20220922_1810.zip");
    public static ClassPathResource EXPORT_PACKAGE_CLAIM_ESTIMATE_LETTER = new ClassPathResource("samples/ExportPackage-Claim Estimate Letter(CA)-e3ad7e29-4db9-428f-a38c-343c9e7aa39f-20220922_1810.zip");
    public static ClassPathResource EXPORT_PACKAGE_GENERIC_EMPOWER_FULFILLMENT = new ClassPathResource("samples/ExportPackage-Generic Empower Fulfillment.zip");
    public static ClassPathResource EXPORT_PACKAGE_SAMPLE_FILE_FOR_WORKFLOW_AND_UPDATE = new ClassPathResource("samples/ExportPackage-sampleFileForWorkflowAndUpdate.json-9c6f9a92-fa99-4a63-81e3-b6ad2200f5e9-20221004_1854.zip");
    public static ClassPathResource UPLOAD_NEW_CONTENT_FOR_DAS_RESOURCE_JSON = new ClassPathResource("samples/uploadNewContentForResourceInDAS.json");
    public static ClassPathResource UPLOAD_NEW_DRIVER_FILE_TO_DAS_JSON = new ClassPathResource("samples/uploadNewDriverFileIntoDAS.json");
    public static ClassPathResource CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_PDF_OUTPUT = new ClassPathResource("samples/claimAcknowledgementDriverDataForPdfOutput.json");
    public static ClassPathResource CLAIM_ACKNOWLEDGEMENT_DRIVER_DATA_FOR_EMPOWER_OUTPUT = new ClassPathResource("samples/claimAcknowledgementDriverDataForEmpowerOutput.json");
}
