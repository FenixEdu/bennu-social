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
import org.fenixedu.bennu.social.domain.user.GithubUser;
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

public class GithubAPI extends GithubAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GithubAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/github/callback";
    private static final String AUTH_URL = "https://github.com/login/oauth/authorize";
    private static final String ACCESS_URL = "https://github.com/login/oauth/access_token";
    private static final String REVIEW_URL = "https://github.com/settings/connections/applications/";

    private static final SortedSet<String> SCOPES = new TreeSet<String>();

    private GithubAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static GithubAPI getInstance() {
        if (Bennu.getInstance().getGithubAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getGithubAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static GithubAPI initialize() {
        if (Bennu.getInstance().getGithubAPI() == null) {
            return new GithubAPI();
        }
        return Bennu.getInstance().getGithubAPI();
    }

    @Override
    public Optional<GithubUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getGithubUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {
        Optional<GithubUser> githubUser = getAuthenticatedUser(user);

        if (!githubUser.isPresent()) {
            githubUser = Optional.of(new GithubUser(user));
        }

        String scopes = getBindedScopes();
        githubUser.get().setAskedScopes(scopes);

        return AUTH_URL + "?client_id=" + getClientId() + "&state=" + githubUser.get().getState() + "&scope=" + scopes;
    }

    public String getAccessTokenUrl() {
        return ACCESS_URL;
    }

    public String getReviewAuthUrl() {
        return REVIEW_URL + getClientId();
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    @Override
    public void revokeAllAccesses() {

        Set<GithubUser> users = getGithubUserSet();

        LOGGER.info("Revoking accesses for Github API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<GithubUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Github API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    public HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String code) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("client_id", getClientId());
        map.add("client_secret", getClientSecret());
        map.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        return new HttpEntity<MultiValueMap<String, String>>(map, headers);
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    GithubAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Github scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Github API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
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
