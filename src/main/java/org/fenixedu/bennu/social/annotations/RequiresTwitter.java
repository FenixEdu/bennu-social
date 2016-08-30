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
package org.fenixedu.bennu.social.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiresTwitter {

    /*
     * Scopes should be used in a way the user can configure correctly the application on Twitter interface.
     * Bad configuration may lead to unexpected errors
     * See https://dev.twitter.com/oauth/overview/application-permission-model (link may be outdated)
     */
    TwitterScopes scope() default TwitterScopes.READ;

    public enum TwitterScopes {

        READ,

        WRITE,

        MESSAGES;

        public String getQualifiedName() {
            return TwitterScopes.class.getSimpleName() + "." + name();
        }
    }
}
