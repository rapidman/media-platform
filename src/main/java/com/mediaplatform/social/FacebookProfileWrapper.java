package com.mediaplatform.social;

import org.agorava.facebook.model.FacebookProfile;

import javax.inject.Named;
import java.io.Serializable;

/**
 * User: timur
 * Date: 1/12/14
 * Time: 11:58 PM
 */
public class FacebookProfileWrapper implements Serializable{
    private FacebookProfile fbUser;

    public FacebookProfileWrapper() {
    }

    public FacebookProfileWrapper(FacebookProfile fbUser) {
        this.fbUser = fbUser;
    }

    public FacebookProfile getFbUser() {
        return fbUser;
    }

    public void setFbUser(FacebookProfile fbUser) {
        this.fbUser = fbUser;
    }
}
