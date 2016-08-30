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
import org.fenixedu.bennu.social.domain.api.FlickrAPI;
import org.fenixedu.bennu.social.domain.user.FlickrUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestFlickrAPI {

    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "FlickrAPI_clientId";
    private static final String CLIENT_SECRET = "FlickrAPI_clientSecret";

    private FlickrAPI getInactiveInstance() {
        FlickrAPI instance = FlickrAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private FlickrAPI getInstance() {
        FlickrAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        instance.setBindedScopes(BINDED_SCOPES);
        return instance;
    }

    private FlickrUser createApiUser(User user) {
        return new FlickrUser(user);
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

        FlickrAPI instance = getInactiveInstance();

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

        FlickrAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        FlickrUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        FlickrAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        FlickrUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void callbackURL() {

        FlickrAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/flickr/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkErrorScopes() {
        assertEquals("read", FlickrAPI.makeScopes(Arrays.asList("scope1")));
    }

    @Test
    public void mkSingleScopes() {
        assertEquals("write", FlickrAPI.makeScopes(Arrays.asList("write")));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals("delete", FlickrAPI.makeScopes(Arrays.asList("read", "delete")));
    }

}