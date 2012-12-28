package com.mediaplatform.util.jsf;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * User: timur
 * Date: 12/25/12
 * Time: 9:00 PM
 */
public class FacesUtil {
    public static final String sessionToken = "MULTI_PAGE_MESSAGES_SUPPORT";
    private FacesUtil(){}


    public static void redirectToLoginPage() {
        redirect("login");
    }

    public static void redirectToDeniedPage() {
        redirect("denied");
    }

    public static void redirectToHomePage() {
        redirect("");
    }

    public static void redirect(String page) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            FacesUtil.saveMessages(facesContext);
            String redirect = externalContext.getRequestContextPath() + "/" + page;
            externalContext.redirect(redirect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static int saveMessages(final FacesContext facesContext)
    {
        List<FacesMessage> messages = new ArrayList<FacesMessage>();
        for (Iterator<FacesMessage> iter = facesContext.getMessages(null); iter.hasNext();)
        {
            messages.add(iter.next());
            iter.remove();
        }

        if (messages.size() == 0)
        {
            return 0;
        }

        Map<String, Object> sessionMap = facesContext.getExternalContext().getSessionMap();
        List<FacesMessage> existingMessages = (List<FacesMessage>) sessionMap.get(sessionToken);
        if (existingMessages != null)
        {
            existingMessages.addAll(messages);
        }
        else
        {
            sessionMap.put(sessionToken, messages);
        }
        return messages.size();
    }


}
