package com.mediaplatform.manager.media;

import com.mediaplatform.data.stat.RtmpDTO;
import com.mediaplatform.data.stat.StreamDTO;
import com.mediaplatform.manager.MediaServerApiManager;
import com.mediaplatform.model.LiveStream;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.RtmpPublishFormat;
import com.mediaplatform.util.RunShellCmdHelper;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * User: timur
 * Date: 12/28/12
 * Time: 12:20 AM
 */
@Stateful
@ConversationScoped
@Named
public class LiveStreamManager extends AbstractContentManager {
    private List<LiveStream> liveStreams;

    private LiveStream currentStream;

    public List<LiveStream> getLiveStreams(){
        if(liveStreams == null){
            liveStreams = appEm.createQuery("select s from LiveStream s").getResultList();
        }
        return liveStreams;
    }

    public void setLiveStreams(List<LiveStream> liveStreams) {
        this.liveStreams = liveStreams;
    }

    public LiveStream getCurrentStream() {
        if(currentStream == null){
            currentStream = new LiveStream();
        }
        return currentStream;
    }

    public void setCurrentStream(LiveStream currentStream) {
        this.currentStream = currentStream;
    }

    public void show(){
        ConversationUtils.safeBegin(conversation);
        liveStreams = null;
    }

    @Admin
    public void edit(LiveStream stream){
        ConversationUtils.safeBegin(conversation);
        this.currentStream = stream;
    }

    @Admin
    public void saveOrCreate(){
        if(currentStream.getId() == null){
            appEm.persist(currentStream);
        }else{
            appEm.merge(currentStream);
        }
        currentStream = null;
        liveStreams = null;
    }

    @Admin
    public void remove(LiveStream stream){
        appEm.remove(appEm.find(LiveStream.class, stream.getId()));
        this.currentStream = null;
        this.liveStreams = null;
    }

    @Admin
    public void publish(LiveStream stream){
        appEm.merge(stream);
        this.currentStream = stream;
        liveStreams = null;
        //todo make unique
        String streamName = stream.getTitle() + "?" + getCallbackQueryParams();
        if(stream.isPublished()){
            RunShellCmdHelper.dropStream(stream.getTitle(), configBean.getStreamDropUrl());
            RunShellCmdHelper.publish(stream.getSource(), streamName, RtmpPublishFormat.MP4_LIVE_STREAM_HIGH);
            messages.info("Published " + stream.getSource());
        }else{
            RunShellCmdHelper.dropStream(stream.getTitle(), configBean.getStreamDropUrl());
            messages.info("Dropped " + stream.getSource());
        }
    }

    @Admin
    public void add(){
        ConversationUtils.safeBegin(conversation);
        currentStream = new LiveStream();
    }

    public void viewLiveVideo(LiveStream stream) {
        ConversationUtils.safeBegin(conversation);
        this.currentStream = stream;
    }

    @Override
    protected String getCurrentContentName() {
        return getCurrentStream().getTitle();
    }

    public boolean isEdit(){
        return currentStream != null && currentStream.getId() != null;
    }

}
