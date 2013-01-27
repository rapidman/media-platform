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
package com.mediaplatform.exceptioncontrol;

import com.mediaplatform.util.jsf.FacesUtil;
import org.jboss.seam.international.status.Messages;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.io.IOException;

@HandlesExceptions
public class GeneralExceptionHandler {
    @Inject
    private Messages messages;

    @Inject
    private FacesContext facesContext;

    @Inject
    private Logger logger;

    @Inject
    private Identity identity;

    public void printExceptionMessage(@Handles CaughtException<Throwable> event, Logger log) {
            log.info("Exception logged by seam-catch catcher: " + event.getException().getMessage());
        if (event.getException().getMessage() != null &&
                (event.getException().getMessage().indexOf("WELD-001303 No active contexts for scope type javax.enterprise.context.ConversationScoped") != -1 ||
                event.getException().getMessage().indexOf("WELD-000321 No conversation found to restore for id") != -1)) {
            event.handled();
            return;
        }
        event.handled();
        logger.error(event.getException());
    }

    public void handleAuthorizationException(@Handles CaughtException<AuthorizationException> evt) {
        evt.handled();
        if(!identity.isLoggedIn()){
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "У Вас недостаточно прав для выполнения данной операции. Требуется авторизация.", null));
            FacesUtil.redirectToLoginPage();
            return;
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "У Вас недостаточно прав для выполнения данной операции", null));
        FacesUtil.redirectToDeniedPage();
    }

    public void onNonexistentConversation(
            @Handles CaughtException<NonexistentConversationException> evt) {
        logger.error("NonexistentConversationException!\n" + evt.getException().getMessage(), evt.getException());
        evt.handled();
        FacesUtil.redirectToEndConversation();
    }
}
