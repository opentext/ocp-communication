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

package com.opentext.exstream.sdk.exstreamApi.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Configuration bean used to read the configuration information for the exstream-api project
 */
@Configuration
public class ExstreamApiConfiguration {
    private static Logger logger = LoggerFactory.getLogger(ExstreamApiConfiguration.class);
    private final String OT2_DEPLOYMENT = "ot2";
    private final String LOCAL_DEPLOYMENT = "local";

    @Value("${exstream.deployment.type:ot2}")
    private String deploymentType;

    @PostConstruct
    private void logConfig() {
       logger.info("deploymentType=" + deploymentType);
    }

    public boolean isOT2() {
        if (deploymentType.equals(OT2_DEPLOYMENT)) {
            return true;
        }
        return false;
    }

    public boolean isLocal() {
        if (deploymentType.equals(LOCAL_DEPLOYMENT)) {
            return true;
        }
        return false;
    }
}
