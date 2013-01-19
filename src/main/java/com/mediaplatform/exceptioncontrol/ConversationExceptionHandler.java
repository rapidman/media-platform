package com.mediaplatform.exceptioncontrol;

import java.io.IOException;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import com.mediaplatform.util.jsf.FacesUtil;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;

@HandlesExceptions
public class ConversationExceptionHandler {
    @Inject
    private FacesContext facesContext;

    /**
     * Handles the exception thrown at the end of a conversation redirecting
     * the flow to a pretty page instead of printing a stacktrace on the screen.
     *
     * @param event
     * @param log
     */
    public void conversationEndedExceptionHandler(@Handles CaughtException<NonexistentConversationException> event, Logger log) {
        log.info("Conversation ended: " + event.getException().getMessage());
        FacesUtil.redirectToEndConversation();
    }
}
