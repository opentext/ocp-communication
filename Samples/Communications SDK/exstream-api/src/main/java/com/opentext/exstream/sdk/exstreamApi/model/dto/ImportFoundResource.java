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

import java.util.UUID;

/**
 * Object model for resources imported to Exstream DAS
 * @see com.opentext.exstream.sdk.exstreamApi.model.response.ImportResponse
 */
public class ImportFoundResource {
    public UUID id;
    public String name;
    public String newName;
    public String type;
    public int version;
    public String state;

    @Override
    public String toString() {
        return String.join("\n",
                "name: " + this.name,
                "newName: " +  this.newName,
                "id: " +  this.id,
                "version: " + this.version,
                "type: " +  this.type,
                "state: " +  this.state
        );
    }
}
