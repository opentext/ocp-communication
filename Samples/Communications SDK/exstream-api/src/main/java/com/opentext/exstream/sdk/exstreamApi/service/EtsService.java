package com.opentext.exstream.sdk.exstreamApi.service;

import com.opentext.exstream.sdk.exstreamApi.utils.RestTemplateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class EtsService {
    public static final String ETS_TOKEN = "ETSToken";
    private static final Logger logger = LoggerFactory.getLogger(EtsService.class);
    private static final String ETS_SEARCH = "/search";
    private static final String MEDIA_TYPE_HAL_JSON = "application/hal+json";

    RestTemplate restTemplate;

    @Autowired
    OtdsService otdsService;

    @Value("${ets.url.root.backend:http://localhost}")
    private String etsUrlRootBackEnd;
    @Value("${ets.url.entitlement:/ets/v1}")
    private String etsRootPath;
    @Value("${otds.clientId:}")
    private String clientId;

    // Username to get entitlements for
    @Value("${otds.username:}")
    private String otdsUserName;
    @Value("${otds.subscription.name:}")
    private String otdsSubscriptionName;

    private String cachedToken = null;

    public EtsService() {
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
        return getToken(this.otdsUserName, this.otdsSubscriptionName, refreshToken);
    }

    /**
     * Authenticates as the specified user in the specified tenant and returns the resulting token.<br>
     * Note: The OTDS token will be cached after it is first retrieved and reused. If this token expires
     * subsequent uses will fail, and it will need to be refreshed.
     *
     * @param userId User to get ETS token for
     * @param subscriptionName Subscription name to find corresponding entitlements
     * @param refreshToken If true, get a new token and overwrite the existing cached copy
     * @return The access token string
     */
    public String getToken(String userId, String subscriptionName, boolean refreshToken) {
        if ((cachedToken == null) || (refreshToken)) {
            cachedToken = fetchETSToken(userId, subscriptionName);
        }
        return cachedToken;
    }

    private String getEntitlementUri(String uid, String subscriptionName) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(this.etsUrlRootBackEnd).path(etsRootPath + EtsService.ETS_SEARCH).queryParam("query", "entitlement").queryParam("clientId", this.clientId);

        if ((uid != null) && (!uid.isEmpty())) {
            uriBuilder.queryParam("userId", uid);
        }
        if ((subscriptionName != null) && (!subscriptionName.isEmpty())) {
            uriBuilder.queryParam("subscriptionName", subscriptionName);
        }
        return uriBuilder.toUriString();
    }

    public String fetchETSToken(String userId, String subscriptionName) {
        HttpHeaders headers = getHeaders(null);
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<String> response = this.restTemplate.exchange(this.getEntitlementUri(userId, subscriptionName), HttpMethod.GET, entity, String.class);
            if (response.getStatusCode() != HttpStatus.OK) {
                logger.warn("Error getting ETS token for user {} subscription={}. Status={} Body={}", userId, subscriptionName, response.getStatusCode(), response.getBody());
                throw new RuntimeException("Error getting ETS token");
            }
            else {
                String etsValue = response.getHeaders().getFirst(ETS_TOKEN);
                if ((etsValue == null) || (etsValue.isEmpty())) {
                    logger.warn("Unable to retrieve {} from response to our request to fetch an ETS token.", ETS_TOKEN);
                    throw new RuntimeException("Unable to retrieve ETS token from response");
                }
                return etsValue;
            }
        } catch (HttpStatusCodeException httpStatusCodeException) {
            throw new RuntimeException(httpStatusCodeException.getResponseBodyAsString());
        }
    }

    private HttpHeaders getAuthHeaders(HttpHeaders headers, String token) {
        if (headers == null)
            headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        return headers;
    }

    private HttpHeaders getHeaders(HttpHeaders headers) {
        String token = otdsService.getToken(false);
        headers = getAuthHeaders(headers, token);
        headers.add("Accept", EtsService.MEDIA_TYPE_HAL_JSON);
        return headers;
    }
}
