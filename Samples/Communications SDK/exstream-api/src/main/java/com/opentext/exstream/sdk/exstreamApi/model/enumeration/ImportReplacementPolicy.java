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

package com.opentext.exstream.sdk.exstreamApi.model.enumeration;

/**
 * Replacement policies for an Exstream DAS import request.
 */
public enum ImportReplacementPolicy {
    // Abort the import and rollback if a conflict is detected
    ERROR,

    // Conflicting resources from the import will become the latest version of the resource. All links to/from the new resources will be added.
    REPLACE,

    // Leaves existing resources in place for conflicting resources. Links from skipped resources will be ignored, but links to them will be added
    SKIP,

    // Conflicting resources will be imported as a new copy with a new ID. Imported content that embeds this ID will have the ID replaced.
    // All links to/from the new copy will be added and the new resource will have the timestamp appended to the name to avoid confusion with
    // the existing resource.
    AUTO_RENAME
}
