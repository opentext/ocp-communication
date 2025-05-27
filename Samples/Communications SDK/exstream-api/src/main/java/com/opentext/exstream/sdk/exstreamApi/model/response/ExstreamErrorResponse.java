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

/**
 * Erorr response object from the Exstream API
 */
public class ExstreamErrorResponse {
    public long timestamp;
    public int status;
    public String message;
    public String error;
    public String details;
    public Integer errorCode;
    public String path;

    @Override
    public String toString() {
        return String.join("\n",
                "Timestamp: " + this.timestamp,
                "Status: " + this.status,
                "Error Code: " + this.errorCode,
                "Message: " +  this.message,
                "Error: " + this.error,
                "Details: " + this.details,
                "Path: " + this.path
        );
    }
}
