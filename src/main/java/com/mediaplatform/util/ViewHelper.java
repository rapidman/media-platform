package com.mediaplatform.util;

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

    public String getImgUrl(FileEntry fileEntry, ImageFormat format){
        return fileStorageManager.getImageFileUrl(fileEntry, format);
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
}
