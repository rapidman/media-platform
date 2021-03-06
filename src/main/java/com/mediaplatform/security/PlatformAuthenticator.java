/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mediaplatform.security;

import com.mediaplatform.account.UserSession;
import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.manager.UserManager;
import com.mediaplatform.model.BannedUser;
import com.mediaplatform.model.User;
import com.mediaplatform.social.SocialClient;
import com.mediaplatform.util.jsf.FacesUtil;
import org.agorava.facebook.model.FacebookProfile;
import org.agorava.twitter.model.TwitterProfile;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Date;

@Stateful
@LocalBean
@Named("platformAuthenticator")
public class PlatformAuthenticator extends BaseAuthenticator implements Authenticator {

    @Inject
    private Logger log;

    @Inject
    private Credentials credentials;

    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    @Inject
    private Identity identity;

    @Inject
    private Instance<UserManager> userManagerInstance;

    @Inject
    private FacesContext facesContext;

    @Inject
    private SocialClient socialClient;

    @Inject
    private UserSession userSession;

    public void authenticate() {
        User user;
        try {
            boolean ok = false;
            TwitterProfile twUser = userSession.getTwUser();
            FacebookProfile fbUser = userSession.getFbUser();
            if(twUser != null){
                log.info("Logging in " + twUser.getScreenName());
                user = userManagerInstance.get().findByUsername(twUser.getId());
                if(user != null){
                    authenticate(user);
                    ok = true;
                }
            }else if(fbUser != null){
                log.info("Logging in " + fbUser.getId());
                user = userManagerInstance.get().findByUsername(fbUser.getId());
                if(user != null){
                    authenticate(user);
                    ok = true;
                }
            }else{
                log.info("Logging in " + credentials.getUsername());
                user = userManagerInstance.get().findByUsername(credentials.getUsername());
                if (user != null && credentials.getCredential() instanceof PasswordCredential &&
                        user.getPassword().equals(((PasswordCredential) credentials.getCredential()).getValue())) {
                    if(user.getUserInfo() != null && user.getUserInfo().getSocialNetType() != null){
                        throw new IllegalStateException("User with social type " + user.getUserInfo().getSocialNetType() + " can't login from web login form");
                    }
                    authenticate(user);
                    if("admin".equals(user.getUsername())){
                        identity.addRole("admin", "USERS", "GROUP");
                    }
                    ok = true;
                }
            }
            if(ok){
                return;
            }
        } catch (Exception e) {
            log.error(e);
        }
        messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
        setStatus(AuthenticationStatus.FAILURE);
        reset();
    }

    public void authenticate(User user) {
        BannedUser banedUser = user.getBannedUser();
        if(banedUser != null && banedUser.getBannedTo() != null && banedUser.getBannedTo().after(new Date())){
            FacesUtil.redirectToDeniedPage();
            FacesUtil.addError(null, banedUser.getBanMessage());
//            messages.info(new DefaultBundleKey("identity_user_banned"), banedUser.getBanMessage());
            reset();
            return;
        }
        loginEventSrc.fire(user);
        messages.info(new DefaultBundleKey("identity_loggedIn"), user.getName()).defaults("You're signed in as {0}")
                .params(user.getName());
        setStatus(AuthenticationStatus.SUCCESS);
        setUser(new SimpleUser(user.getUsername()));
    }

    private void reset() {
        socialClient.resetConnection();
    }

    public void checkAuthenticated(){
        if(!identity.isLoggedIn()){
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login required", null));
            FacesUtil.redirectToLoginPage();
        }
    }

    public boolean checkIsAdmin(){
        if(!Restrictions.checkAdmin(identity)){
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient rights", null));
            FacesUtil.redirectToHomePage();
            return false;
        }
        return true;
    }
}
