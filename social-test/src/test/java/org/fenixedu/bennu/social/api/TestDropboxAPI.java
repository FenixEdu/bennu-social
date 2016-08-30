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
import org.fenixedu.bennu.social.annotations.RequiresDropbox;
import org.fenixedu.bennu.social.domain.api.DropboxAPI;
import org.fenixedu.bennu.social.domain.user.DropboxUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestDropboxAPI {

    private static final String CODE = "code";
    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "DropboxAPI_clientId";
    private static final String CLIENT_SECRET = "DropboxAPI_clientSecret";

    private DropboxAPI getInactiveInstance() {
        DropboxAPI instance = DropboxAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private DropboxAPI getInstance() {
        DropboxAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        instance.setBindedScopes(BINDED_SCOPES);
        return instance;
    }

    private DropboxUser createApiUser(User user) {
        return new DropboxUser(user);
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

        DropboxAPI instance = getInactiveInstance();

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

        DropboxAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        DropboxUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        DropboxAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        DropboxUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void authenticationUrlForUser() {

        DropboxAPI instance = getInstance();

        User user = createAuthenticatedUser("getAuthenticationUrlForUser");
        DropboxUser apiUser = createApiUser(user);

        String url = instance.getAuthenticationUrlForUser(user);

        assertEquals("https://www.dropbox.com/1/oauth2/authorize?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri="
                + instance.getCallbackURL() + "&state=" + apiUser.getState(), url);
    }

    @Test
    public void accessTokenUrl() {

        DropboxAPI instance = getInstance();

        assertEquals("https://api.dropbox.com/1/oauth2/token?code=" + CODE + "&grant_type=authorization_code" + "&client_id="
                + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + instance.getCallbackURL(),
                instance.getAccessTokenUrl(CODE));
    }

    @Test
    public void callbackURL() {

        DropboxAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/dropbox/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkSingleScopes() {
        assertEquals(RequiresDropbox.DropboxScopes.ALL_FOLDERS.toString(),
                DropboxAPI.makeScopes(Arrays.asList(RequiresDropbox.DropboxScopes.ALL_FOLDERS)));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals(RequiresDropbox.DropboxScopes.ALL_FOLDERS + "," + RequiresDropbox.DropboxScopes.OWN_FOLDER,
                DropboxAPI.makeScopes(Arrays.asList(RequiresDropbox.DropboxScopes.ALL_FOLDERS,
                        RequiresDropbox.DropboxScopes.OWN_FOLDER)));
    }

}