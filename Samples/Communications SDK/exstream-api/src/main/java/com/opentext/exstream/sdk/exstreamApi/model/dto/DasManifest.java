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

import java.util.List;

/**
 * Object model for a manifest from DAS
 * Note: not every property is included at this time
 */
public class DasManifest {
    public List<DasDataSource> dsnlist;
    public List<DasQueue> queueList;

    public boolean hasEmpowerOutput() {
        if (queueList == null || queueList.isEmpty()) {
            return false;
        } else {
            return queueList.stream().anyMatch(p -> p.driver.equals("Empower"));
        }
    }

    public boolean hasPdfOutput() {
        if (queueList == null || queueList.isEmpty()) {
            return false;
        } else {
            return queueList.stream().anyMatch(p -> p.driver.equals("PDF"));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("dsnlist:\n");
        if (dsnlist != null) {
            dsnlist.forEach(dsn -> builder.append(dsn.toString()));
        }
        builder.append("queueList:\n");
        if(queueList != null) {
            queueList.forEach(queue -> builder.append(queue.toString()));
        }
        return builder.toString();
    }
}
