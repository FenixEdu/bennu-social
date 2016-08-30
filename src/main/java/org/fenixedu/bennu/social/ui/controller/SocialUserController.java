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
package org.fenixedu.bennu.social.ui.controller;

import java.io.IOException;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.social.domain.api.BitbucketAPI;
import org.fenixedu.bennu.social.domain.api.DropboxAPI;
import org.fenixedu.bennu.social.domain.api.FacebookAPI;
import org.fenixedu.bennu.social.domain.api.FlickrAPI;
import org.fenixedu.bennu.social.domain.api.GithubAPI;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.api.LinkedinAPI;
import org.fenixedu.bennu.social.domain.api.SocialAPI;
import org.fenixedu.bennu.social.domain.api.TwitterAPI;
import org.fenixedu.bennu.social.domain.user.BitbucketUser;
import org.fenixedu.bennu.social.domain.user.DropboxUser;
import org.fenixedu.bennu.social.domain.user.FacebookUser;
import org.fenixedu.bennu.social.domain.user.FlickrUser;
import org.fenixedu.bennu.social.domain.user.GithubUser;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.fenixedu.bennu.social.domain.user.LinkedinUser;
import org.fenixedu.bennu.social.domain.user.TwitterUser;
import org.fenixedu.bennu.social.ui.service.BitbucketService;
import org.fenixedu.bennu.social.ui.service.DropboxService;
import org.fenixedu.bennu.social.ui.service.FacebookService;
import org.fenixedu.bennu.social.ui.service.FlickrService;
import org.fenixedu.bennu.social.ui.service.GithubService;
import org.fenixedu.bennu.social.ui.service.GoogleService;
import org.fenixedu.bennu.social.ui.service.LinkedinService;
import org.fenixedu.bennu.social.ui.service.SocialService;
import org.fenixedu.bennu.social.ui.service.TwitterService;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@SpringFunctionality(app = SocialAdminController.class, title = "title.User", accessGroup = "anyone")
@RequestMapping("/bennu-social-user")
public class SocialUserController {

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocialUserController.class);

    @Autowired
    SocialService service;

    @RequestMapping
    public String home(Model model) {

        User user = Authenticate.getUser();
        model.addAttribute("user", user);

        GithubAPI githubAPI = GithubAPI.getInstance();
        model.addAttribute("githubAPI", githubAPI);

        LinkedinAPI linkedinAPI = LinkedinAPI.getInstance();
        model.addAttribute("linkedinAPI", linkedinAPI);

        FacebookAPI facebookAPI = FacebookAPI.getInstance();
        model.addAttribute("facebookAPI", facebookAPI);

        TwitterAPI twitterAPI = TwitterAPI.getInstance();
        model.addAttribute("twitterAPI", twitterAPI);

        FlickrAPI flickrAPI = FlickrAPI.getInstance();
        model.addAttribute("flickrAPI", flickrAPI);

        DropboxAPI dropboxAPI = DropboxAPI.getInstance();
        model.addAttribute("dropboxAPI", dropboxAPI);

        BitbucketAPI bitbucketAPI = BitbucketAPI.getInstance();
        model.addAttribute("bitbucketAPI", bitbucketAPI);

        GoogleAPI googleAPI = GoogleAPI.getInstance();
        model.addAttribute("googleAPI", googleAPI);

        return "bennu-social/user";
    }

    @RequestMapping(value = "/revoke/{api}", method = RequestMethod.GET)
    public String adminSave(@PathVariable("api") SocialAPI api, Model model) {

        service.revokePermission(Authenticate.getUser(), api);

        return "redirect:/bennu-social-user";
    }

    //TODO remove
    @RequestMapping(value = "/githubTest", method = RequestMethod.GET)
    public String githubTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        GithubAPI githubAPI = GithubAPI.getInstance();

        if (githubAPI.getActive() && githubAPI.isConfigured()) {
            if (githubAPI.isUserAuthenticated(user)) {
                GithubUser githubUser = new GithubService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Github API: " + githubUser.getExternalId());
                return "github-social/listRepos";
//                GitHub githubServer = GitHub.connectUsingOAuth(githubUser.get().getAccessToken());
//
//                GHMyself myself = githubServer.getMyself();
//                model.addAttribute("myself", myself);
//
//                List<GHEventInfo> events = githubServer.getEvents();
//                model.addAttribute("events", events);
            } else {
                String authUrl = new GithubService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in github api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Github API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/linkTest", method = RequestMethod.GET)
    public String linkTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        LinkedinAPI linkedinAPI = LinkedinAPI.getInstance();

        if (linkedinAPI.getActive() && linkedinAPI.isConfigured()) {
            if (linkedinAPI.isUserAuthenticated(user)) {
                LinkedinUser linkedinUser = new LinkedinService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Linkedin API: " + linkedinUser.getExternalId());
                return "github-social/listRepos";

            } else {
                String authUrl = new LinkedinService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in Linkedin api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Linkedin API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/fbTest", method = RequestMethod.GET)
    public String fbTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        FacebookAPI facebookAPI = FacebookAPI.getInstance();

        if (facebookAPI.getActive() && facebookAPI.isConfigured()) {
            if (facebookAPI.isUserAuthenticated(user)) {
                FacebookUser facebookUser = new FacebookService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Facebook API: " + facebookUser.getExternalId());
                return "github-social/listRepos";
//                DefaultFacebookClient fbClient = new DefaultFacebookClient(fbUser.getAccessToken(), Version.VERSION_2_4);
//
//                com.restfb.types.User restFbUser = fbClient.fetchObject("me", com.restfb.types.User.class);
//                Page page = fbClient.fetchObject("cocacola", Page.class);
//
//                System.out.println("User name: " + user.getName());
//                System.out.println("Page likes: " + page.getLikes());
            } else {
                String authUrl = facebookAPI.getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in Facebook api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Facebook API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/ttTest", method = RequestMethod.GET)
    public String ttTest(Model model) {

        User user = Authenticate.getUser();

        TwitterAPI twitterAPI = TwitterAPI.getInstance();

        if (twitterAPI.getActive() && twitterAPI.isConfigured()) {
            if (twitterAPI.isUserAuthenticated(user)) {
                TwitterUser twitterUser = new TwitterService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Twitter API: " + twitterUser.getExternalId());
                return "github-social/listRepos";
//
//                try {
//                    Twitter twitter = TwitterFactory.getSingleton();
//                    twitter.setOAuthConsumer(twitterAPI.getClientId(), twitterAPI.getClientSecret());
//                    AccessToken accessToken = new AccessToken(twitterUser.getAccessToken(), twitterUser.getTokenSecret());
//                    twitter.setOAuthAccessToken(accessToken);
//                    ResponseList<Status> favs = twitter.getFavorites(twitter.getScreenName());
//
//                    favs.forEach(f -> {
//                        System.out.println("FAV: " + f.getText());
//                    });
//
//                } catch (IllegalStateException | TwitterException e) {
//                    e.printStackTrace();
//                }
            } else {
                String authUrl = new TwitterService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in Twitter api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Twitter API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/flkTest", method = RequestMethod.GET)
    public String flkTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        FlickrAPI flickrAPI = new FlickrService().getInstance();

        if (flickrAPI.getActive() && flickrAPI.isConfigured()) {
            if (flickrAPI.isUserAuthenticated(user)) {
                FlickrUser flickrUser = new FlickrService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Flickr API: " + flickrUser.getExternalId());
                return "github-social/listRepos";
            } else {
                String authUrl = new FlickrService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in Flickr api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Flickr API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/dropTest", method = RequestMethod.GET)
    public String dropTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        DropboxAPI dropboxAPI = DropboxAPI.getInstance();

        if (dropboxAPI.getActive() && dropboxAPI.isConfigured()) {
            if (dropboxAPI.isUserAuthenticated(user)) {
                DropboxUser dropboxUser = new DropboxService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Dropbox API: " + dropboxUser.getExternalId());
                return "github-social/listRepos";
            } else {
                String authUrl = new DropboxService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in dropbox api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Dropbox API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/bitTest", method = RequestMethod.GET)
    public String bitTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        BitbucketAPI bitbucketAPI = BitbucketAPI.getInstance();

        if (bitbucketAPI.getActive()) {
            if (bitbucketAPI.isUserAuthenticated(user)) {
                BitbucketUser bitbucketUser = new BitbucketService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Bitbucket API: " + bitbucketUser.getExternalId());
                return "github-social/listRepos";

            } else {
                String authUrl = new BitbucketService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in bitbucket api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Bitbucket API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }

    @RequestMapping(value = "/googleTest", method = RequestMethod.GET)
    public String googleTest(Model model) throws IOException {

        User user = Authenticate.getUser();

        GoogleAPI googleAPI = GoogleAPI.getInstance();

        if (googleAPI.getActive()) {
            if (googleAPI.isUserAuthenticated(user)) {
                GoogleUser googleUser = new GoogleService().getAuthenticatedUser(user);
                LOGGER.info("user authenticated in Google API: " + googleUser.getExternalId());
                return "github-social/listRepos";

            } else {
                String authUrl = new GoogleService().getAuthenticationUrlForUser(user);
                LOGGER.info("Current user is not authenticated in google api. Redirecting to authorization link: " + authUrl);
                return "redirect:" + authUrl;
            }
        } else {
            LOGGER.info("Google API is disabled or not correctly configured");
            return "redirect:/bennu-social";
        }
    }
}
