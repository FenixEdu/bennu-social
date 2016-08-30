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
import org.fenixedu.bennu.social.annotations.RequiresBitbucket;
import org.fenixedu.bennu.social.domain.api.BitbucketAPI;
import org.fenixedu.bennu.social.domain.user.BitbucketUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestBitbucketAPI {

    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "BitbucketAPI_clientId";
    private static final String CLIENT_SECRET = "BitbucketAPI_clientSecret";

    private BitbucketAPI getInactiveInstance() {
        BitbucketAPI instance = BitbucketAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private BitbucketAPI getInstance() {
        BitbucketAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        instance.setBindedScopes(BINDED_SCOPES);
        return instance;
    }

    private BitbucketUser createApiUser(User user) {
        return new BitbucketUser(user);
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

        BitbucketAPI instance = getInactiveInstance();

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

        BitbucketAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        BitbucketUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        BitbucketAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        BitbucketUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void authenticationUrlForUser() {

        BitbucketAPI instance = getInstance();

        User user = createAuthenticatedUser("getAuthenticationUrlForUser");

        String url = instance.getAuthenticationUrlForUser(user);

        assertEquals("https://bitbucket.org/site/oauth2/authorize" + "?client_id=" + CLIENT_ID + "&response_type=code", url);
    }

    @Test
    public void accessTokenUrl() {

        BitbucketAPI instance = getInstance();

        assertEquals("https://bitbucket.org/site/oauth2/access_token", instance.getAccessTokenUrl());
    }

    @Test
    public void callbackURL() {

        BitbucketAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/bitbucket/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkSingleScopes() {
        assertEquals(RequiresBitbucket.BitbucketScopes.ACCOUNT_EMAIL.toString(),
                BitbucketAPI.makeScopes(Arrays.asList(RequiresBitbucket.BitbucketScopes.ACCOUNT_EMAIL)));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals(RequiresBitbucket.BitbucketScopes.ACCOUNT_EMAIL + "," + RequiresBitbucket.BitbucketScopes.SNIPPETS_R,
                BitbucketAPI.makeScopes(Arrays.asList(RequiresBitbucket.BitbucketScopes.ACCOUNT_EMAIL,
                        RequiresBitbucket.BitbucketScopes.SNIPPETS_R)));
    }

}