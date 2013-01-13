package com.mediaplatform.manager.media;

import com.mediaplatform.event.CreateContentEvent;
import com.mediaplatform.event.DeleteContentEvent;
import com.mediaplatform.event.UpdateCatalogEvent;
import com.mediaplatform.event.UpdateContentEvent;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.jsf.fileupload.UploadedFile;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.*;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.*;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
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
public class ContentManager extends AbstractContentManager implements Serializable{
    private Content selectedContent;
    private Genre selectedGenre;
    @Inject
    private Event<UpdateContentEvent> updateEvent;
    @Inject
    private Event<CreateContentEvent> createEvent;
    @Inject
    private Event<UpdateCatalogEvent> updateCatalogEvent;
    @Inject
    private Event<DeleteContentEvent> deleteEvent;
    @Inject
    private CatalogManager catalogManager;

    private FileUploadBean fileUploadBean = new FileUploadBean();

    private FileUploadBean imgFileUploadBean = new FileUploadBean();

    @Inject
    private FileStorageManager fileStorageManager;
    @Inject
    private ViewHelper viewHelper;

    private static final int HOME_PAGE_LIST_MAX_SIZE = 10;

    @Produces
    @ConversationScoped
    @Named("currentContent")
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

    public Genre getSelectedGenre() {
        return selectedGenre;
    }

    public void setSelectedGenre(Genre selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    public List<Content> getContentList() {
        if(selectedGenre == null) return new ArrayList<Content>();
        return selectedGenre.getContentList();
    }

    public String viewByCatalogId(Long catalogId) {
        ConversationUtils.safeBegin(conversation);
        TwoTuple<Genre, List<Content>> result = catalogManager.getCatalogContentList(catalogId);
        selectedGenre = result.first;
        return "/view-content-list";
    }

    public void add(){
        ConversationUtils.safeBegin(conversation);
        selectedContent = new Content();
    }

    public Content viewVideoOnDemand(Long id) {
        ConversationUtils.safeBegin(conversation);
        view(id);
        return selectedContent;
    }

    @Admin
    public void editContent(Long id) {
        ConversationUtils.safeBegin(conversation);
        view(id);
    }

    private void view(Long id) {
        selectedContent = getById(id);
        selectedGenre = catalogManager.getById(selectedContent.getGenre().getId());
    }

    @Admin
    public void saveOrUpdate() {
        FileEntry mediaFile = null;

        boolean edit = selectedContent.getId() != null;
        super.saveOrUpdate(selectedContent, selectedGenre, null, null);
        if (fileUploadBean.getSize() > 0) {
            mediaFile = fileStorageManager.saveFile(
                    new ParentRef(selectedContent.getId(), selectedContent.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.MEDIA_CONTENT);
        }
        UploadedFile uploadedCover = null;
        if(imgFileUploadBean.getSize() > 0){
            uploadedCover = imgFileUploadBean.getFiles().get(0);
        }else{
            uploadedCover = new UploadedFile(fileStorageManager.getDefaultImage());
        }

        FileEntry cover = fileStorageManager.saveFile(
                new ParentRef(selectedContent.getId(),
                        selectedContent.getEntityType()),
                uploadedCover,
                DataType.COVER);

        super.saveOrUpdate(selectedContent, selectedGenre, mediaFile, cover);
        if(edit){
            messages.info("Updated successfull!");
            updateEvent.fire(new UpdateContentEvent(selectedContent.getId()));
        }else{
            messages.info("Created successfull!");
            createEvent.fire(new CreateContentEvent(selectedContent.getId(), getExpandedCatalogIds()));
        }

        updateCatalogEvent.fire(new UpdateCatalogEvent(selectedGenre.getId()));
        fileUploadBean.clearUploadData();
        imgFileUploadBean.clearUploadData();
    }

    @Admin
    public void delete() {
        DeleteContentEvent event = new DeleteContentEvent(selectedContent.getId(), getExpandedCatalogIds());
        super.delete(selectedContent);
        messages.info("Deleted successfull!");
        deleteEvent.fire(event);
        selectedContent = null;
        selectedGenre = catalogManager.getById(selectedGenre.getId());
        fileUploadBean.clearUploadData();
        imgFileUploadBean.clearUploadData();
    }

    private Set<Long> getExpandedCatalogIds() {
        Set<Long> expandedCatalogIds = new HashSet<Long>();
        Genre parent = selectedGenre;
        while (parent != null) {
            expandedCatalogIds.add(parent.getId());
            parent = parent.getParent();
        }
        return expandedCatalogIds;
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

    public FileUploadBean getImgFileUploadBean() {
        return imgFileUploadBean;
    }

    public String getCoverUrl(String format) {
        if (selectedContent == null || selectedContent.getCover() == null) return null;
        return viewHelper.getImgUrlExt(selectedContent.getCover(), format);
    }

}

