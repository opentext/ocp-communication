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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Data response from Orchestration when it encounters an error but still produces the full response
 */
public class OrchestrationErrorData {
    public String errorMessage;
    public String statusCode;
    public EngineOutputContext engineOutputContext;

    @Override
    public String toString() {
        return String.join("\n",
                "errorMessage: " + this.errorMessage,
                "statusCode: " +  this.statusCode,
                "Engine Output Context:",
                this.engineOutputContext.toString().trim()
        );
    }
}
