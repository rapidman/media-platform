package com.mediaplatform.jsf;

import com.mediaplatform.security.Admin;
import com.mediaplatform.security.User;
import org.jboss.seam.faces.rewrite.FacesRedirect;
import org.jboss.seam.faces.rewrite.UrlMapping;
import org.jboss.seam.faces.security.AccessDeniedView;
import org.jboss.seam.faces.security.LoginView;
import org.jboss.seam.faces.view.config.ViewConfig;
import org.jboss.seam.faces.view.config.ViewPattern;

/**
 * User: timur
 * Date: 2/17/13
 * Time: 8:49 PM
 */
@ViewConfig
public interface Pages {


    static enum SubPages{

        @ViewPattern("/admin/viewContentList.xhtml")
        @Admin
        ADMIN,

        @UrlMapping(pattern = "/user/#{id}")
        @ViewPattern("/account/userProfile.xhtml")
        @User
        USER_PROFILE,

        @FacesRedirect
        @ViewPattern("/*")
        @AccessDeniedView("/denied.xhtml")
        @LoginView("/login.xhtml")
        ALL;
    }

}
