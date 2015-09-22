package org.fenixedu.bennu.social.ui.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pt.ist.fenixframework.Atomic;

import java.util.Arrays;
import java.util.Optional;

@Service
public class GoogleService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GoogleService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public GoogleAPI getInstance() {
        return GoogleAPI.getInstance();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        return getInstance().getAuthenticationUrlForUser(user);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public GoogleUser getAuthenticatedUser(User user) {
        Optional<GoogleUser> googleUser = getInstance().getAuthenticatedUser(user);

        if (!googleUser.isPresent()) {
            return new GoogleUser(user);
        }
        return googleUser.get();
    }

    public ResponseEntity<String> makeAccessTokenRequest(String code) {
        GoogleAPI googleAPI = getInstance();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String googleURL = googleAPI.getAccessTokenUrl(code);

        return restTemplate.exchange(googleURL, HttpMethod.POST, entity, String.class);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void parseResponse(GoogleUser user, ResponseEntity<String> rsp) throws AccessTokenNotProvidedException {
        String body = rsp.getBody();

        LOGGER.info("Access token request answer from Google: " + body);

        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

        if (!json.has("access_token")) {
            LOGGER.error("Access token (access_token) has not been returned by Google API. Instead: " + body);
            throw new AccessTokenNotProvidedException(body);
        }

        String accessToken = json.get("access_token").getAsString();
        String tokenType = json.get("token_type").getAsString();
        String idToken = Optional.ofNullable(json.get("id_token")).map(JsonElement::getAsString).orElse(user.getTokenId());
        String refreshToken = Optional.ofNullable(json.get("refresh_token")).map(JsonElement::getAsString).orElse(user.getRefreshToken());
        DateTime expirationDate = Optional.ofNullable(json.get("expires_in")).map(JsonElement::getAsInt)
                .map(SocialService::getExpirationDate).orElse(null);

        user.setExpirationDate(expirationDate);
        user.setAccessToken(accessToken);
        user.setTokenType(tokenType);
        user.setTokenId(idToken);
        user.setRefreshToken(refreshToken);
    }
}
