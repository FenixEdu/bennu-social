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
import org.fenixedu.bennu.social.annotations.RequiresTwitter;
import org.fenixedu.bennu.social.domain.user.TwitterUser;
import org.fenixedu.bennu.social.ui.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.RequestToken;

public class TwitterAPI extends TwitterAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(TwitterAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/twitter/callback";

    private static final SortedSet<RequiresTwitter.TwitterScopes> SCOPES = new TreeSet<RequiresTwitter.TwitterScopes>();

    private Twitter twitter = null;

    public Twitter getTwitter() {
        return twitter;
    }

    public void setTwitter(Twitter twitter) {
        this.twitter = twitter;
    }

    private TwitterAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static TwitterAPI getInstance() {
        if (Bennu.getInstance().getTwitterAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getTwitterAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static TwitterAPI initialize() {
        if (Bennu.getInstance().getTwitterAPI() == null) {
            return new TwitterAPI();
        }
        return Bennu.getInstance().getTwitterAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<TwitterUser> users = getTwitterUserSet();

        LOGGER.info("Revoking accesses for Twitter API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<TwitterUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Twitter API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {

        try {
            TwitterFactory tf = new TwitterFactory();
            Twitter twitter = tf.getInstance();
            twitter.setOAuthConsumer(getClientId(), getClientSecret());

            RequestToken oauthRequestToken = twitter.getOAuthRequestToken(getCallbackURL());

            String oauthToken = oauthRequestToken.getToken();
            String oauthTokenSecret = oauthRequestToken.getTokenSecret();
            String authorizationURL = oauthRequestToken.getAuthorizationURL();

            LOGGER.info("Received OauthToken. Token: " + oauthToken + ", TokenSecret: " + oauthTokenSecret
                    + ", AuthenticationURL: " + authorizationURL);

            TwitterUser twitterUser = new TwitterService().getAuthenticatedUser(user);
            twitterUser.setOauthToken(oauthToken);
            twitterUser.setOauthTokenSecret(oauthTokenSecret);

            return authorizationURL;

        } catch (TwitterException e) {
            LOGGER.error("Received TwitterException:" + e.getErrorMessage());
            e.printStackTrace();
            return "";
        }
    }

    @Override
    public Optional<TwitterUser> getAuthenticatedUser(User user) {

        if (!getActive()) {
            return Optional.empty();
        }

        return getTwitterUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    TwitterAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Twitter scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Twitter API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
                    }
                    instance.setBindedScopes(makeScopes(instance.getScopes()));
                });
    }

    protected Set<RequiresTwitter.TwitterScopes> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }

    public static void appendScopes(RequiresTwitter.TwitterScopes scope) {
        SCOPES.add(scope);
    }

    public static String makeScopes(Collection<RequiresTwitter.TwitterScopes> scopes) {
        return scopes.stream().map(s -> s.name()).collect(Collectors.joining(","));
    }
}
