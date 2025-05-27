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

package com.opentext.exstream.sdk.exstreamApi.model.dto;

import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ResourceType;
import com.opentext.exstream.sdk.exstreamApi.model.enumeration.WorkflowState;

import java.util.Date;
import java.util.UUID;

/**
 * The object model for a specific version of a DAS resource<br>
 * Note: Not every attribute from the response has been included
 */
public class DasResourceVersion {
    public UUID id;
    public int version;
    public String name;
    public ResourceType type;
    public WorkflowState state;
    public String stateComment;
    public String description;
    public String createdBy;
    public Date createdDate;
    public String lastModifiedBy;
    public Date lastModifiedDate;
    public ResourceMetadata metadata;

    // Indicates if the resource is locked for editing
    public boolean locked;

    @Override
    public String toString() {
        String value = String.join("\n",
                "name: " + this.name,
                "id: " + this.id,
                "version: " + this.version,
                "type: " + this.type,
                "state: " + this.state,
                "stateComment: " + this.stateComment,
                "description: " + this.description,
                "locked: " + this.locked,
                "createdBy: " + this.createdBy,
                "createdDate: " + this.createdDate,
                "lastModifiedBy: " + this.lastModifiedBy,
                "lastModifiedDate: " + this.lastModifiedDate
        );
        if (this.metadata != null) {
            value = String.join("\n", value,  "metadata: subtype: " + this.metadata.subtype);
        }

        return value;
    }
}
