package com.mediaplatform.social;

import com.mediaplatform.model.User;
import com.mediaplatform.model.UserInfo;
import com.mediaplatform.security.PlatformAuthenticator;
import org.apache.commons.lang.StringUtils;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.social.Twitter;
import org.jboss.seam.social.oauth.OAuthApplication;
import org.jboss.seam.social.twitter.TwitterService;
import org.jboss.seam.social.twitter.jackson.TwitterServiceJackson;
import org.jboss.seam.social.twitter.model.TwitterProfile;
import org.jboss.solder.logging.Logger;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/16/12
 * Time: 4:34 PM
 */
@Named
@SessionScoped
public class TwitterBean implements Serializable {
    @Inject
    private Identity identity;

    @Inject
    private Logger log;

    @Inject
    @Twitter
    private TwitterService service;

    @Produces
    @Named("twUser")
    private TwitterProfile twUser;

    @OAuthApplication(apiKey = "YajUl0D4ZJhfXawKwNjWzQ", apiSecret = "QWi1vHsJzsInr4Y3Bcfv7LcTiCoHB3n9hjsfggKA8", callback = "http://timur.asuscomm.com:8080/media-platform/social/twitterCallback.xhtml")
    @Twitter
    @Produces
    public TwitterService twitterServiceProducer(TwitterService ts) {
        return ts;
    }

    @Inject
    protected EntityManager appEm;

    @Inject
    private Messages messages;

    public TwitterService getService() {
        return service;
    }

    public String getFullName() {
        TwitterProfile user = service.getMyProfile();
        String fullName = user.getFullName();
        return fullName;
    }

    public void login(){
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            service.resetConnection();
            externalContext.redirect(service.getAuthorizationUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void authenticate() {
        try {
            service.initAccessToken();
        } catch (Exception e) {
            String message = "Can't authenticate via twitter account";
            log.error(message, e);
            messages.error(message);
            redirectToLoginPage();
            return;
        }
        twUser = service.getMyProfile();
        User user = appEm.find(User.class, twUser.getScreenName());
        if (user == null) {
            user = new User();
            //TODO убедиться что обеспечиваем уникальность
            user.setUsername(twUser.getScreenName());
            user.setName(twUser.getName());
            UserInfo userInfo = new UserInfo(twUser);
            appEm.persist(userInfo);
            appEm.persist(user);
            userInfo.setUser(user);
            user.setUserInfo(userInfo);
            appEm.merge(user);
            appEm.merge(userInfo);
        }
        if (Identity.RESPONSE_LOGIN_SUCCESS.equals(identity.login())) {
            redirectToHomePage();
        } else {
            messages.error("Login via twitter account is failed");
            redirectToLoginPage();
        }
    }

    private void redirectToLoginPage() {
       redirect("login");
    }

    private void redirectToHomePage() {
        redirect("");
    }

    private void redirect(String page) {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String redirect = externalContext.getRequestContextPath() + "/" + page;
            externalContext.redirect(redirect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
