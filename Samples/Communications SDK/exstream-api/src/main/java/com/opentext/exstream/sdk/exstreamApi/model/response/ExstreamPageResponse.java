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

import java.util.Collection;

/**
 * ExstreamPageResponse object returned from an Exstream API response.<br>
 * ExstreamPageResponse is a top level response object for data collections that are batched into pages.<br>
 * The response contains the status of success or failure, a {@link Collection} of data objects, and paging information
 * @param <T> The type of objects expected in the response data collection
 * @see SimplePage
 * @see ExstreamDataResponse
 */
public class ExstreamPageResponse<T> extends ExstreamDataResponse<Collection<T>>{
    public SimplePage page;
}
