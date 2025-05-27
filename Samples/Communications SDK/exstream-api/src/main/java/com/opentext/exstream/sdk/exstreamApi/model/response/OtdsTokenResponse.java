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
 * OtdsTokenResponse object returned from the OTDS authentication API.<br>
 * OtdsTokenResponse contains the token issued after successfully authenticating. The token must be submitted with
 * all requests to the Exstream APIs
 */
public class OtdsTokenResponse {
    public String access_token;
    public String refresh_token;
    public String token_type;
    public int expires_in;
}
