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
import org.jboss.solder.logging.Logger;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@HandlesExceptions
public class GeneralExceptionHandler {
    @Inject
    Messages messages;

    public void printExceptionMessage(@Handles CaughtException<Throwable> event, Logger log) {
        log.info("Exception logged by seam-catch catcher: " + event.getException().getMessage());
        event.rethrow();
    }

    public void handleAuthorizationException(@Handles CaughtException<AuthorizationException> evt) {
        evt.handled();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "You do not have the necessary permissions to perform that operation", null));
        FacesUtil.redirectToDeniedPage();
    }
}