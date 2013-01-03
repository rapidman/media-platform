package com.mediaplatform.manager;

import com.mediaplatform.event.StreamChangedEvent;
import com.mediaplatform.model.LiveStream;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import java.util.List;

/**
 * User: timur
 * Date: 1/3/13
 * Time: 6:54 PM
 */
@Stateful
@ConversationScoped
@Named
public class MainPageManager extends AbstractManager{
    private List<LiveStream> publishedLiveStreams;

    public void show(){
        publishedLiveStreams = null;
    }

    public List<LiveStream> getPublishedLiveStreams(){
        if(publishedLiveStreams == null){
            publishedLiveStreams = appEm.createQuery("select s from LiveStream s where s.published = true").getResultList();
        }
        return publishedLiveStreams;
    }

    public void setPublishedLiveStreams(List<LiveStream> publishedLiveStreams) {
        this.publishedLiveStreams = publishedLiveStreams;
    }

    public void observeStreamChanged(@Observes StreamChangedEvent streamChangedEvent){
        show();
    }
}
