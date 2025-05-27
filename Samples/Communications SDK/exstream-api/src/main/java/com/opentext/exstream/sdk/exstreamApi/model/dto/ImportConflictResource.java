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

import com.opentext.exstream.sdk.exstreamApi.model.enumeration.ImportReplacementPolicy;

/**
 * Object model for conflicting resources imported to Exstream DAS
 * @see ImportFoundResource
 * @see com.opentext.exstream.sdk.exstreamApi.model.response.ImportResponse
 */
public class ImportConflictResource extends ImportFoundResource {
    public ImportReplacementPolicy userSelectedAction = null;
    public ImportReplacementPolicy performedAction = null;

    @Override
    public String toString() {
        return super.toString().trim().concat(
                String.join("\n",
                "userSelectedAction: " + this.userSelectedAction,
                "performedAction: " + this.performedAction
        ));
    }
}
