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

import com.opentext.exstream.sdk.exstreamApi.model.response.OtdsTokenResponse;
import com.opentext.exstream.sdk.exstreamApi.spring.ExstreamApiConfiguration;
import com.opentext.exstream.sdk.exstreamApi.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/**
 * Service layer for interacting with an OpenText Directory Service instance. Reads the following environmental properties:<br>
 * otds.tenant: The tenant to connect to.<br>
 * otds.username: Username to authenticate as.<br>
 * otds.password: Password for the user.<br>
 * otds.clientId: Client id to include in the authentication header.<br>
 * otds.subscription.name: OT2 subscription name.
 */
@Service
public class OtdsService {
	private static Logger logger = LoggerFactory.getLogger(OtdsService.class);

    @Autowired
    ExstreamApiConfiguration configuration;

    // Tenant to access
    @Value("${otds.tenant:}")
	private String otdsTenant;

	// Username to authenticate as
    @Value("${otds.username:}")
	private String otdsUserName;

	// Password for the user
    @Value("${otds.password:}")
	private String otdsPassword;

	// Client id to include in the authentication header
    @Value("${otds.clientId:}")
	private String otdsClientId;

    // Service Client id to include in the authentication header
    @Value("${otds.serviceClientId:}")
    private String serviceClientId;

    //Service Client secret to include in the authentication header
    @Value("${otds.serviceClientSecret:}")
    private String serviceClientSecret;

    // OT2 subscription name
    @Value("${otds.subscription.name:}")
    private String otdsSubscriptionName;

    private OtdsTokenResponse cachedToken = null;

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_CLIENT_CREDENTIALS = "client_credentials";
    private static final String LOCAL_OTDS_SCOPE = "otds:groups";
    private static final String OT2_OTDS_SCOPE = "search otds:groups subscription:%s";

	RestTemplate restTemplate;
	@Autowired
    OtdsUriBuilder uriBuilder;

	public OtdsService() {
		restTemplate = RestTemplateUtils.buildRestTemplateWithLoggingAndErrorHandler();
	}

	/**
	 * Authenticates as the configured user in the configured tenant and returns the resulting token.
     * The cached copy of the token will always be returned if it exists.
	 * @return The access token string
	 */
    public String getToken() {
        return getToken(false);
    }

    /**
     * Authenticates as the configured user in the configured tenant and returns the resulting token.
     * @param refreshToken If true, get a new token and overwrite the existing cached copy
     * @return The access token string
     */
	public String getToken(boolean refreshToken) {
		return getToken(this.otdsTenant, this.otdsUserName, this.otdsPassword, refreshToken);
	}

	/**
	 * Authenticates as the specified user in the specified tenant and returns the resulting token.<br>
     * Note: The OTDS token will be cached after it is first retrieved and reused. If this token expires
     * subsequent uses will fail, and it will need to be refreshed.
     *
	 * @param tenant Tenant to access
	 * @param username User to authenticate as
	 * @param password Password for the user
     * @param refreshToken If true, get a new token and overwrite the existing cached copy
	 * @return The access token string
	 */
	public String getToken(String tenant, String username, String password, boolean refreshToken) {
        // This could potentially be smart and know when to automatically refresh the token
        // since the OtdsTokenResponse object has the expires_in time, and we could log the time
        // we got it.
        // Return the cached token if we have one
        if (cachedToken != null && !refreshToken) {
            logger.info("Reusing cached OTDS token.");
            return cachedToken.access_token;
        }

		// Build the request
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(List.of(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", GRANT_TYPE_PASSWORD);
		map.add("username", username);
		map.add("password", password);
		map.add("client_id", otdsClientId);
        if (configuration.isOT2()) {
            map.add("scope", String.format(OT2_OTDS_SCOPE, otdsSubscriptionName));
        } else {
            map.add("scope", LOCAL_OTDS_SCOPE);
        }

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

		// Get the token
        URI uri = uriBuilder.buildOtdsTokenUri(tenant);
		logger.info("Fetching token from OTDS: {}.", uri.toString());
		ResponseEntity<OtdsTokenResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, OtdsTokenResponse.class);

		logger.info("Successfully authenticated to OTDS.");

        // Cache the token
        cachedToken = Objects.requireNonNull(response.getBody());

		// Return the access token
		return cachedToken.access_token;
	}

    /**
     * Authenticates as the configured user in the configured tenant and returns the resulting token.
     * The cached copy of the token will always be returned if it exists.
     * @return The access token string
     */
    public String getServiceClientToken() {
        return getServiceClientToken(false);
    }

    /**
     * Authenticates as the configured user in the configured tenant and returns the resulting token.
     * @param refreshToken If true, get a new token and overwrite the existing cached copy
     * @return The access token string
     */
    public String getServiceClientToken(boolean refreshToken) {
        return getServiceClientToken(this.otdsTenant, refreshToken);
    }


    /**
     * Authenticates using service credentials(client_credentials) in the specified tenant and returns the resulting token.<br>
     * Note: The OTDS token will be cached after it is first retrieved and reused. If this token expires
     * subsequent uses will fail, and it will need to be refreshed.
     *
     * @param tenant Tenant to access
     * @param refreshToken If true, get a new token and overwrite the existing cached copy
     * @return The access token string
     */
    public String getServiceClientToken(String tenant,  boolean refreshToken) {
        // This could potentially be smart and know when to automatically refresh the token
        // since the OtdsTokenResponse object has the expires_in time, and we could log the time
        // we got it.
        // Return the cached token if we have one
        if (cachedToken != null && !refreshToken) {
            logger.info("Reusing cached OTDS token.");
            return cachedToken.access_token;
        }

        // Build the request
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", GRANT_TYPE_CLIENT_CREDENTIALS);
        map.add("client_id", serviceClientId);
        map.add("client_secret", serviceClientSecret);
        if (configuration.isOT2()) {
            map.add("scope", String.format(OT2_OTDS_SCOPE, otdsSubscriptionName));
        } else {
            map.add("scope", LOCAL_OTDS_SCOPE);
        }

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        // Get the token
        URI uri = uriBuilder.buildOtdsTokenUri(tenant);
        logger.info("Fetching token from OTDS: {}.", uri.toString());
        ResponseEntity<OtdsTokenResponse> response = restTemplate.exchange(uri, HttpMethod.POST, entity, OtdsTokenResponse.class);

        logger.info("Successfully authenticated to OTDS.");

        // Cache the token
        cachedToken = Objects.requireNonNull(response.getBody());

        // Return the access token
        return cachedToken.access_token;
    }

}
