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

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.fenixedu.bennu.social.domain.api.BitbucketAPI;
import org.fenixedu.bennu.social.domain.api.DropboxAPI;
import org.fenixedu.bennu.social.domain.api.FacebookAPI;
import org.fenixedu.bennu.social.domain.api.FlickrAPI;
import org.fenixedu.bennu.social.domain.api.GithubAPI;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.api.LinkedinAPI;
import org.fenixedu.bennu.social.domain.api.SocialAPI;
import org.fenixedu.bennu.social.domain.api.TwitterAPI;
import org.fenixedu.bennu.social.ui.service.BitbucketService;
import org.fenixedu.bennu.social.ui.service.DropboxService;
import org.fenixedu.bennu.social.ui.service.FacebookService;
import org.fenixedu.bennu.social.ui.service.FlickrService;
import org.fenixedu.bennu.social.ui.service.GithubService;
import org.fenixedu.bennu.social.ui.service.GoogleService;
import org.fenixedu.bennu.social.ui.service.LinkedinService;
import org.fenixedu.bennu.social.ui.service.SocialService;
import org.fenixedu.bennu.social.ui.service.TwitterService;
import org.fenixedu.bennu.spring.portal.SpringApplication;
import org.fenixedu.bennu.spring.portal.SpringFunctionality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@SpringApplication(group = "anyone", path = "bennu-social-admin", title = "title.BennuSocial")
@SpringFunctionality(app = SocialAdminController.class, title = "title.BennuSocial", accessGroup = "#managers")
@RequestMapping("/bennu-social-admin")
public class SocialAdminController { //TODO confirmation/error messages

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocialAdminController.class);

    @Autowired
    SocialService service;

    @Autowired
    GithubService githubService;

    @Autowired
    LinkedinService linkedinService;

    @Autowired
    FacebookService facebookService;

    @Autowired
    TwitterService twitterService;

    @Autowired
    FlickrService flickrService;

    @Autowired
    DropboxService dropboxService;

    @Autowired
    BitbucketService bitbucketService;

    @Autowired
    GoogleService googleService;

    @RequestMapping
    public String admin(Model model) {
        GithubAPI github = githubService.getInstance();
        LOGGER.info("Adding Github API to admin interface (" + (github != null) + ")");
        model.addAttribute("github", github);

        LinkedinAPI linkedin = linkedinService.getInstance();
        LOGGER.info("Adding Linkedin API to admin interface (" + (linkedin != null) + ")");
        model.addAttribute("linkedin", linkedin);

        FacebookAPI facebook = facebookService.getInstance();
        LOGGER.info("Adding Facebook API to admin interface (" + (facebook != null) + ")");
        model.addAttribute("facebook", facebook);

        TwitterAPI twitter = twitterService.getInstance();
        LOGGER.info("Adding Twitter API to admin interface (" + (twitter != null) + ")");
        model.addAttribute("twitter", twitter);

        FlickrAPI flickr = flickrService.getInstance();
        LOGGER.info("Adding Flickr API to admin interface (" + (flickr != null) + ")");
        model.addAttribute("flickr", flickr);

        DropboxAPI dropbox = dropboxService.getInstance();
        LOGGER.info("Adding Dropbox API to admin interface (" + (dropbox != null) + ")");
        model.addAttribute("dropbox", dropbox);

        BitbucketAPI bitbucket = bitbucketService.getInstance();
        LOGGER.info("Adding Bitbucket API to admin interface (" + (bitbucket != null) + ")");
        model.addAttribute("bitbucket", bitbucket);

        GoogleAPI google = googleService.getInstance();
        LOGGER.info("Adding Google API to admin interface (" + (google != null) + ")");
        model.addAttribute("google", google);

        return "bennu-social/admin";
    }

    @RequestMapping(value = "/admin-save", method = RequestMethod.POST)
    public String adminSave(@RequestParam(required = false) boolean githubapiActive,
            @RequestParam(required = false) boolean linkedinapiActive, @RequestParam(required = false) boolean facebookapiActive,
            @RequestParam(required = false) boolean twitterapiActive, @RequestParam(required = false) boolean flickrapiActive,
            @RequestParam(required = false) boolean dropboxapiActive, @RequestParam(required = false) boolean bitbucketapiActive,
            @RequestParam(required = false) boolean googleapiActive, Model model) {

        GithubAPI githubAPI = githubService.getInstance();
        service.activateAPI(githubAPI, githubapiActive);

        LinkedinAPI linkedinAPI = linkedinService.getInstance();
        service.activateAPI(linkedinAPI, linkedinapiActive);

        FacebookAPI facebookAPI = facebookService.getInstance();
        service.activateAPI(facebookAPI, facebookapiActive);

        TwitterAPI twitterAPI = twitterService.getInstance();
        service.activateAPI(twitterAPI, twitterapiActive);

        FlickrAPI flickrAPI = flickrService.getInstance();
        service.activateAPI(flickrAPI, flickrapiActive);

        DropboxAPI dropboxAPI = dropboxService.getInstance();
        service.activateAPI(dropboxAPI, dropboxapiActive);

        BitbucketAPI bitbucketAPI = bitbucketService.getInstance();
        service.activateAPI(bitbucketAPI, bitbucketapiActive);

        GoogleAPI googleAPI = googleService.getInstance();
        service.activateAPI(googleAPI, googleapiActive);

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/saveActive/{api}", method = RequestMethod.POST)
    public ResponseBuilder saveActive(@PathVariable("api") SocialAPI api, @QueryParam(value = "active") boolean active,
            Model model) {

        LOGGER.info("Saving api active for " + api.getClass().getName() + ": active=" + active);

        service.activateAPI(api, active);

        return Response.ok();
    }

    @RequestMapping(value = "/configAPI/{api}", method = RequestMethod.POST)
    public String configApiSave(@PathVariable("api") SocialAPI api, @QueryParam(value = "clientId") String clientId, @QueryParam(
            value = "clientSecret") String clientSecret, Model model) {

        LOGGER.info("Saving api configuration for " + api.getClass().getName() + ": clientId=" + clientId + " && clientSecret="
                + clientSecret);

        service.setAPICredentials(api, clientId, clientSecret);
        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/revokeAll/{api}", method = RequestMethod.GET)
    public String revokeAll(@PathVariable("api") SocialAPI api, Model model) {

        LOGGER.info("Revoking all accesses for API " + api.getClass().getName());

        service.revokeAccounts(api);

        model.addAttribute("api", api);
        return "redirect:/bennu-social-admin";
    }
}
