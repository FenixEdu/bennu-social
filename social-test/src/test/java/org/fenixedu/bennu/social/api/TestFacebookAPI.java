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
import org.fenixedu.bennu.social.domain.api.FacebookAPI;
import org.fenixedu.bennu.social.domain.user.FacebookUser;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestFacebookAPI {

    private static final String CODE = "code";
    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "FacebookAPI_clientId";
    private static final String CLIENT_SECRET = "FacebookAPI_clientSecret";

    private FacebookAPI getInactiveInstance() {
        FacebookAPI instance = FacebookAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private FacebookAPI getInstance() {
        FacebookAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        return instance;
    }

    private FacebookUser createApiUser(User user) {
        return new FacebookUser(user);
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

        FacebookAPI instance = getInactiveInstance();

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

        FacebookAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        FacebookUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        FacebookAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        FacebookUser apiUser = createApiUser(user);
        apiUser.setExpirationDate(new DateTime().plus(1000));

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void authenticationUrlForUser() {

        FacebookAPI instance = getInstance();
        instance.setBindedScopes(BINDED_SCOPES);

        User user = createAuthenticatedUser("getAuthenticationUrlForUser");
        FacebookUser apiUser = createApiUser(user);

        String url = instance.getAuthenticationUrlForUser(user);

        assertEquals(
                "https://www.facebook.com/dialog/oauth?client_id=" + CLIENT_ID + "&redirect_uri=" + instance.getCallbackURL()
                        + "&state=" + apiUser.getState() + "&response_type=code&scope=" + BINDED_SCOPES, url);
    }

    @Test
    public void accessTokenUrl() {

        FacebookAPI instance = getInstance();

        assertEquals(
                "https://graph.facebook.com/v2.4/oauth/access_token?client_id=" + CLIENT_ID + "&redirect_uri="
                        + instance.getCallbackURL() + "&client_secret=" + CLIENT_SECRET + "&code=" + CODE,
                instance.getAccessTokenUrl(CODE));
    }

    @Test
    public void callbackURL() {

        FacebookAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/facebook/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkSingleScopes() {
        assertEquals("scope1", FacebookAPI.makeScopes(Arrays.asList("scope1")));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals("scope1,scope2", FacebookAPI.makeScopes(Arrays.asList("scope1", "scope2")));
    }

}