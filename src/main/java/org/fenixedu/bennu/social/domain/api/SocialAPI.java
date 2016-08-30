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

import java.util.Optional;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.user.SocialUser;

public abstract class SocialAPI extends SocialAPI_Base {

    public SocialAPI() {
        super();
    }

    public boolean isConfigured() {
        return getClientId() != null && !getClientId().isEmpty() && getClientSecret() != null && !getClientSecret().isEmpty();
    }

    public abstract void revokeAllAccesses();

    public abstract void revokePermission(User user);

    public abstract Optional<? extends SocialUser> getAuthenticatedUser(User user);

    public boolean isUserAuthenticated(User user) {
        // Warning: may return true if user has revoked authorization manually
        Optional<? extends SocialUser> authenticatedUser = getAuthenticatedUser(user);
        return authenticatedUser.isPresent() && authenticatedUser.get().getAccessToken() != null;
    }

    public abstract String getAuthenticationUrlForUser(User user);

    public abstract String getCallbackURL();

}
