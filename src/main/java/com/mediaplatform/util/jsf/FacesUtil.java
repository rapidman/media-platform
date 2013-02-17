package com.mediaplatform.util.jsf;

import org.apache.commons.lang.StringUtils;

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

    public static void redirectToEndConversation() {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Превышено время ожидания, операция отменена.", null));
        redirect("conversation_ended");
    }
    
    public static void redirect(String page) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            if(facesContext == null) return;
            ExternalContext externalContext = facesContext.getExternalContext();
            FacesUtil.saveMessages(facesContext);
            String redirect = externalContext.getRequestContextPath() + "/" + page;
            externalContext.redirect(redirect);
        } catch (Exception e) {
            e.printStackTrace();
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

    public static boolean validateLong(javax.faces.context.FacesContext facesContext,
                                       javax.faces.component.UIComponent uiComponent,
                                       java.lang.Object obj,
                                       String requiredMsg){
        boolean ok = validateRequired(facesContext, obj, requiredMsg);
        if(ok){
            try {
                Long.parseLong(String.valueOf(obj));
            } catch (NumberFormatException e) {
                ok = false;
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Неверное число '" + obj + "'", null));
            }
        }
        return ok;
    }

    public static boolean validateRequired(FacesContext facesContext, Object obj, String requiredMsg) {
        if(obj == null || (String.class.isInstance(obj) && StringUtils.isBlank((String) obj))){
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, requiredMsg, null));
            return false;
        }
        return true;
    }

    public static void addError(String clientId, String msg) {
        FacesContext.getCurrentInstance().addMessage(clientId, new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg));
    }
}
