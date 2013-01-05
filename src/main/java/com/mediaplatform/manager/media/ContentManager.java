package com.mediaplatform.manager.media;

import com.mediaplatform.event.DeleteContentEvent;
import com.mediaplatform.event.UpdateContentEvent;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.RtmpPublishFormat;
import com.mediaplatform.util.ViewHelper;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.*;
import com.mediaplatform.util.RunShellCmdHelper;
import com.mediaplatform.util.TwoTuple;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 3:41 PM
 */
@Stateful
@ConversationScoped
@Named
public class ContentManager extends AbstractContentManager {
    private Content selectedContent;
    private Catalog selectedCatalog;
    private List<Content> contentList;
    @Inject
    private Event<UpdateContentEvent> updateEvent;
    @Inject
    private Event<DeleteContentEvent> deleteEvent;
    @Inject
    private CatalogManager catalogManager;

    private FileUploadBean fileUploadBean = new FileUploadBean();
    @Inject
    private FileStorageManager fileStorageManager;
    @Inject
    private ViewHelper viewHelper;

    private static final int HOME_PAGE_LIST_MAX_SIZE = 10;

    public Content getSelectedContent() {
        if (selectedContent == null) {
            selectedContent = new Content();
        }
        return selectedContent;
    }

    public String getMediaFileName() {
        if (selectedContent != null && selectedContent.getMediaFile() != null)
            return selectedContent.getMediaFile().getName();
        return "empty";
    }

    public String getMediaFileFullName() {
        if (selectedContent != null && selectedContent.getMediaFile() != null)
            return selectedContent.getMediaFile().getFullName();
        return "empty";
    }

    public void setSelectedContent(Content selectedContent) {
        this.selectedContent = selectedContent;
    }

    public Catalog getSelectedCatalog() {
        return selectedCatalog;
    }

    public void setSelectedCatalog(Catalog selectedCatalog) {
        this.selectedCatalog = selectedCatalog;
    }

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public String viewByCatalogId(Long catalogId) {
        ConversationUtils.safeBegin(conversation);
        TwoTuple<Catalog, List<Content>> result = catalogManager.getCatalogContentList(catalogId);
        selectedCatalog = result.first;
        contentList = result.second;
        return "/view-content-list";
    }

    public void add(){
        ConversationUtils.safeBegin(conversation);
        selectedContent = new Content();
    }

    public void viewVideoOnDemand(Long id) {
        view(id);
    }

    @Admin
    public void editContent(Long id) {
        view(id);
    }

    private void view(Long id) {
        ConversationUtils.safeBegin(conversation);
        selectedContent = getById(id);
        selectedCatalog = catalogManager.getById(selectedContent.getCatalog().getId());
    }

    @Admin
    public void saveOrUpdate() {
        FileEntry mediaFile = null;
        if (fileUploadBean.getSize() > 0) {
            mediaFile = fileStorageManager.saveFile(
                    new ParentRef(selectedContent.getId(), selectedContent.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.MEDIA_CONTENT);
        }
        selectedContent.setCatalog(selectedCatalog);
        super.saveOrUpdate(selectedContent, mediaFile);
        messages.info("Updated successfull!");
        updateEvent.fire(new UpdateContentEvent(selectedContent.getId()));
    }

    @Admin
    public void delete() {
        super.delete(selectedContent);
        messages.info("Deleted successfull!");
        Set<Long> expandedCatalogIds = new HashSet<Long>();
        Catalog parent = selectedCatalog;
        while (parent != null) {
            expandedCatalogIds.add(parent.getId());
            parent = parent.getParent();
        }
        deleteEvent.fire(new DeleteContentEvent(selectedContent.getId(), expandedCatalogIds));
        if (contentList != null) {
            contentList.remove(selectedContent);
        }
        selectedContent = null;
        ConversationUtils.safeEnd(conversation);
    }

    @Admin
    public void publish(boolean highQuality) {
        String absFilePath = fileStorageManager.getMediaFileUrl(selectedContent.getMediaFile(), true);
        LiveStream liveStream = new LiveStream(absFilePath, selectedContent.getMediaFile().getName(), selectedContent.getDescription(), true);
        appEm.persist(liveStream);
        RunShellCmdHelper.publish(absFilePath, selectedContent.getMediaFile().getName(), highQuality ? RtmpPublishFormat.FLV_HIGH : RtmpPublishFormat.FLV_LOW);
        messages.info("Published successfull!");
    }

    @Admin
    public void publish() {
        publish(true);
    }

    public List<Content> getLatestContentList() {
        return findLatestList(HOME_PAGE_LIST_MAX_SIZE);
    }

    public List<Content> getPopularContentList() {
        return findPopularList(HOME_PAGE_LIST_MAX_SIZE);
    }

    public String getVideoUrl() {
        if (selectedContent == null || selectedContent.getMediaFile() == null) return "";
        return viewHelper.getVideoUrl(selectedContent.getMediaFile());
    }

    @Override
    protected String getCurrentContentName() {
        return getMediaFileFullName();
    }

    public String getHeader(){
        boolean edit = selectedContent != null && selectedContent.getId() != null;
        if(edit){
            return "Edit " + selectedContent.getTitle();
        }
        return "Add new content";
    }

    public FileUploadBean getFileUploadBean() {
        return fileUploadBean;
    }
}

