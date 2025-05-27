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

package com.opentext.exstream.sdk.exstreamApi.model.response;

import com.opentext.exstream.sdk.exstreamApi.model.dto.ImportConflictResource;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ImportFoundResource;
import com.opentext.exstream.sdk.exstreamApi.model.dto.ImportReplacementPolicySettings;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ImportResponse object returned from the Exstream DAS API.<br>
 * ImportResponse contains details about the imported resources and the replacement policy used.
 */
public class ImportResponse {
    public UUID exportPackageUUID;
    public ImportReplacementPolicySettings policies = new ImportReplacementPolicySettings();
    public List<ImportFoundResource> importedResources = new ArrayList<>();
    public List<ImportFoundResource> ignoredResources = new ArrayList<>();
    public List<ImportConflictResource> conflictedResources = new ArrayList<>();
    public List<ImportFoundResource> existingResources = new ArrayList<>();
}
