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
 *  Enum of output types produced by an orchestration job based on their mime types
 */
public enum OutputTypes {
    PDF("application/pdf"),
    HTML("text/html"),
    EMPOWER("application/vnd.exstream-empower"),
    JSON("application/json");

    OutputTypes(String mimeType) {
        this.mimeType = mimeType;
    }

    private final String mimeType;

    public boolean equals(String value) {
       return this.mimeType.equalsIgnoreCase(value);
    }
}
