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
package com.mediaplatform.account;

import javax.ejb.Stateful;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.manager.UserManager;
import com.mediaplatform.model.User;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.international.status.builder.BundleKey;

/**
 * The view controller for registering a new user
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Stateful
@Model
public class Registrar {
    @PersistenceContext
    private EntityManager em;

    @Inject
    private Messages messages;

    @Inject
    private FacesContext facesContext;

    private UIInput usernameInput;

    private final User newUser = new User();

    @Inject
    private Instance<UserManager> userManagerInstance;

    @NotNull
    @Size(min = 5, max = 15, message = "Ошибка: Значение должно быть в интервале от 5 до 15")
    private String confirmPassword;

    private boolean registered;

    private boolean registrationInvalid;

    public void register() {
        if (verifyUsernameIsAvailable()) {
            registered = true;
            em.persist(newUser);

            messages.info(new DefaultBundleKey("registration_registered"))
                    .defaults("Вы зарегистрировались как пользователь {0}! Теперь Вы можете зайти на сайт.")
                    .params(newUser.getUsername());
        } else {
            registrationInvalid = true;
        }
    }

    public boolean isRegistrationInvalid() {
        return registrationInvalid;
    }

    public void notifyIfRegistrationIsInvalid() {
        if (facesContext.isValidationFailed() || registrationInvalid) {
            messages.warn(new DefaultBundleKey("registration_invalid")).defaults(
                    "Регистрация не удалась. Пожалуйста исправьте ошибки и попробуйте еще раз.");
        }
    }

    @Produces
    @Named
    public User getNewUser() {
        return newUser;
    }

    public boolean isRegistered() {
        return registered;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String password) {
        confirmPassword = password;
    }

    public UIInput getUsernameInput() {
        return usernameInput;
    }

    public void setUsernameInput(final UIInput usernameInput) {
        this.usernameInput = usernameInput;
    }

    private boolean verifyUsernameIsAvailable() {
        User existing = userManagerInstance.get().findByUsername(newUser.getUsername());
        if (existing != null) {
            messages.warn(new BundleKey("messages", "account_usernameTaken"))
                    .defaults("Ник '{0}' уже испльзуется. Пожалуйста выберите другой ник.")
                    .targets(usernameInput.getClientId()).params(newUser.getUsername());
            return false;
        }

        return true;
    }

}
