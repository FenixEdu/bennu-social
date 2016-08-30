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
package org.fenixedu.bennu.social.domain.user;

import java.util.Arrays;
import java.util.UUID;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.GithubAPI;

public class GithubUser extends GithubUser_Base {

    public GithubUser(User user) {
        super();
        setState(UUID.randomUUID().toString());
        setUser(user);
        setGithubAPI(GithubAPI.getInstance());
        setAskedScopes(GithubAPI.getInstance().getBindedScopes());
    }

    public boolean wasAskedScope(String scope) {
        return Arrays.asList(getAskedScopes().split(",")).contains(scope);
    }

    public boolean hasAcceptedScope(String scope) {
        return Arrays.asList(getAuthorizedScopes().split(",")).contains(scope);
    }

    public boolean hasRejecteddScope(String scope) {
        return wasAskedScope(scope) && !hasAcceptedScope(scope);
    }

    @Override
    public void delete() {
        /*
         * Warning: if an application saves the oauth token somewhere else,
         * then this method does not guarantees this application won't continue to operate.
         * To revoke access securely please use Github interface
         */
        setUser(null);
        setGithubAPI(null);
        deleteDomainObject();
    }
}
