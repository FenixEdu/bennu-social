package org.fenixedu.bennu.social.ui.controller;

import java.util.Arrays;

import org.fenixedu.bennu.core.domain.User;
import org.fenixedu.bennu.core.security.Authenticate;
import org.fenixedu.bennu.social.domain.api.BitbucketAPI;
import org.fenixedu.bennu.social.domain.api.DropboxAPI;
import org.fenixedu.bennu.social.domain.api.FacebookAPI;
import org.fenixedu.bennu.social.domain.api.FlickrAPI;
import org.fenixedu.bennu.social.domain.api.GithubAPI;
import org.fenixedu.bennu.social.domain.api.GoogleAPI;
import org.fenixedu.bennu.social.domain.api.LinkedinAPI;
import org.fenixedu.bennu.social.domain.api.TwitterAPI;
import org.fenixedu.bennu.social.domain.user.BitbucketUser;
import org.fenixedu.bennu.social.domain.user.DropboxUser;
import org.fenixedu.bennu.social.domain.user.FacebookUser;
import org.fenixedu.bennu.social.domain.user.FlickrUser;
import org.fenixedu.bennu.social.domain.user.GithubUser;
import org.fenixedu.bennu.social.domain.user.GoogleUser;
import org.fenixedu.bennu.social.domain.user.LinkedinUser;
import org.fenixedu.bennu.social.domain.user.TwitterUser;
import org.fenixedu.bennu.social.exception.AccessTokenNotProvidedException;
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
import org.scribe.model.Token;
import org.scribe.model.Verifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.REST;

@SpringFunctionality(app = SocialAdminController.class, title = "title.Callback", accessGroup = "anyone")
@RequestMapping("/bennu-social")
public class SocialCallbackController { //TODO confirmation/error messages
    /*
     * Controller menu entry should not be visible
     */

    private static final String API_INVALID_STATE_LABEL_CODE = "api.invalid.state";

    protected static final Logger LOGGER = LoggerFactory.getLogger(SocialCallbackController.class);

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

