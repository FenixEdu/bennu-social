package org.fenixedu.bennu.social.domain;

import org.fenixedu.bennu.social.annotations.*;
import org.fenixedu.bennu.social.domain.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.util.Arrays;
import java.util.Set;

@HandlesTypes({ RequiresGithub.class, RequiresLinkedin.class, RequiresBitbucket.class, RequiresDropbox.class,
        RequiresFacebook.class, RequiresGoogle.class, RequiresTwitter.class })
public class SocialInitializer implements ServletContainerInitializer {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocialInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
        LOGGER.info("Appending scopes to Social APIs");

        if (c != null) {
            for (Class<?> type : c) {
                if (type.isAnnotationPresent(RequiresGithub.class)) {
                    RequiresGithub annotation = type.getAnnotation(RequiresGithub.class);

                    LOGGER.info("Appending scopes to GithubAPI through " + type.getName() + " : "
                            + GithubAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    GithubAPI.appendScopes(annotation.scopes());
                }

                if (type.isAnnotationPresent(RequiresLinkedin.class)) {
                    RequiresLinkedin annotation = type.getAnnotation(RequiresLinkedin.class);

                    LOGGER.info("Appending scopes to LinkedinAPI through " + type.getName() + " : "
                            + LinkedinAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    LinkedinAPI.appendScopes(annotation.scopes());
                }

                if (type.isAnnotationPresent(RequiresFacebook.class)) {
                    RequiresFacebook annotation = type.getAnnotation(RequiresFacebook.class);

                    LOGGER.info("Appending scopes to FacebookAPI through " + type.getName() + " : "
                            + FacebookAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    FacebookAPI.appendScopes(annotation.scopes());
                }

                if (type.isAnnotationPresent(RequiresTwitter.class)) {
                    RequiresTwitter annotation = type.getAnnotation(RequiresTwitter.class);

                    LOGGER.info("Appending scopes to TwitterAPI through " + type.getName() + " : "
                            + TwitterAPI.makeScopes(Arrays.asList(annotation.scope())));
                    TwitterAPI.appendScopes(annotation.scope());
                }

                if (type.isAnnotationPresent(RequiresBitbucket.class)) {
                    RequiresBitbucket annotation = type.getAnnotation(RequiresBitbucket.class);

                    LOGGER.info("Appending scopes to BitbucketAPI through " + type.getName() + " : "
                            + BitbucketAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    BitbucketAPI.appendScopes(annotation.scopes());
                }

                if (type.isAnnotationPresent(RequiresDropbox.class)) {
                    RequiresDropbox annotation = type.getAnnotation(RequiresDropbox.class);

                    LOGGER.info("Appending scopes to DropboxAPI through " + type.getName() + " : "
                            + DropboxAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    DropboxAPI.appendScopes(annotation.scopes());
                }

                if (type.isAnnotationPresent(RequiresFlickr.class)) {
                    RequiresFlickr annotation = type.getAnnotation(RequiresFlickr.class);

                    LOGGER.info("Appending scopes to FlickrAPI through " + type.getName() + " : "
                            + FlickrAPI.makeScopes(Arrays.asList(annotation.scope())));
                    FlickrAPI.appendScopes(annotation.scope());
                }

                if (type.isAnnotationPresent(RequiresGoogle.class)) {
                    RequiresGoogle annotation = type.getAnnotation(RequiresGoogle.class);

                    LOGGER.info("Appending scopes to GoogleAPI through " + type.getName() + " : "
                            + GoogleAPI.makeScopes(Arrays.asList(annotation.scopes())));
                    GoogleAPI.appendScopes(annotation.scopes());
                }
            }

            GithubAPI.ensureConsistentScope();
            LinkedinAPI.ensureConsistentScope();
            FacebookAPI.ensureConsistentScope();
            TwitterAPI.ensureConsistentScope();
            BitbucketAPI.ensureConsistentScope();
            DropboxAPI.ensureConsistentScope();
            FlickrAPI.ensureConsistentScope();
            GoogleAPI.ensureConsistentScope();
        }
    }
}