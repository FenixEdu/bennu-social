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

import org.apache.commons.codec.binary.Base64;
import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.social.annotations.RequiresBitbucket;
import org.fenixedu.bennu.social.domain.user.BitbucketUser;
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

public class BitbucketAPI extends BitbucketAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BitbucketAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/bitbucket/callback";
    private static final String AUTH_URL = "https://bitbucket.org/site/oauth2/authorize";
    private static final String ACCESS_URL = "https://bitbucket.org/site/oauth2/access_token";
    private static final String REVIEW_URL = "https://bitbucket.org/site/oauth2/access_token";

    private static final SortedSet<RequiresBitbucket.BitbucketScopes> SCOPES = new TreeSet<RequiresBitbucket.BitbucketScopes>();

    private BitbucketAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static BitbucketAPI getInstance() {
        if (Bennu.getInstance().getBitbucketAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getBitbucketAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static BitbucketAPI initialize() {
        if (Bennu.getInstance().getBitbucketAPI() == null) {
            return new BitbucketAPI();
        }
        return Bennu.getInstance().getBitbucketAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<BitbucketUser> users = getBitbucketUserSet();

        LOGGER.info("Revoking accesses for Bitbucket API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<BitbucketUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Bitbucket API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {
        Optional<BitbucketUser> bitbucketUser = getAuthenticatedUser(user);

        if (!bitbucketUser.isPresent()) {
            bitbucketUser = Optional.of(new BitbucketUser(user));
        }

        return AUTH_URL + "?client_id=" + getClientId() + "&response_type=code";
    }

    @Override
    public Optional<BitbucketUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getBitbucketUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    public String getAccessTokenUrl() {
        return ACCESS_URL;
    }

    public HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("grant_type", "authorization_code");
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String plainCreds = getClientId() + ":" + getClientSecret();
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        headers.add("Authorization", "Basic " + base64Creds);

        return new HttpEntity<MultiValueMap<String, String>>(map, headers);
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    BitbucketAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Bitbucket scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Bitbucket API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
                    }
                    instance.setBindedScopes(makeScopes(instance.getScopes()));
                });
    }

    protected Set<RequiresBitbucket.BitbucketScopes> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }

    public static void appendScopes(RequiresBitbucket.BitbucketScopes[] scopes) {
        SCOPES.addAll(Arrays.asList(scopes));
    }

    public static String makeScopes(Collection<RequiresBitbucket.BitbucketScopes> scopes) {
        return scopes.stream().map(s -> s.name()).collect(Collectors.joining(","));
    }
}
