package com.mediaplatform.model;

import org.jboss.seam.social.UserProfile;
import org.jboss.seam.social.twitter.model.TwitterProfile;
import org.jboss.solder.core.Veto;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User: timur
 * Date: 12/17/12
 * Time: 6:41 PM
 */
@Cacheable
@Entity
@Table(name = "user_info")
@Veto
public class UserInfo extends AbstractEntity {
    private String screenName;
    private String url;
    private String profileImageUrl;
    private String profileUrl;
    private String description;
    private String location;
    private Date createdDate;
    private String language;
    private User user;
    private SocialNetType socialNetType;

    public UserInfo() {
        super(EntityType.USER_INFO);
    }

    public UserInfo(TwitterProfile twUser) {
        this();
        socialNetType = SocialNetType.TW;
        this.screenName = twUser.getScreenName();
        this.url = twUser.getUrl();
        this.profileImageUrl = twUser.getProfileImageUrl();
        this.profileUrl = twUser.getProfileUrl();
        this.description = twUser.getDescription();
        this.location = twUser.getLocation();
        this.createdDate = new Date();
        this.language = twUser.getLanguage();
    }

    public UserInfo(UserProfile fbUser) {
        this();
        socialNetType = SocialNetType.TW;
        this.screenName = fbUser.getFullName();
        this.profileImageUrl = fbUser.getProfileImageUrl();
        this.createdDate = new Date();
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @OneToOne(mappedBy = "userInfo")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Enumerated(EnumType.STRING)
    public SocialNetType getSocialNetType() {
        return socialNetType;
    }

    public void setSocialNetType(SocialNetType socialNetType) {
        this.socialNetType = socialNetType;
    }
}
