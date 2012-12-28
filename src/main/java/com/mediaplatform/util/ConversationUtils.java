package com.mediaplatform.util;

import javax.enterprise.context.Conversation;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 10:36 PM
 */
public class ConversationUtils {
    private ConversationUtils(){}

    public static void safeBegin(Conversation conversation) {
        if(!conversation.isTransient()){
            conversation.end();
        }
        conversation.begin();
    }
}