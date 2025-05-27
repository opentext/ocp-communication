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

import com.opentext.exstream.sdk.exstreamApi.model.dto.OrchestrationResponseData;
import com.opentext.exstream.sdk.exstreamApi.service.EmpowerUriBuilder;
import com.opentext.exstream.sdk.exstreamApi.utils.ExstreamApiUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class SampleUtils {
    // The directory that saveContentToOutputFolder will save files to
    private static final String outputDir = "output";

    public static void printEmpowerUrls(EmpowerUriBuilder empowerUrlBuilder, List<OrchestrationResponseData> files) {
        System.out.println("The following Empower documents are available for editing:");
        // Assumes all files are empower files
        files.forEach(f -> {
            System.out.println(empowerUrlBuilder.buildEmpowerOpenDocumentUri(ExstreamApiUtils.readEmpowerOutputChannelContent(f.getContent()).documentId).toString());
        });
    }

    /**
     * Saves a base64 content string to a file.<br>
     * The file is saved to a directory called ./output relative to the current working directory by default.<br>
     * Multiple calls to this method with the same file name will overwrite the file if it exists.
     * @param fileName The name of the file to create
     * @param base64Content The file contents as a base64 encoded string
     * @return A {@link File} object representing the newly created file
     */
    public static File saveContentToOutputFolder(String fileName, String base64Content) {
        return saveContentToOutputFolder(fileName, Base64.getDecoder().decode(base64Content));
    }

    /**
     * Saves byte array content to a file.<br>
     * The file is saved to a directory called ./output relative to the current working directory by default.<br>
     * Multiple calls to this method with the same file name will overwrite the file if it exists.
     * @param fileName The name of the file to create
     * @param fileContent The file contents as a byte array
     * @return A {@link File} object representing the newly created file
     */
    public static File saveContentToOutputFolder(String fileName, byte[] fileContent) {
        // Create the output directory if it doesn't exist
        Path outputPath;
        try {
            outputPath = Files.createDirectories(Path.of(outputDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File outputFile = Paths.get(outputPath.toString(), fileName).toFile();
        try (OutputStream stream = new FileOutputStream(outputFile)) {
            stream.write(fileContent);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return outputFile;
    }

}
