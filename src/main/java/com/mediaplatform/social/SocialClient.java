/*
 * Copyright 2013 Agorava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mediaplatform.social;

import com.mediaplatform.account.UserSession;
import com.mediaplatform.manager.UserManager;
import com.mediaplatform.model.User;
import com.mediaplatform.model.UserInfo;
import org.agorava.AgoravaContext;
import org.agorava.api.oauth.OAuthSession;
import org.agorava.api.service.OAuthLifeCycleService;
import org.agorava.facebook.Facebook;
import org.agorava.facebook.UserService;
import org.agorava.facebook.model.FacebookProfile;
import org.agorava.spi.UserProfile;
import org.agorava.twitter.Twitter;
import org.agorava.twitter.TwitterUserService;
import org.agorava.twitter.model.TwitterProfile;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Identity;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;


@Named
@RequestScoped
public class SocialClient implements Serializable {

    private static final long serialVersionUID = 3723552335163650582L;
    @Inject
    protected EntityManager appEm;
    @Inject
    protected Messages messages;
    @Inject
    protected Instance<UserManager> userManagerInstance;
    @Inject
    protected Identity identity;
    @Inject
    OAuthLifeCycleService lifeCycleService;
    @Inject
    @Twitter
    private Instance<TwitterUserService> twUserService;
    @Inject
    @Facebook
    private Instance<UserService> userService;

    @Inject
    private UserSession userSession;

    private String Status;
    private String selectedService;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public OAuthSession getCurrentSession() {
        return lifeCycleService.getCurrentSession();
    }

    public void setCurrentSession(OAuthSession currentSession) {
        lifeCycleService.setCurrentSession(currentSession);
    }

    public List<OAuthSession> getSessions() {
        return lifeCycleService.getAllActiveSessions();
    }

    public String getCurrentSessionName() {
        return getCurrentSession().toString();
    }

    public void redirectToAuthorizationURL(String url) throws IOException {
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        externalContext.redirect(url);
    }

    public void serviceInit() throws IOException {
        redirectToAuthorizationURL(lifeCycleService.startDanceFor(selectedService));
    }

    public void resetConnection() {
        lifeCycleService.killCurrentSession();
        userSession.clearSocial();
    }

    /**
     * @return the selectedService
     */
    public String getSelectedService() {
        return selectedService;
    }

    /**
     * @param selectedService the selectedService to set
     */
    public void setSelectedService(String selectedService) {
        this.selectedService = selectedService;
    }

    public List<String> getListOfServices() {
        return AgoravaContext.getListOfServices();
    }

    public void afterLogin() {
        if (getCurrentSession() != null && getCurrentSession().isConnected()) {
            if (getCurrentSession().getServiceName().equals("Twitter")) {
                userSession.setTwUser(twUserService.get().getUserProfile());
            } else if (getCurrentSession().getServiceName().equals("Facebook")) {
                userSession.setFbUser(userService.get().getUserProfile());
            }

            UserProfile userProfile = getCurrentSession().getUserProfile();
            User user = userManagerInstance.get().findByUsername(userProfile.getId());
            if (user == null) {
                user = new User();
                user.setUsername(userProfile.getId());
                user.setName(userProfile.getFullName());

                UserInfo userInfo = null;
                if (getCurrentSession().getServiceName().equals("Twitter")) {
                    userInfo = new UserInfo(userSession.getTwUser());
                } else if (getCurrentSession().getServiceName().equals("Facebook")) {
                    userInfo = new UserInfo(userSession.getFbUser());
                }
                appEm.persist(userInfo);
                appEm.persist(user);
                userInfo.setUser(user);
                user.setUserInfo(userInfo);
                appEm.merge(user);
                appEm.merge(userInfo);
            }
            if (!Identity.RESPONSE_LOGIN_SUCCESS.equals(identity.login())) {
                messages.error("Авторизация через социальный аккаунт прошла неудачно!");
            }
        }
    }

}
