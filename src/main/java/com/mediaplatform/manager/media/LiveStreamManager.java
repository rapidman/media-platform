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
//    private TwoTuple<ApplicationDTO, StreamDTO> currStreamInfo;

    @Inject
    private MediaServerApiManager apiManager;

    private String url;

    private List<LiveStream> liveStreams;

    private LiveStream currentStream;

    public List<LiveStream> getLiveStreams(){
        if(liveStreams == null){
            liveStreams = appEm.createQuery("select s from LiveStream s").getResultList();
        }
        return liveStreams;
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
        liveStreams = null;
    }

    @Admin
    public void edit(LiveStream stream){
        ConversationUtils.safeBegin(conversation);
        this.currentStream = stream;
    }

    @Admin
    public void save(){
        if(currentStream.getId() == null){
            appEm.persist(currentStream);
        }else{
            appEm.merge(currentStream);
        }
        show();
        ConversationUtils.safeEnd(conversation);
    }

    @Admin
    public void remove(LiveStream stream){
        appEm.remove(appEm.find(LiveStream.class, stream.getId()));
        ConversationUtils.safeEnd(conversation);
        show();
        this.currentStream = null;
    }

    @Admin
    public void publish(LiveStream stream){
        appEm.merge(stream);
        this.currentStream = stream;
        liveStreams = null;
        //todo make unique
        String streamName = stream.getTitle() + "?" + getCallbackQueryParams();
        if(stream.isPublished()){
            RunShellCmdHelper.publish(stream.getSource(), streamName, RtmpPublishFormat.FLV_HIGH);
            messages.info("Published " + stream.getSource());
        }else{
            RunShellCmdHelper.dropStream(streamName, configBean.getStreamDropUrl());
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

//        RtmpDTO info = getLiveVideoInfo();
//        for (ApplicationDTO app : info.getServer().getApplications()) {
//            for (StreamDTO stream : app.getLive().getStreams()) {
//                if (stream.getName().equals(name)) {
//                    this.currStreamInfo = new TwoTuple<ApplicationDTO, StreamDTO>(app, stream);
//                    break;
//                }
//            }
//        }

    }

    public RtmpDTO getLiveVideoInfo() {
        return apiManager.getStatInfo();
    }

    public List<StreamDTO> getLiveStreamsFromService() {
        return getLiveVideoInfo().getServer().getLiveApp() == null ? null : getLiveVideoInfo().getServer().getLiveApp().getLive().getStreams();
    }

//    public TwoTuple<ApplicationDTO, StreamDTO> getCurrStreamInfo() {
//        return currStreamInfo;
//    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Conversation getConversation() {
        return conversation;
    }

    @Override
    protected String getCurrentContentName() {
        return getCurrentStream().getTitle();
    }


}
