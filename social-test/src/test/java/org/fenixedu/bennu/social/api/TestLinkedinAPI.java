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
import org.fenixedu.bennu.social.domain.api.LinkedinAPI;
import org.fenixedu.bennu.social.domain.user.LinkedinUser;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.FenixFrameworkRunner;

@RunWith(FenixFrameworkRunner.class)
public class TestLinkedinAPI {

    private static final String BINDED_SCOPES = "bindedScopes";
    private static final String CLIENT_ID = "LinkedinAPI_clientId";
    private static final String CLIENT_SECRET = "LinkedinAPI_clientSecret";

    private LinkedinAPI getInactiveInstance() {
        LinkedinAPI instance = LinkedinAPI.getInstance();
        instance.setActive(false);
        instance.setClientId(null);
        instance.setClientSecret(null);
        return instance;
    }

    private LinkedinAPI getInstance() {
        LinkedinAPI instance = getInactiveInstance();
        instance.setClientId(CLIENT_ID);
        instance.setClientSecret(CLIENT_SECRET);
        instance.setActive(true);
        instance.setBindedScopes(BINDED_SCOPES);
        return instance;
    }

    private LinkedinUser createApiUser(User user) {
        LinkedinUser apiUser = new LinkedinUser(user);
        apiUser.setExpirationDate(new DateTime().plus(1000));
        return apiUser;
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

        LinkedinAPI instance = getInactiveInstance();

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

        LinkedinAPI instance = getInactiveInstance();

        User user = createAuthenticatedUser("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));

        LinkedinUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticatedInactive");

        assertFalse(instance.isUserAuthenticated(user));
    }

    @Test
    public void isUserAuthenticated() {

        LinkedinAPI instance = getInstance();

        User user = createAuthenticatedUser("isUserAuthenticated");

        assertFalse(instance.isUserAuthenticated(user));

        LinkedinUser apiUser = createApiUser(user);

        assertFalse(instance.isUserAuthenticated(user));

        apiUser.setAccessToken("isUserAuthenticated");

        assertTrue(instance.isUserAuthenticated(user));
    }

    @Test
    public void authenticationUrlForUser() {

        LinkedinAPI instance = getInstance();

        User user = createAuthenticatedUser("getAuthenticationUrlForUser");
        LinkedinUser apiUser = createApiUser(user);

        String url = instance.getAuthenticationUrlForUser(user);

        assertEquals(
                "https://www.linkedin.com/uas/oauth2/authorization?response_type=code&client_id=" + CLIENT_ID + "&redirect_uri="
                        + instance.getCallbackURL() + "&state=" + apiUser.getState() + "&scope=" + instance.getBindedScopes(),
                url);
    }

    @Test
    public void accessTokenUrl() {

        LinkedinAPI instance = getInstance();

        assertEquals("https://www.linkedin.com/uas/oauth2/accessToken", instance.getAccessTokenUrl());
    }

    @Test
    public void callbackURL() {

        LinkedinAPI instance = getInstance();

        assertEquals(CoreConfiguration.getConfiguration().applicationUrl() + "/bennu-social/linkedin/callback",
                instance.getCallbackURL());
    }

    @Test
    public void mkSingleScopes() {
        assertEquals("scope1", LinkedinAPI.makeScopes(Arrays.asList("scope1")));
    }

    @Test
    public void mkSeveralScopes() {
        assertEquals("scope1+scope2", LinkedinAPI.makeScopes(Arrays.asList("scope1", "scope2")));

    }

}