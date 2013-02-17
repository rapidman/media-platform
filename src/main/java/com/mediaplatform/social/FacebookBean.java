package com.mediaplatform.social;

import com.mediaplatform.model.User;
import com.mediaplatform.model.UserInfo;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.seam.social.Facebook;
import org.jboss.seam.social.UserProfile;
import org.jboss.seam.social.facebook.FacebookService;
import org.jboss.seam.social.oauth.OAuthApplication;
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
@Named("facebookBean")
@SessionScoped
public class FacebookBean extends AbstractSocialBean {

    @Inject
    @Facebook
    private FacebookService service;

    @Produces
    @Named("fbUser")
    private UserProfile fbUser;

    @OAuthApplication(apiKey = "135636733179752", apiSecret = "9fd8e67d6a8ed31d606aeac39e3e0189", callback = "http://videoblog.tomsk.ru:8080/media-platform/social/facebookCallback.xhtml")
    @Facebook
    @Produces
    public FacebookService facebookServiceProducer(FacebookService fs) {
        return fs;
    }

    public FacebookService getService() {
        return service;
    }

    public String getFullName() {
        UserProfile user = service.getMyProfile();
        String fullName = user.getFullName();
        return fullName;
    }

    @Override
    protected String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    @Override
    protected User findUser() {
        fbUser = service.getMyProfile();
        return userManagerInstance.get().findByUsername(fbUser.getId());
    }

    @Override
    protected UserInfo createUserInfo() {
        return new UserInfo(fbUser);
    }

    @Override
    protected void assignProps(User user) {
        user.setUsername(fbUser.getId());
        user.setName(fbUser.getFullName());
    }

    @Override
    protected void initAccessToken() {
        service.initAccessToken();
    }

    public void reset(){
        fbUser = null;
    }

}
