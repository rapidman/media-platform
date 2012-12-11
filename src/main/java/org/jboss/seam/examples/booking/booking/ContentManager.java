package org.jboss.seam.examples.booking.booking;

import org.jboss.seam.examples.booking.event.DeleteContentEvent;
import org.jboss.seam.examples.booking.event.UpdateContentEvent;
import org.jboss.seam.examples.booking.jsf.fileupload.FileUploadBean;
import org.jboss.seam.examples.booking.model.*;
import org.jboss.seam.examples.booking.util.RunShellCmdHelper;
import org.jboss.seam.examples.booking.util.TwoTuple;

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
    @Inject
    private FileUploadBean fileUploadBean;
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

    public void viewLiveVideo(Long id) {
        view(id);
    }

    public void viewVideoOnDemand(Long id) {
        view(id);
    }

    public void editContent(Long id) {
        view(id);
    }

    private void view(Long id) {
        ConversationUtils.safeBegin(conversation);
        selectedContent = getById(id);
        selectedCatalog = catalogManager.getById(selectedContent.getCatalog().getId());
    }

    public void update() {
        FileEntry mediaFile = null;
        if (fileUploadBean.getSize() > 0) {
            mediaFile = fileStorageManager.saveFile(
                    new ParentRef(selectedContent.getId(), selectedContent.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.MEDIA_CONTENT);
        }
        super.update(selectedContent, mediaFile);
        messages.info("Updated successfull!");
        updateEvent.fire(new UpdateContentEvent(selectedContent.getId()));
    }

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
        contentList.remove(selectedContent);
        selectedContent = null;
    }

    public void publish(boolean highQuality){
        String absFilePath = fileStorageManager.getMediaFileUrl(selectedContent.getMediaFile(), true);
        RunShellCmdHelper.publish(absFilePath, selectedContent.getMediaFile().getName(), highQuality ? RtmpPublishFormat.FLV_HIGH : RtmpPublishFormat.FLV_LOW);
        selectedContent.setPublished(true);
        super.update(selectedContent);
        messages.info("Published successfull!");
    }

    public void publish(){
       publish(true);
    }

    public void dropStream(String streamName){
        RunShellCmdHelper.dropStream(streamName, configBean.getStreamDropUrl());
        selectedContent.setPublished(false);
        super.update(selectedContent);
        messages.info(streamName + "is dropped");
    }

    public List<Content> getLatestContentList() {
        return findLatestList(HOME_PAGE_LIST_MAX_SIZE);
    }

    public List<Content> getPopularContentList() {
        return findPopularList(HOME_PAGE_LIST_MAX_SIZE);
    }

    public String getVideoUrl(){
        if(selectedContent == null || selectedContent.getMediaFile() == null) return "";
        return viewHelper.getVideoUrl(selectedContent.getMediaFile());
    }
}

