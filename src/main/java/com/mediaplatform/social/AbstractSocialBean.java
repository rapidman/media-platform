package com.mediaplatform.social;

import com.mediaplatform.model.User;
import com.mediaplatform.model.UserInfo;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/20/12
 * Time: 11:44 PM
 */
public abstract class AbstractSocialBean implements Serializable {
    @Inject
    protected Identity identity;

    @Inject
    protected Logger log;

    @Inject
    protected EntityManager appEm;

    @Inject
    protected Messages messages;


    public void login(){
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.redirect(getAuthorizationUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getAuthorizationUrl();

    public void authenticate() {
        try {
            initAccessToken();
        } catch (Exception e) {
            String message = "Can't authenticate";
            log.error(message, e);
            messages.error(message);
            redirectToLoginPage();
            return;
        }
        User user = findUser();
        if (user == null) {
            user = new User();
            //TODO убедиться что обеспечиваем уникальность
            assignProps(user);
            UserInfo userInfo = createUserInfo();
            appEm.persist(userInfo);
            appEm.persist(user);
            userInfo.setUser(user);
            user.setUserInfo(userInfo);
            appEm.merge(user);
            appEm.merge(userInfo);
        }
        if (Identity.RESPONSE_LOGIN_SUCCESS.equals(identity.login())) {
            redirectToHomePage();
        } else {
            messages.error("Login via social account is failed");
            redirectToLoginPage();
        }
    }

    protected abstract User findUser();

    protected abstract UserInfo createUserInfo();

    protected abstract void assignProps(User user);

    protected abstract void initAccessToken();

    private void redirectToLoginPage() {
        redirect("login");
    }

    private void redirectToHomePage() {
        redirect("");
    }

    private void redirect(String page) {
        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String redirect = externalContext.getRequestContextPath() + "/" + page;
            externalContext.redirect(redirect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
