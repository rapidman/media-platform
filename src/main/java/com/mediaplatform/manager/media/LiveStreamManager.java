package com.mediaplatform.manager.media;

import com.mediaplatform.data.stat.ApplicationDTO;
import com.mediaplatform.data.stat.RtmpDTO;
import com.mediaplatform.data.stat.StreamDTO;
import com.mediaplatform.manager.MediaServerApiManager;
import com.mediaplatform.util.TwoTuple;

import javax.ejb.Stateful;
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
public class LiveStreamManager {
    private TwoTuple<ApplicationDTO, StreamDTO> currStreamInfo;

    @Inject
    private MediaServerApiManager apiManager;


    public void viewLiveVideoByName(String name) {
        RtmpDTO info = getLiveVideoInfo();
        for (ApplicationDTO app : info.getServer().getApplications()) {
            for (StreamDTO stream : app.getLive().getStreams()) {
                if (stream.getName().equals(name)) {
                    this.currStreamInfo = new TwoTuple<ApplicationDTO, StreamDTO>(app, stream);
                    break;
                }
            }
        }
    }

    public RtmpDTO getLiveVideoInfo() {
        return apiManager.getStatInfo();
    }

    public List<StreamDTO> getLiveStreams() {
        return getLiveVideoInfo().getServer().getLiveApp() == null ? null : getLiveVideoInfo().getServer().getLiveApp().getLive().getStreams();
    }

    public TwoTuple<ApplicationDTO, StreamDTO> getCurrStreamInfo() {
        return currStreamInfo;
    }
}
