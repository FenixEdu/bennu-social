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

import java.util.Optional;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.GithubAPI;
import org.fenixedu.bennu.social.domain.user.GithubUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import pt.ist.fenixframework.Atomic;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
public class GithubService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GithubService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public GithubAPI getInstance() {
        return GithubAPI.getInstance();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        return getInstance().getAuthenticationUrlForUser(user);
    }

    public ResponseEntity<String> makeAccessTokenRequest(String code) {
        GithubAPI githubAPI = getInstance();
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<MultiValueMap<String, String>> request = githubAPI.getAccessTokenRequest(code);
        return restTemplate.postForEntity(githubAPI.getAccessTokenUrl(), request, String.class);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void parseResponse(GithubUser user, ResponseEntity<String> rsp) throws AccessTokenNotProvidedException {
        String body = rsp.getBody();

        LOGGER.info("Access token request answer from Github: " + body);

        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

        if (!json.has("access_token")) {
            LOGGER.error("Access token (access_token) has not been returned by Github API. Instead: " + body);
            throw new AccessTokenNotProvidedException(body);
        }

        String accessToken = json.get("access_token").getAsString();
        String tokenType = json.get("token_type").getAsString();
        String scope = json.get("scope").getAsString();

        user.setAccessToken(accessToken);
        user.setTokenType(tokenType);
        user.setAuthorizedScopes(scope);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public GithubUser getAuthenticatedUser(User user) {
        Optional<GithubUser> githubUser = getInstance().getAuthenticatedUser(user);

        if (!githubUser.isPresent()) {
            return new GithubUser(user);
        }
        return githubUser.get();
    }
}
