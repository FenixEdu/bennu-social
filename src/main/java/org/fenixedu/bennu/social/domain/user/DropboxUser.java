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

import java.util.UUID;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.DropboxAPI;

public class DropboxUser extends DropboxUser_Base {

    public DropboxUser(User user) {
        super();
        setState(UUID.randomUUID().toString());
        setUser(user);
        setDropboxAPI(DropboxAPI.getInstance());
    }

    @Override
    public void delete() {
        /*
         * Warning: if an application saves the oauth token somewhere else,
         * then this method does not guarantees this application won't continue to operate.
         * To revoke access securely please use Dropbox interface
         */
        setUser(null);
        setDropboxAPI(null);
        deleteDomainObject();
    }

}
