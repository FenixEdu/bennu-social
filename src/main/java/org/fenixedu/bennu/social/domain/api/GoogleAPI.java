package org.fenixedu.bennu.social.domain.api;

import java.util.*;
import java.util.stream.Collectors;

import org.fenixedu.bennu.core.domain.Bennu;
import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.util.CoreConfiguration;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class GoogleAPI extends GoogleAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(GoogleAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/google/callback";
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String ACCESS_URL = "https://www.googleapis.com/oauth2/v3/token";

    private static final SortedSet<String> SCOPES = new TreeSet<String>();

    private GoogleAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static GoogleAPI getInstance() {
        if (Bennu.getInstance().getGoogleAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getGoogleAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static GoogleAPI initialize() {
        if (Bennu.getInstance().getGoogleAPI() == null) {
            return new GoogleAPI();
        }
        return Bennu.getInstance().getGoogleAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<GoogleUser> users = getGoogleUserSet();

        LOGGER.info("Revoking accesses for Google API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<GoogleUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Google API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public Optional<GoogleUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getGoogleUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {
        Optional<GoogleUser> googleUser = getAuthenticatedUser(user);
        return AUTH_URL + "?response_type=code" + "&client_id=" + getClientId() + "&redirect_uri=" + getCallbackURL()
                + "&scope="+ getBindedScopes() + "&approval_prompt=force&&access_type=offline"
                + "&state=" + (googleUser.isPresent() ? googleUser.get().getState() :  UUID.randomUUID().toString());
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    public String getAccessTokenUrl(String code) {
        return ACCESS_URL + "?code=" + code + "&grant_type=authorization_code" + "&client_id=" + getClientId()
               + "&client_secret=" + getClientSecret() + "&redirect_uri=" + getCallbackURL();
    }

    public static String makeScopes(Collection<String> scopes) {
        return scopes.stream().collect(Collectors.joining(" "));
    }

    public static void appendScopes(String[] scopes) {
        SCOPES.addAll(Arrays.asList(scopes));
    }

    public static void ensureConsistentScope() {
        FenixFramework
        .atomic(() -> {
            GoogleAPI instance = getInstance();

            LOGGER.info("Checking old vs new Google scopes " + instance.getBindedScopes() + " vs "
                    + makeScopes(instance.getScopes()));

            if (instance.getBindedScopes() != null
                    && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                LOGGER.warn("Google API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
            }
            instance.setBindedScopes(makeScopes(instance.getScopes()));
        });
    }

    protected Set<String> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }
}
