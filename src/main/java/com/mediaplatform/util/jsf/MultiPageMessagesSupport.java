package com.mediaplatform.util.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.List;
import java.util.Map;
import static com.mediaplatform.util.jsf.FacesUtil.sessionToken;

/**
 * User: timur
 * Date: 12/25/12
 * Time: 9:32 PM
 */
public class MultiPageMessagesSupport implements PhaseListener
{

    private static final long serialVersionUID = 1250469273857785274L;


    public PhaseId getPhaseId()
    {
        return PhaseId.ANY_PHASE;
    }

    /*
     * Check to see if we are "naturally" in the RENDER_RESPONSE phase. If we
     * have arrived here and the response is already complete, then the page is
     * not going to show up: don't display messages yet.
     */
    // TODO: Blog this (MultiPageMessagesSupport)
    public void beforePhase(final PhaseEvent event)
    {
        FacesContext facesContext = event.getFacesContext();

        if (PhaseId.RENDER_RESPONSE.equals(event.getPhaseId()))
        {
            if (!facesContext.getResponseComplete())
            {
                this.restoreMessages(facesContext);
            }
        }
    }

    /*
     * Save messages into the session after every phase.
     */
    public void afterPhase(final PhaseEvent event){}


    @SuppressWarnings("unchecked")
    private int restoreMessages(final FacesContext facesContext)
    {
        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
        List<FacesMessage> messages = (List<FacesMessage>) sessionMap.remove(sessionToken);

        if (messages == null)
        {
            return 0;
        }

        int restoredCount = messages.size();
        for (Object element : messages)
        {
            facesContext.addMessage(null, (FacesMessage) element);
        }
        return restoredCount;
    }
}