/**
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of social-test.
 *
 * social-test is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * social-test is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with social-test.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.bennu.social.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Locale;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.domain.UserProfile;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.social.domain.api.GithubAPI;
import org.fenixedu.bennu.social.domain.user.GithubUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestGithubAPI {

    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "GithubAPI_clientId";
    private static final String CLIENT_SECRET = "GithubAPI_clientSecret";

    private GithubAPI getInactiveInstance() {
        GithubAPI instance = GithubAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private GithubAPI getInstance() {
        GithubAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        return instance;
    }

    private GithubUser createApiUser(User user) {
        return new GithubUser(user);
    }

    public static User createAuthenticatedUser(String username) {
        return createAuthenticatedUser(username, "name", "familyName", "mail@fenixedu.org");
    }

    public static User createAuthenticatedUser(String username, String name, String familyName, String email) {
        User user = new User(username, new UserProfile(name, familyName, name + " " + familyName, email, Locale.getDefault()));
        Authenticate.mock(user);
        return user;
    }

    @Test
    public void isConfigured() {

        GithubAPI instance = getInactiveInstance();

        assertFalse(instance.isConfigured());

        instance.setClientId("");
        instance.setClientSecret("");

        assertFalse(instance.isConfigured());

        instance.setClientId("isConfigured");
        instance.setClientSecret("");

        assertFalse(instance.isConfigured());

        instance.setClientId("");
        instance.setClientSecret("isConfigured");

        assertFalse(instance.isConfigured());

        instance.setClientId("isConfigured");
        instance.setClientSecret("isConfigured");

        assertTrue(instance.isConfigured());
    }

    @Test
    public void isUserAuthenticatedInactive() {

        GithubAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        GithubUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        GithubAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        GithubUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void authenticationUrlForUser() {

        GithubAPI instance = getInstance();
        instance.setBindedScopes(BINDED_SCOPES);

        User user = createAuthenticatedUser("getAuthenticationUrlForUser");

        String url = instance.getAuthenticationUrlForUser(user);

        assertEquals("https://github.com/login/oauth/authorize?client_id=" + CLIENT_ID + "&state="
                + instance.getAuthenticatedUser(user).get().getState() + "&scope=" + BINDED_SCOPES, url);
    }

    @Test
    public void accessTokenUrl() {

        GithubAPI instance = getInstance();

        assertEquals("https://github.com/login/oauth/access_token", instance.getAccessTokenUrl());
    }

    @Test
    public void reviewAuthUrl() {

        GithubAPI instance = getInstance();

        assertEquals("https://github.com/settings/connections/applications/" + CLIENT_ID, instance.getReviewAuthUrl());
    }

    @Test
    public void callbackURL() {

        GithubAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/github/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkSingleScopes() {
        assertEquals("scope1", GithubAPI.makeScopes(Arrays.asList("scope1")));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals("scope1,scope2", GithubAPI.makeScopes(Arrays.asList("scope1", "scope2")));
    }

}