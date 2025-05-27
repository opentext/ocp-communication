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

import com.google.gson.GsonBuilder;

/**
 * ExstreamGenericResponse is the base of most responses returned from an Exstream API call.<br>
 * The response contains a status string, which will be success or failure.
 */
public class ExstreamGenericResponse {
    public String status;
    public String details;

    @Override
    public String toString() {
        // Don't print the base64 content of the output into the logs to keep the size down
        String rawString = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        return rawString.replaceAll("\"content\": \".*\"", "\"content\": \"...\"");
    }
}
