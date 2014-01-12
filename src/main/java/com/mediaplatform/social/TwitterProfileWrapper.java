package com.mediaplatform.social;

import org.agorava.twitter.model.TwitterProfile;

import java.io.Serializable;

/**
 * User: timur
 * Date: 1/12/14
 * Time: 11:57 PM
 */
public class TwitterProfileWrapper implements Serializable{
    private TwitterProfile twUser;

    public TwitterProfileWrapper() {
    }

    public TwitterProfileWrapper(TwitterProfile twUser) {
        this.twUser = twUser;
    }

    public TwitterProfile getTwUser() {
        return twUser;
    }

    public void setTwUser(TwitterProfile twUser) {
        this.twUser = twUser;
    }
}
