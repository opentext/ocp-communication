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
 * The object model for paging information provided in requests to Exstream API endpoints that support paged responses
 */
public class PageInfo {
    // The number of items to return per page in the response
    private int count;

    // The paging offset. "offset" is used to get subsequent pages if there are more results available
    // than specified by the count value in the request.
    private int offset;

    //region Getters and Setters

    public int getOffset() {
        return offset;
    }

    public PageInfo setOffset(int offset) {
        this.offset = offset;
        return this;
    }

    public int getCount() {
        return count;
    }

    public PageInfo setCount(int count) {
        this.count = count;
        return this;
    }

    //endregion

    /**
     * Sets the default values for count and offset.
     * @return A {@link PageInfo} object with the default settings
     */
    public PageInfo setDefaults() {
        this.count = 100;
        this.offset = 0;
        return this;
    }
}