    @RequestMapping(value = "/github/callback", method = RequestMethod.GET)
    public String github(Model model, RedirectAttributes redirectAttrs, @RequestParam(value = "state") String state,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error, @RequestParam(value = "error_description",
                    required = false) String errorDescription,
            @RequestParam(value = "error_uri", required = false) String errorUri) {

        LOGGER.info("received invocation at github callback endpoint w/ params: state=" + state + ", code=" + code + ", error="
                + error + ", error_description=" + errorDescription + ", error_uri=" + errorUri);

        GithubAPI githubAPI = githubService.getInstance();

        if (!githubAPI.isConfigured()) {
            LOGGER.error("Received invocation at Github callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.github"));
        } else {
            User user = Authenticate.getUser();

            GithubUser githubUser = githubService.getAuthenticatedUser(user);

            if (githubUser.getState().equals(state)) {

                if (code != null) {
                    ResponseEntity<String> rsp = githubService.makeAccessTokenRequest(code);
                    try {
                        githubService.parseResponse(githubUser, rsp);
                    } catch (AccessTokenNotProvidedException e) {
                        e.printStackTrace();
                        redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                    }
                }

                if (error != null) {
                    String errorMessage = error + " - " + errorDescription + ". More info at " + errorUri;
                    LOGGER.info("Received error from Github server: " + errorMessage);
                    redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
                }

            } else {
                LOGGER.error("Github api callback returned with erroneous state parameter. Expected: " + githubUser.getState()
                        + ". Found: " + state);
                redirectAttrs.addFlashAttribute("errors", Arrays.asList(API_INVALID_STATE_LABEL_CODE));
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/linkedin/callback", method = RequestMethod.GET)
    public String linkedin(Model model, RedirectAttributes redirectAttrs,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error, @RequestParam(value = "error_description",
                    required = false) String errorDescription) {

        LOGGER.info("received invocation at linkedin callback endpoint w/ params: state=" + state + ", code=" + code + ", error="
                + error + ", error_description=" + errorDescription);

        LinkedinAPI linkedinAPI = linkedinService.getInstance();

        if (!linkedinAPI.isConfigured()) {
            LOGGER.error("Received invocation at Linkedin callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.linkedin"));
        } else {

            if (error != null) {
                String errorMessage = "Received error from Linkedin server: " + error + " - " + errorDescription;
                LOGGER.info(errorMessage);
                redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
            } else {
                User user = Authenticate.getUser();

                LinkedinUser linkedinUser = linkedinService.getAuthenticatedUser(user);

                if (linkedinUser.getState().equals(state)) {

                    ResponseEntity<String> rsp = linkedinService.makeAccessTokenRequest(code);

                    try {
                        linkedinService.parseResponse(linkedinUser, rsp);
                    } catch (AccessTokenNotProvidedException e) {
                        e.printStackTrace();
                        redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                    }

                } else {
                    LOGGER.error("Linkedin api callback returned with erroneous state parameter. Expected: "
                            + linkedinUser.getState() + ". Found: " + state);
                    redirectAttrs.addFlashAttribute("errors", Arrays.asList(API_INVALID_STATE_LABEL_CODE));
                }
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/facebook/callback", method = RequestMethod.GET)
    public String facebook(Model model, RedirectAttributes redirectAttrs,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error_reason", required = false) String errorReason, @RequestParam(value = "error",
                    required = false) String error,
            @RequestParam(value = "error_description", required = false) String errorDescription) {

        LOGGER.info("received invocation at facebook callback endpoint w/ params: state=" + state + ", code=" + code + ", error="
                + error + ", error_reason=" + errorReason + ", error_description=" + errorDescription);

        FacebookAPI facebookAPI = facebookService.getInstance();

        if (!facebookAPI.isConfigured()) {
            LOGGER.error("Received invocation at Facebook callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.facebook"));
        } else {
            if (error != null) {
                String errorMessage =
                        "Received error from Facebook server: " + error + " - " + errorReason + "; " + errorDescription;
                LOGGER.info(errorMessage);
                redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
            } else {

                User user = Authenticate.getUser();

                FacebookUser facebookUser = facebookService.getAuthenticatedUser(user);

                if (facebookUser.getState().equals(state)) {

                    ResponseEntity<String> rsp = facebookService.makeAccessTokenRequest(code);

                    try {
                        facebookService.parseResponse(facebookUser, rsp);
                    } catch (AccessTokenNotProvidedException e) {
                        e.printStackTrace();
                        redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                    }

                } else {
                    LOGGER.error("Facebook api callback returned with erroneous state parameter. Expected: "
                            + facebookUser.getState() + ". Found: " + state);
                    redirectAttrs.addFlashAttribute("errors", Arrays.asList(API_INVALID_STATE_LABEL_CODE));
                }
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/twitter/callback", method = RequestMethod.GET)
    public String twitter(Model model, RedirectAttributes redirectAttrs,
            @RequestParam(value = "oauth_token", required = false) String oauthToken, @RequestParam(value = "oauth_verifier",
                    required = false) String oauthVerifier, @RequestParam(value = "denied", required = false) String denied) {

        LOGGER.info("received invocation at twitter callback endpoint w/ params: oauth_token=" + oauthToken + ", oauth_verifier="
                + oauthVerifier + ", denied=" + denied);

        TwitterAPI twitterAPI = twitterService.getInstance();

        if (!twitterAPI.isConfigured()) {
            LOGGER.error("Received invocation at Twitter callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.twitter"));
        } else {

            if (denied != null) {
                String errorMessage = "Received error from Twitter server: " + denied;
                LOGGER.info(errorMessage);
                redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
            } else {
                try {
                    User user = Authenticate.getUser();
                    TwitterUser twitterUser = twitterService.getAuthenticatedUser(user);

                    Twitter instance = new TwitterFactory().getInstance();
                    instance.setOAuthConsumer(twitterAPI.getClientId(), twitterAPI.getClientSecret());
                    RequestToken requestToken = new RequestToken(twitterUser.getOauthToken(), twitterUser.getOauthTokenSecret());
                    AccessToken oAuthAccessToken = instance.getOAuthAccessToken(requestToken, oauthVerifier);

                    twitterService.parseResponse(twitterUser, oAuthAccessToken);
                } catch (TwitterException | AccessTokenNotProvidedException e) {
                    e.printStackTrace();
                    redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                }
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/flickr/callback", method = RequestMethod.GET)
    public String flickr(Model model, RedirectAttributes redirectAttrs, @RequestParam("oauth_token") String oauthToken,
            @RequestParam("oauth_verifier") String oauthVerifier) {

        LOGGER.info("received invocation at flickr callback endpoint w/ params: oauth_token=" + oauthToken
                + " and oauth_verifier=" + oauthVerifier);

        FlickrAPI flickrAPI = FlickrAPI.getInstance();

        if (!flickrAPI.isConfigured()) {
            LOGGER.error("Received invocation at Flickr callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.flickr"));
        } else {
            User user = Authenticate.getUser();

            FlickrUser flickrUser = flickrService.getAuthenticatedUser(user);

            String apiKey = flickrAPI.getClientId();
            String sharedSecret = flickrAPI.getClientSecret();
            Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());

            Token oAuthRequestToken = new Token(flickrUser.getToken(), flickrUser.getSecret());
            Verifier verifier = new Verifier(oauthVerifier);
            Token accessToken = flickr.getAuthInterface().getAccessToken(oAuthRequestToken, verifier);

            LOGGER.info("Received access token for user " + flickrUser + " : " + accessToken);

            try {
                flickrService.parseResponse(flickrUser, accessToken);
            } catch (AccessTokenNotProvidedException e) {
                e.printStackTrace();
                redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/dropbox/callback", method = RequestMethod.GET)
    public String dropbox(Model model, RedirectAttributes redirectAttrs, @RequestParam("state") String state, @RequestParam(
            value = "code", required = false) String code,
            @RequestParam(value = "error_description", required = false) String errorDescription, @RequestParam(value = "error",
                    required = false) String error) {

        LOGGER.info("received invocation at dropbox callback endpoint w/ params: state=" + state + ", code=" + code + ", error="
                + error + ", error_description=" + errorDescription);

        DropboxAPI dropboxAPI = dropboxService.getInstance();

        if (!dropboxAPI.isConfigured()) {
            LOGGER.error("Received invocation at Dropbox callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.dropbox"));
        } else {
            User user = Authenticate.getUser();

            DropboxUser dropboxUser = dropboxService.getAuthenticatedUser(user);

            if (dropboxUser.getState().equals(state)) {

                if (error != null) {
                    String errorMessage = "Received error from Dropbox server: " + error + " - " + errorDescription;
                    LOGGER.info(errorMessage);
                    redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
                } else {
                    ResponseEntity<String> rsp = dropboxService.makeAccessTokenRequest(code);
                    try {
                        dropboxService.parseResponse(dropboxUser, rsp);
                    } catch (AccessTokenNotProvidedException e) {
                        e.printStackTrace();
                        redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                    }
                }
            } else {
                LOGGER.error("Dropbox api callback returned with erroneous state parameter. Expected: " + dropboxUser.getState()
                        + ". Found: " + state);
                redirectAttrs.addFlashAttribute("errors", Arrays.asList(API_INVALID_STATE_LABEL_CODE));
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/bitbucket/callback", method = RequestMethod.GET)
    public String bitbucket(Model model, RedirectAttributes redirectAttrs,
            @RequestParam(value = "code", required = false) String code, @RequestParam(value = "error_description",
                    required = false) String errorDescription, @RequestParam(value = "error", required = false) String error) {

        LOGGER.info("received invocation at bitbucket callback endpoint w/ params: code=" + code + ", error=" + error
                + ", error_description=" + errorDescription);

        BitbucketAPI bitbucketAPI = bitbucketService.getInstance();

        if (!bitbucketAPI.isConfigured()) {
            LOGGER.error("Received invocation at Bitbucket callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.bitbucket"));
        } else {

            if (error != null) {
                String errorMessage = "Received error from Bitbucket server: " + error + " - " + errorDescription;
                LOGGER.info(errorMessage);
                redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
            } else {
                User user = Authenticate.getUser();

                BitbucketUser bitbucketUser = bitbucketService.getAuthenticatedUser(user);

                ResponseEntity<String> rsp = bitbucketService.makeAccessTokenRequest(code);

                try {
                    bitbucketService.parseResponse(bitbucketUser, rsp);
                } catch (AccessTokenNotProvidedException e) {
                    e.printStackTrace();
                    redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                }
            }
        }

        return "redirect:/bennu-social-admin";
    }

    @RequestMapping(value = "/google/callback", method = RequestMethod.GET)
    public String google(Model model, RedirectAttributes redirectAttrs,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "error", required = false) String error) {

        LOGGER.info("received invocation at google callback endpoint w/ params: code=" + code + ", error=" + error);

        GoogleAPI googleAPI = googleService.getInstance();

        if (!googleAPI.isConfigured()) {
            LOGGER.error("Received invocation at Google callback endpoint, but service not yet configured");
            redirectAttrs.addFlashAttribute("errors", Arrays.asList("api.not.configured.google"));
        } else {

            if (error != null) {
                String errorMessage = "Received error from Google server: " + error;
                LOGGER.info(errorMessage);
                redirectAttrs.addFlashAttribute("errorMessage", errorMessage);
            } else {
                User user = Authenticate.getUser();

                GoogleUser googleUser = googleService.getAuthenticatedUser(user);

                ResponseEntity<String> rsp = googleService.makeAccessTokenRequest(code);

                try {
                    googleService.parseResponse(googleUser, rsp);
                } catch (AccessTokenNotProvidedException e) {
                    e.printStackTrace();
                    redirectAttrs.addFlashAttribute("errors", Arrays.asList(e.getClass().getSimpleName()));
                }
            }
        }

        return "redirect:/bennu-social-admin";
    }

}
