package org.jboss.seam.examples.booking.booking;

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
