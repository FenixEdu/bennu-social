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
import org.fenixedu.bennu.social.domain.api.DropboxAPI;
import org.fenixedu.bennu.social.domain.user.DropboxUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
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
public class DropboxService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DropboxService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public DropboxAPI getInstance() {
        return DropboxAPI.getInstance();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        return getInstance().getAuthenticationUrlForUser(user);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void parseResponse(DropboxUser user, ResponseEntity<String> rsp) throws AccessTokenNotProvidedException {
        String body = rsp.getBody();

        LOGGER.info("Access token request answer from Dropbox: " + body);

        JsonObject json = new JsonParser().parse(body).getAsJsonObject();

        if (!json.has("access_token")) {
            LOGGER.error("Access token (access_token) has not been returned by Dropbox API. Instead: " + body);
            throw new AccessTokenNotProvidedException(body);
        }
        String accessToken = json.get("access_token").getAsString();
        String tokenType = json.get("token_type").getAsString();
        String uid = json.get("uid").getAsString();

        user.setAccessToken(accessToken);
        user.setTokenType(tokenType);
        user.setUid(uid);
    }

    public ResponseEntity<String> makeAccessTokenRequest(String code) {
        DropboxAPI dropboxAPI = getInstance();
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        String dropboxURL = dropboxAPI.getAccessTokenUrl(code);

        return restTemplate.exchange(dropboxURL, HttpMethod.POST, entity, String.class);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public DropboxUser getAuthenticatedUser(User user) {
        Optional<DropboxUser> dropboxUser = getInstance().getAuthenticatedUser(user);

        if (!dropboxUser.isPresent()) {
            return new DropboxUser(user);
        }

        return dropboxUser.get();
    }

}
