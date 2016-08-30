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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
import org.fenixedu.bennu.social.domain.user.LinkedinUser;
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

public class LinkedinAPI extends LinkedinAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LinkedinAPI.class);

    private static final String URL_ENCODE_SCHEME = "UTF-8";

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/linkedin/callback";
    private static final String AUTH_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    private static final String ACCESS_URL = "https://www.linkedin.com/uas/oauth2/accessToken";

    private static final SortedSet<String> SCOPES = new TreeSet<String>();

    private LinkedinAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static LinkedinAPI getInstance() {
        if (Bennu.getInstance().getLinkedinAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getLinkedinAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static LinkedinAPI initialize() {
        if (Bennu.getInstance().getLinkedinAPI() == null) {
            return new LinkedinAPI();
        }
        return Bennu.getInstance().getLinkedinAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<LinkedinUser> users = getLinkedinUserSet();

        LOGGER.info("Revoking accesses for Linkedin API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<LinkedinUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Linkedin API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public boolean isUserAuthenticated(User user) {
        // Warning: may return true if user has revoked authorization manually
        Optional<LinkedinUser> authenticatedUser = getAuthenticatedUser(user);
        return authenticatedUser.isPresent() && authenticatedUser.get().getAccessToken() != null
                && authenticatedUser.get().getExpirationDate() != null
                && authenticatedUser.get().getExpirationDate().isAfterNow();
    }

    @Override
    public Optional<LinkedinUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getLinkedinUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    public String getAccessTokenUrl() {
        return ACCESS_URL;
    }

    public HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);
        map.add("redirect_uri", getCallbackURL());
        map.add("client_id", getClientId());
        map.add("client_secret", getClientSecret());

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        return new HttpEntity<MultiValueMap<String, String>>(map, headers);
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {
        Optional<LinkedinUser> linkedinUser = getAuthenticatedUser(user);

        if (!linkedinUser.isPresent()) {
            linkedinUser = Optional.of(new LinkedinUser(user));
        }

        String scopes = getBindedScopes();

        return AUTH_URL + "?response_type=code" + "&client_id=" + getClientId() + "&redirect_uri=" + getCallbackURL() + "&state="
                + linkedinUser.get().getState() + "&scope=" + scopes;
    }

    public static String makeScopes(Collection<String> scopes) {
        String collectedScopes = scopes.stream().collect(Collectors.joining(" "));
        try {
            return URLEncoder.encode(collectedScopes, URL_ENCODE_SCHEME);
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("Error applying URL-encode to scopes: " + collectedScopes);
            return null;
        }
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    LinkedinAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Linkedin scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Linkedin API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
                    }
                    instance.setBindedScopes(makeScopes(instance.getScopes()));
                });
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    protected Set<String> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }

    public static void appendScopes(String[] scopes) {
        SCOPES.addAll(Arrays.asList(scopes));
    }
}
