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
public @interface RequiresDropbox {

    /*
     * Scopes should be used in a way the user can configure correctly the application on Dropbox interface.
     * Bad configuration may lead to unexpected errors
     * See https://www.dropbox.com/developers/reference/devguide#app-permissions (link may be outdated)
     */
    DropboxScopes[] scopes() default {};

    public enum DropboxScopes {

        OWN_FOLDER,

        ALL_FOLDERS,

        ALL_FILE_TYPES,

        SPECIFIC_FILE_TYPES,

        TEXT_FILE,

        DOC_FILE,

        IMAGE_FILE,

        VIDEO_FILE,

        AUDIO_FILE,

        EBOOKS;

        public String getQualifiedName() {
            return DropboxScopes.class.getSimpleName() + "." + name();
        }
    }
}
