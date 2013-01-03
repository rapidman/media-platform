package com.mediaplatform.manager;

import com.mediaplatform.model.Event;
import com.mediaplatform.util.ConversationUtils;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * User: timur
 * Date: 1/3/13
 * Time: 12:50 AM
 */
@Stateful
@ConversationScoped
@Named
public class EventManager extends AbstractManager{
    @Inject
    protected Conversation conversation;

    private List<Event> events;

    public List<Event> getEvents() {
        if(events == null){
            events = appEm.createQuery("select e from Event e order by e.createDateTime desc").getResultList();
        }
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

    public void show(){
        ConversationUtils.safeBegin(conversation);
        events = null;
    }
}
