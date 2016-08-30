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
package org.fenixedu.bennu.social.domain.user;

import java.io.IOException;
import java.util.UUID;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.FenixFramework;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.CredentialRefreshListener;
import com.google.api.client.auth.oauth2.TokenErrorResponse;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.com.google.common.base.Strings;

public class GoogleUser extends GoogleUser_Base {
    private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    protected static final Logger LOGGER = LoggerFactory.getLogger(GoogleUser.class);

    public GoogleUser(User user) {
        super();
        setState(UUID.randomUUID().toString());
        setUser(user);
        setGoogleAPI(GoogleAPI.getInstance());
    }

    @Override
    public void delete() {
        /*
         * Warning: if an application saves the oauth token somewhere else,
         * then this method does not guarantees this application won't continue to operate.
         * To revoke access securely please use Google interface
         */
        disconnect();
        setUser(null);
        setGoogleAPI(null);
        deleteDomainObject();
    }

    public GoogleCredential getAuthenticatedSDK() {
        GoogleCredential credential =
                new GoogleCredential.Builder().addRefreshListener(new CredentialRefreshListener() {

                    @Override
                    public void onTokenResponse(Credential arg0, TokenResponse arg1) throws IOException {
                        FenixFramework.atomic(() -> {
                            setAccessToken(arg1.getAccessToken());
                            setRefreshToken(arg1.getRefreshToken());
                        });
                    }

                    @Override
                    public void onTokenErrorResponse(Credential arg0, TokenErrorResponse arg1) throws IOException {
                        delete();
                    }

                }).setTransport(new NetHttpTransport()).setJsonFactory(JacksonFactory.getDefaultInstance())
                        .setClientSecrets(getGoogleAPI().getClientId(), getGoogleAPI().getClientSecret()).build()
                        .setAccessToken(getAccessToken()).setRefreshToken(getRefreshToken());

        return credential;
    }

    private void disconnect() {
        try {
            if (!Strings.isNullOrEmpty(getAccessToken())) {
                HttpRequestFactory factory = HTTP_TRANSPORT.createRequestFactory();

                GenericUrl url = new GenericUrl("https://accounts.google.com/o/oauth2/revoke?token=" + getAccessToken());
                HttpRequest request = factory.buildGetRequest(url);
                HttpResponse response = request.execute();
            }
        } catch (Exception e) {
            LOGGER.error("Error disconnecting user '" + getUser() + "' from google.", e);
        }
    }
}
