package com.mediaplatform.manager;

import com.mediaplatform.manager.media.AbstractContentManager;
import com.mediaplatform.manager.media.ContentManager;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.ModerationStatus;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: timur
 * Date: 1/24/13
 * Time: 1:09 AM
 */
@Stateful
@ConversationScoped
@Named
public class AdminContentManager extends AbstractContentManager {
    private List<Content> contentList;
    private SelectItem[] moderationStatuses;
    private String[] selectedModerationStatuses;

    @Admin
    public SelectItem[] getModerationStatuses() {
        if(moderationStatuses == null){
            moderationStatuses = new SelectItem[ModerationStatus.values().length];
            int ind = 0;
            for(ModerationStatus status:ModerationStatus.values()){
                moderationStatuses[ind] = new SelectItem(status.name(), status.name());
                ind++;
            }
        }
        return moderationStatuses;
    }

    public String[] getSelectedModerationStatuses() {
        if(selectedModerationStatuses == null){
            selectedModerationStatuses = new String[ModerationStatus.values().length];
            int ind=0;
            for(ModerationStatus status:ModerationStatus.values()){
                selectedModerationStatuses[ind] = status.name();
                ind++;
            }
        }
        return selectedModerationStatuses;
    }

    public void setSelectedModerationStatuses(String[] selectedModerationStatuses) {
        this.selectedModerationStatuses = selectedModerationStatuses;
    }

    public List<ModerationStatus> getSelectedModerationStatusesAsList() {
        List<ModerationStatus> result = new ArrayList<ModerationStatus>();
        for(String status:getSelectedModerationStatuses()){
            result.add(ModerationStatus.valueOf(status));
        }
        return result;
    }

    @Admin
    public List<Content> getContentList() {
        if(contentList == null){
            contentList = appEm.createQuery("select c from Content c where c.moderationStatus in (:moderationStatuses)").setParameter("moderationStatuses", getSelectedModerationStatusesAsList()).getResultList();
        }
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public void show(){
        ConversationUtils.safeBegin(conversation);
        contentList = null;
    }

    @Override
    protected String getCurrentContentName() {
        throw new UnsupportedOperationException();
    }
}
