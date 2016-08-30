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
public @interface RequiresBitbucket {

    /*
     * Scopes should be used in a way the user can configure correctly the application on Bitbucket interface.
     * Bad configuration may lead to unexpected errors
     * See https://confluence.atlassian.com/display/BITBUCKET/Integrate+another+application+through+OAuth (link may be outdated)
     */
    BitbucketScopes[] scopes() default {};

    public enum BitbucketScopes {

        ACCOUNT_EMAIL,

        ACCOUNT_R,

        ACCOUNT_W,

        TEAM_R,

        TEAM_W,

        REPO_R,

        REPO_W,

        REPO_ADMIN,

        PULL_R,

        PULL_W,

        ISSUES_R,

        ISSUES_W,

        WIKI,

        SNIPPETS_R,

        SNIPPETS_W,

        WEBHOOKS;

        public String getQualifiedName() {
            return BitbucketScopes.class.getSimpleName() + "." + name();
        }
    }
}
