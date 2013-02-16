package com.mediaplatform.util;

import com.mediaplatform.model.User;
import org.apache.commons.lang.StringUtils;
import com.mediaplatform.manager.ConfigBean;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.FileEntry;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/2/12
 * Time: 11:20 AM
 */
@ApplicationScoped
@Named
public class ViewHelper implements Serializable {
    @Inject
    private FileStorageManager fileStorageManager;

    @Inject
    private ConfigBean configBean;
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
}
