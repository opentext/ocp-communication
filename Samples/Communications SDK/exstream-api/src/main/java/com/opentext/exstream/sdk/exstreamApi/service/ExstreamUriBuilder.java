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

package com.opentext.exstream.sdk.exstreamApi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Base builder class for Exstream URLs. Reads the following environmental properties:<br>
 * exstream.url: The root URL for Exstream.<br>
 */
@Component
public abstract class ExstreamUriBuilder {
    protected String baseUrl;
    protected final String servicePath;
    protected UriComponentsBuilder uriBuilder;

    protected ExstreamUriBuilder(String servicePath) {
        this.servicePath = servicePath;
    }

    /**
     * Get a {@link UriComponentsBuilder} object with the exstreamUrl value from the application properties
     * @return {@link UriComponentsBuilder} initialized with the value of the service root URL
     */
    protected UriComponentsBuilder getBaseUriBuilder() {
        if (uriBuilder == null) {
            uriBuilder = UriComponentsBuilder.fromHttpUrl(baseUrl).pathSegment(servicePath);
        }
        return uriBuilder.cloneBuilder();
    }
}
