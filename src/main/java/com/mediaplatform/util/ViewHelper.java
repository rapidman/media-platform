package com.mediaplatform.util;

import com.mediaplatform.account.UserSession;
import com.mediaplatform.manager.ConfigBean;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.*;
import com.mediaplatform.social.SocialClient;
import org.agorava.facebook.model.FacebookProfile;
import org.agorava.twitter.model.TwitterProfile;
import org.apache.commons.lang.StringUtils;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/2/12
 * Time: 11:20 AM
 */
@RequestScoped
@Named
public class ViewHelper implements Serializable {
    @Inject
    private FileStorageManager fileStorageManager;

    @Inject
    private ConfigBean configBean;

    @Inject
    private UserSession userSession;

    private static final int MAX_TITLE_LENGTH = 40;
    private static final int MAX_DESC_LENGTH = 80;

    public String getImgUrl(FileEntry fileEntry, ImageFormat format){
        return configBean.getResourceServletMapping() + fileStorageManager.getImageFileUrl(fileEntry, format);
    }

    public String getImgUrlByStr(FileEntry fileEntry, String format){
        return configBean.getResourceServletMapping() + fileStorageManager.getImageFileUrl(fileEntry, ImageFormat.parse(format));
    }

    public String getDefaultAvatar(String format){
        return configBean.getResourceServletMapping() + fileStorageManager.getDefaultAvatarUrl(format);
    }

    public String getAvatar(User user, String dimen){
        if(!StringUtils.isBlank(userSession.getUserProfileImageUrl())){
            return userSession.getUserProfileImageUrl();
        }
        if(user.getAvatar() != null){
            return getImgUrlByStr(user.getAvatar(), dimen);
        }
        return getDefaultAvatar(dimen);
    }

    public String getImgUrlExt(FileEntry fileEntry, String format){
        return configBean.getResourceServletMapping() + getImgUrl(fileEntry, ImageFormat.parse(format));
    }

    public String getVideoUrl(FileEntry fileEntry){
        return configBean.getResourceServletMapping() + fileStorageManager.getMediaFileUrl(fileEntry);
    }

    public String viewTitle(String title){
        return StringUtils.abbreviate(title, MAX_TITLE_LENGTH);
    }

    public String viewDescription(String desc){
        return StringUtils.abbreviate(desc, MAX_DESC_LENGTH);
    }

    public String userProfileHeader(User user){
        return "Страница пользователя '" + user.getName() + "'";
    }

    public String userOwnerProfileHeader(User user){
        return "Ваша страница";
    }

    public String constructOutcome(String str, Long id){
        return str + id;
    }

    public boolean isContent(AbstractEntity entity){
        return Content.class.isInstance(entity);
    }

    public boolean isUser(AbstractEntity entity){
        return User.class.isInstance(entity);
    }

    public boolean isComment(AbstractEntity entity){
        return Comment.class.isInstance(entity);
    }

}
