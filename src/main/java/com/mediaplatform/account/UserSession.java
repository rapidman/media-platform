package com.mediaplatform.account;

import org.agorava.facebook.model.FacebookProfile;
import org.agorava.twitter.model.TwitterProfile;
import org.jboss.seam.security.events.LoggedInEvent;
import org.jboss.seam.security.events.LoginFailedEvent;

import javax.ejb.Stateful;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Model;

/**
 * User: timur
 * Date: 11/29/12
 * Time: 10:59 AM
 */
@Stateful
@Model
public class UserSession {
    private boolean loginFailed = false;
    private FacebookProfile fbUser;

    private TwitterProfile twUser;

    private String userProfileImageUrl;

    public boolean isLoginFailed() {
        return loginFailed;
    }

    public void setLoginFailed(boolean loginFailed) {
        this.loginFailed = loginFailed;
    }

    public void observeLoginFailed(@Observes LoginFailedEvent event){
        loginFailed = true;
    }

    public void observeLoginOk(@Observes LoggedInEvent event){
        loginFailed = false;
    }

    public FacebookProfile getFbUser() {
        return fbUser;
    }

    public void setFbUser(FacebookProfile fbUser) {
        this.fbUser = fbUser;
        userProfileImageUrl = fbUser.getProfileImageUrl();
    }

    public TwitterProfile getTwUser() {
        return twUser;
    }

    public void setTwUser(TwitterProfile twUser) {
        this.twUser = twUser;
        userProfileImageUrl = twUser.getProfileImageUrl();
    }

    public void clearSocial() {
        twUser = null;
        fbUser = null;
        userProfileImageUrl = null;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }
}
