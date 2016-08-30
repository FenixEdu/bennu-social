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
import org.fenixedu.bennu.social.annotations.RequiresDropbox;
import org.fenixedu.bennu.social.domain.user.DropboxUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.Atomic.TxMode;
import pt.ist.fenixframework.FenixFramework;

public class DropboxAPI extends DropboxAPI_Base {

    protected static final Logger LOGGER = LoggerFactory.getLogger(DropboxAPI.class);

    private static final String CALLBACK_URL = CoreConfiguration.getConfiguration().applicationUrl()
            + "/bennu-social/dropbox/callback";
    private static final String AUTH_URL = "https://www.dropbox.com/1/oauth2/authorize";
    private static final String ACCESS_URL = "https://api.dropbox.com/1/oauth2/token";

    private static final SortedSet<RequiresDropbox.DropboxScopes> SCOPES = new TreeSet<RequiresDropbox.DropboxScopes>();

    private DropboxAPI() {
        super();
        setBennu(Bennu.getInstance());
    }

    public static DropboxAPI getInstance() {
        if (Bennu.getInstance().getDropboxAPI() == null) {
            return initialize();
        }
        return Bennu.getInstance().getDropboxAPI();
    }

    @Atomic(mode = TxMode.WRITE)
    private static DropboxAPI initialize() {
        if (Bennu.getInstance().getDropboxAPI() == null) {
            return new DropboxAPI();
        }
        return Bennu.getInstance().getDropboxAPI();
    }

    @Override
    public void revokeAllAccesses() {
        Set<DropboxUser> users = getDropboxUserSet();

        LOGGER.info("Revoking accesses for Dropbox API: Number of accounts = " + users.size());

        users.stream().forEach(u -> {
            u.delete();
        });
    }

    @Override
    public void revokePermission(User user) {
        Optional<DropboxUser> authenticatedUser = getAuthenticatedUser(user);

        LOGGER.info("Revoking permission for Dropbox API by user: " + user.getUsername() + ". Existent permission? "
                + authenticatedUser.isPresent());

        if (authenticatedUser.isPresent()) {
            authenticatedUser.get().delete();
        }
    }

    @Override
    public Optional<DropboxUser> getAuthenticatedUser(User user) {
        if (!getActive()) {
            return Optional.empty();
        }

        return getDropboxUserSet().stream().filter(u -> u.getUser().equals(user)).findFirst();
    }

    @Override
    public String getAuthenticationUrlForUser(User user) {
        Optional<DropboxUser> dropboxUser = getAuthenticatedUser(user);

        if (!dropboxUser.isPresent()) {
            dropboxUser = Optional.of(new DropboxUser(user));
        }

        return AUTH_URL + "?response_type=code" + "&client_id=" + getClientId() + "&redirect_uri=" + getCallbackURL() + "&state="
                + dropboxUser.get().getState();
    }

    public String getAccessTokenUrl(String code) {
        return ACCESS_URL + "?code=" + code + "&grant_type=authorization_code" + "&client_id=" + getClientId()
                + "&client_secret=" + getClientSecret() + "&redirect_uri=" + getCallbackURL();
    }

    @Override
    public String getCallbackURL() {
        return CALLBACK_URL;
    }

    public static void ensureConsistentScope() {
        FenixFramework
                .atomic(() -> {
                    DropboxAPI instance = getInstance();

                    LOGGER.info("Checking old vs new Dropbox scopes " + instance.getBindedScopes() + " vs "
                            + makeScopes(instance.getScopes()));

                    if (instance.getBindedScopes() != null
                            && !makeScopes(instance.getScopes()).equals(instance.getBindedScopes())) {
                        LOGGER.warn("Dropbox API has changed scopes. Endpoint invocations may fail if users do not review their access for the application");
                    }
                    instance.setBindedScopes(makeScopes(instance.getScopes()));
                });
    }

    protected Set<RequiresDropbox.DropboxScopes> getScopes() {
        return Collections.unmodifiableSet(SCOPES);
    }

    public static void appendScopes(RequiresDropbox.DropboxScopes[] scopes) {
        SCOPES.addAll(Arrays.asList(scopes));
    }

    public static String makeScopes(Collection<RequiresDropbox.DropboxScopes> scopes) {
        return scopes.stream().map(s -> s.name()).collect(Collectors.joining(","));
    }

}
