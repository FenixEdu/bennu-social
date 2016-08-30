/**
 * Copyright © 2016 Instituto Superior Técnico
 *
 * This file is part of Bennu Social.
 *
 * Bennu Social is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bennu Social is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Bennu Social.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.social.ui.service;

import java.util.Arrays;
import java.util.Optional;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.FacebookAPI;
import org.fenixedu.bennu.social.domain.user.FacebookUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class FacebookService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FacebookService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public FacebookAPI getInstance() {
        return FacebookAPI.getInstance();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        return getInstance().getAuthenticationUrlForUser(user);
    }

    public ResponseEntity<String> makeAccessTokenRequest(String code) {
        FacebookAPI facebookAPI = getInstance();

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String facebookURL = facebookAPI.getAccessTokenUrl(code);

        return restTemplate.exchange(facebookURL, HttpMethod.GET, entity, String.class);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void parseResponse(FacebookUser user, ResponseEntity<String> rsp) throws AccessTokenNotProvidedException {
        String body = rsp.getBody();

        LOGGER.info("Access token request answer from Facebook: " + body);

        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

        if (!json.has("access_token")) {
            LOGGER.error("Access token (access_token) has not been returned by Facebook API. Instead: " + body);
            throw new AccessTokenNotProvidedException(body);
        }

        String accessToken = json.get("access_token").getAsString();
        String tokenType = json.get("token_type").getAsString();
        int expiresIn = json.get("expires_in").getAsInt();

        user.setAccessToken(accessToken);
        user.setTokenType(tokenType);

        DateTime expirationDate = SocialService.getExpirationDate(expiresIn);
        user.setExpirationDate(expirationDate);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public FacebookUser getAuthenticatedUser(User user) {
        Optional<FacebookUser> facebookUser = getInstance().getAuthenticatedUser(user);

        if (!facebookUser.isPresent()) {
            return new FacebookUser(user);
        }
        return facebookUser.get();
    }

}
