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
@Named("twitterBean")
@SessionScoped
public class TwitterBean extends AbstractSocialBean {
    @Inject
    @Twitter
    private TwitterService service;

    @Produces
    @Named("twUser")
    private TwitterProfile twUser;

    @OAuthApplication(apiKey = "YajUl0D4ZJhfXawKwNjWzQ", apiSecret = "QWi1vHsJzsInr4Y3Bcfv7LcTiCoHB3n9hjsfggKA8", callback = "http://videoblog.tomsk.ru:8080/media-platform/social/twitterCallback.xhtml")
    @Twitter
    @Produces
    public TwitterService twitterServiceProducer(TwitterService ts) {
        return ts;
    }

    public String getFullName() {
        TwitterProfile user = service.getMyProfile();
        String fullName = user.getFullName();
        return fullName;
    }

    @Override
    protected String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    @Override
    protected User findUser() {
        twUser = service.getMyProfile();
        return userManagerInstance.get().findByUsername(twUser.getScreenName());
    }

    @Override
    protected UserInfo createUserInfo() {
        return new UserInfo(twUser);
    }

    @Override
    protected void assignProps(User user) {
        user.setUsername(twUser.getScreenName());
        user.setName(twUser.getName());
    }

    @Override
    protected void initAccessToken() {
        service.initAccessToken();
    }

    public TwitterService getService() {
        return service;
    }

    public void reset(){
        twUser = null;
    }
}
