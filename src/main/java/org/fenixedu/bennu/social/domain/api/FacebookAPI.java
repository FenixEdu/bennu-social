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
package org.fenixedu.bennu.social.domain.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.social.domain.user.FacebookUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class FacebookAPI extends FacebookAPI_Base {

    private static final String AUTHORIZATION_RESPONSE_TYPE = "code";

    protected static final Logger LOGGER = LoggerFactory.getLogger(FacebookAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/facebook/callback";
    private static final String AUTH_URL = "https://www.facebook.com/dialog/oauth";
    private static final String ACCESS_URL = "https://graph.facebook.com/v2.4/oauth/access_token";

    private static final SortedSet<String> SCOPES = new TreeSet<String>();

    private FacebookAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static FacebookAPI getInstance() {
        if (Bennu.getInstance().getFacebookAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getFacebookAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static FacebookAPI initialize() {
        if (Bennu.getInstance().getFacebookAPI() == null) {
            return new FacebookAPI();
        }
        return Bennu.getInstance().getFacebookAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<FacebookUser> users = getFacebookUserSet();

        LOGGER.info("Revoking accesses for Facebook API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<FacebookUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Facebook API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public boolean isUserAuthenticated(User user) {
        // Warning: may return true if user has revoked authorization manually
        Optional<FacebookUser> authenticatedUser = getAuthenticatedUser(user);
        return authenticatedUser.isPresent() && authenticatedUser.get().getAccessToken() != null
                && authenticatedUser.get().getExpirationDate() != null
                && authenticatedUser.get().getExpirationDate().isAfterNow();
    }

    @Override
    @Atomic(mode = TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        Optional<FacebookUser> facebookUser = getAuthenticatedUser(user);

        if (!facebookUser.isPresent()) {
            facebookUser = Optional.of(new FacebookUser(user));
        }

        String scopes = getBindedScopes();

        return AUTH_URL + "?client_id=" + getClientId() + "&redirect_uri=" + getCallbackURL() + "&state="
                + facebookUser.get().getState() + "&response_type=" + AUTHORIZATION_RESPONSE_TYPE + "&scope=" + scopes;
    }

    @Override
    public Optional<FacebookUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getFacebookUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    public String getAccessTokenUrl(String code) {
        return ACCESS_URL + "?client_id=" + getClientId() + "&redirect_uri=" + getCallbackURL() + "&client_secret="
                + getClientSecret() + "&code=" + code;
    }

    public HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("code", code);
        map.add("redirect_uri", getCallbackURL());
        map.add("client_id", getClientId());
        map.add("client_secret", getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
        return request;
    }

    public String getReaskPermissionUrlForUser(User user) {
        return getAuthenticationUrlForUser(user) + "&auth_type=rerequest";
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    FacebookAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Facebook scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Facebook API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
                    }
                    instance.setBindedScopes(makeScopes(instance.getScopes()));
                });
    }

    protected Set<String> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }

    public static void appendScopes(String[] scopes) {
        SCOPES.addAll(Arrays.asList(scopes));
    }

    public static String makeScopes(Collection<String> scopes) {
        return scopes.stream().collect(Collectors.joining(","));
    }
}
