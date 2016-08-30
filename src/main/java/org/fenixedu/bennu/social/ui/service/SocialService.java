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
package org.fenixedu.bennu.social.ui.service;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.SocialAPI;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;

@Service
public class SocialService {

    private static final int EXPIRATION_TIME_CONVERSION_MARGIN = 2;
    protected static final Logger LOGGER = LoggerFactory.getLogger(SocialService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void activateAPI(SocialAPI api, boolean active) {
        api.setActive(active);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void setAPICredentials(SocialAPI api, String clientID, String clientSecret) {

        if ((api.getClientId() != null && !api.getClientId().equals(clientID))
                || (api.getClientSecret() != null && !api.getClientSecret().equals(clientSecret))) {
            api.revokeAllAccesses();
        }

        api.setClientId(clientID);
        api.setClientSecret(clientSecret);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void revokeAccounts(SocialAPI api) {
        api.revokeAllAccesses();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void revokePermission(User user, SocialAPI api) {
        api.revokePermission(user);
    }

    public static DateTime getExpirationDate(int expiresIn) { //static because SocialService cannot have subclasses
        return DateTime.now().plusSeconds(expiresIn - EXPIRATION_TIME_CONVERSION_MARGIN);
    }

}
