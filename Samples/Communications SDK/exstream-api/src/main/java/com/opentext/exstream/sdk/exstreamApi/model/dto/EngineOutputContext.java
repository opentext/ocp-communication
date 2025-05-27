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

/**
 * EngineOutputContext object from the Exstream Orchestration API.<br>
 * ExstreamOutputContext is provided in response to creating on-demand output, and provides metadata about the output context.
 * @see OrchestrationResponseData
 */
public class EngineOutputContext {
	public String queueName;
	public String fileName;
	public String customerNumber;

    @Override
    public String toString() {
        return String.join("\n",
                "queueName: " + this.queueName,
                "fileName: " + this.fileName,
                "customerNumber: " + this.customerNumber
        );
    }
}
