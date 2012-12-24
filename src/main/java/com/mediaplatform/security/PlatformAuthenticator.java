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

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.mediaplatform.account.Authenticated;
import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.Authenticator;
import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;
import org.jboss.seam.social.UserProfile;
import org.jboss.seam.social.twitter.model.TwitterProfile;
import org.jboss.solder.logging.Logger;
import org.picketlink.idm.impl.api.PasswordCredential;
import org.picketlink.idm.impl.api.model.SimpleUser;

@Stateful
@LocalBean
@Named("platformAuthenticator")
public class PlatformAuthenticator extends BaseAuthenticator implements Authenticator {

    @Inject
    private Logger log;

    @PersistenceContext
    private EntityManager em;

    @Inject
    private Credentials credentials;

    @Inject
    private Messages messages;

    @Inject
    @Authenticated
    private Event<User> loginEventSrc;

    @Inject
    @Named("twUser")
    private TwitterProfile twUser;

    @Inject
    @Named("fbUser")
    private UserProfile fbUser;

    @Inject
    private Identity identity;


    public void authenticate() {
        User user;
        try {
            if(twUser != null){
                log.info("Logging in " + twUser.getScreenName());
                user = em.find(User.class, twUser.getScreenName());
                if(user != null){
                    authenticate(user);
                    return;
                }
            }else if(fbUser != null){
                log.info("Logging in " + fbUser.getId());
                user = em.find(User.class, fbUser.getId());
                if(user != null){
                    authenticate(user);
                    return;
                }
            }else{
                log.info("Logging in " + credentials.getUsername());
                user = em.find(User.class, credentials.getUsername());
                if (user != null && credentials.getCredential() instanceof PasswordCredential &&
                        user.getPassword().equals(((PasswordCredential) credentials.getCredential()).getValue())) {
                    if(user.getUserInfo() != null && user.getUserInfo().getSocialNetType() != null){
                        throw new IllegalStateException("User with social type " + user.getUserInfo().getSocialNetType() + " can't login from web login form");
                    }
                    authenticate(user);
                    if("admin".equals(user.getUsername())){
                        identity.addRole("admin", "USERS", "GROUP");
                    }
                    return;
                }
            }
        } catch (Exception e) {
            log.error(e);
        }

        messages.error(new DefaultBundleKey("identity_loginFailed")).defaults("Invalid username or password");
        setStatus(AuthenticationStatus.FAILURE);

    }

    public void authenticate(User user) {
        loginEventSrc.fire(user);
        messages.info(new DefaultBundleKey("identity_loggedIn"), user.getName()).defaults("You're signed in as {0}")
                .params(user.getName());
        setStatus(AuthenticationStatus.SUCCESS);
        setUser(new SimpleUser(user.getUsername()));
    }
}
