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

import java.util.Optional;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.social.domain.api.FlickrAPI;
import org.fenixedu.bennu.social.domain.user.FlickrUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
import org.scribe.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pt.ist.fenixframework.Atomic;

@Service
public class FlickrService {

    protected static final Logger LOGGER = LoggerFactory.getLogger(FlickrService.class);

    @Atomic(mode = Atomic.TxMode.WRITE)
    public FlickrAPI getInstance() {
        return FlickrAPI.getInstance();
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public String getAuthenticationUrlForUser(User user) {
        return getInstance().getAuthenticationUrlForUser(user);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public void parseResponse(FlickrUser flickrUser, Token accessToken) throws AccessTokenNotProvidedException {

        String token = accessToken.getToken();
        String tokenSecret = accessToken.getSecret();

        LOGGER.info("Access token request answer from Flickr: Token=" + token + " and TokenSecret=" + tokenSecret);

        if (token == null) {
            LOGGER.error("Access token has not been returned by Flickr API");
            throw new AccessTokenNotProvidedException(null);
        }

        flickrUser.setAccessToken(token);
        flickrUser.setAccessTokenSecret(tokenSecret);
    }

    @Atomic(mode = Atomic.TxMode.WRITE)
    public FlickrUser getAuthenticatedUser(User user) {
        Optional<FlickrUser> flickrUser = getInstance().getAuthenticatedUser(user);

        if (!flickrUser.isPresent()) {
            return new FlickrUser(user);
        }

        return flickrUser.get();
    }

}
