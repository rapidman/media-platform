package com.mediaplatform.manager.media;

import com.mediaplatform.event.*;
import com.mediaplatform.jsf.CatalogTreeBean;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.jsf.fileupload.UploadedFile;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.*;
import com.mediaplatform.security.*;
import com.mediaplatform.security.User;
import com.mediaplatform.util.*;
import com.mediaplatform.util.jsf.FacesUtil;
import org.jboss.solder.servlet.http.RequestParam;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.faces.application.FacesMessage;
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
public class ContentManager extends AbstractContentManager implements Serializable {
    private Content selectedContent;
    private Genre selectedGenre;
    @Inject
    private Event<UpdateContentEvent> updateEvent;
    @Inject
    private Event<CreateContentEvent> createEvent;
    @Inject
    private Event<SelectGenreEvent> selectGenreEventEvent;
    @Inject
    private Event<UpdateCatalogEvent> updateCatalogEvent;
    @Inject
    private Event<DeleteContentEvent> deleteEvent;
    @Inject
    private CatalogManager catalogManager;

    private FileUploadBean fileUploadBean = new FileUploadBean();

    private FileUploadBean imgFileUploadBean = new FileUploadBean();

    @Inject
    private Instance<FileStorageManager> fileStorageManager;

    @Inject
    private Instance<ViewHelper> viewHelper;

    private static final int HOME_PAGE_LIST_MAX_SIZE = 10;


    @Inject
    @RequestParam("cntid")
    private Instance<Long> selectedContentId;


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
        if (selectedGenre == null) return new ArrayList<Content>();
        return selectedGenre.getContentList();
    }

    public String viewByGenreId(String genreIdStr) {
        Long genreId = Long.parseLong(genreIdStr);
        ConversationUtils.safeBegin(conversation);

        TwoTuple<Genre, List<Content>> result = catalogManager.getCatalogContentList(genreId);
        selectedGenre = result.first;
        selectGenreEventEvent.fire(new SelectGenreEvent(selectedGenre.getId(), getExpandedCatalogIds()));
        return "/view-content-list";
    }

    @com.mediaplatform.security.User
    public void add() {
        ConversationUtils.safeBegin(conversation);
        selectedContent = new Content();
    }

    public void viewVideoOnDemand(String id) {
        ConversationUtils.safeBegin(conversation);
        view(Long.parseLong(id));
    }

    public void validateContentId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj) {
        boolean ok = FacesUtil.validateLong(facesContext, uiComponent, obj, "Content ID not defined");
        if (ok) {
            Long id = Long.parseLong(String.valueOf(obj));
            if (getContentById(id) == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content with ID '" + id + "' not found", null));
                ok = false;
            }
        }
        if (!ok) {
            FacesUtil.redirectToHomePage();
        }
    }

    @User
    public void editContent(Long id) {
        view(id);
        if(!Restrictions.isAdminOrOwner(identity, currentUser, selectedContent.getAuthor())){
            FacesUtil.redirectToDeniedPage();
            return;
        }
        ConversationUtils.safeBegin(conversation);
    }

    private void view(Long id) {
        selectedContent = getContentById(id);
        selectedGenre = catalogManager.getById(selectedContent.getGenre().getId());
    }

    public void validateGenreId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj){
        boolean ok = FacesUtil.validateLong(facesContext, uiComponent, obj, "Genre ID not defined");
        if(ok){
            Long id = Long.parseLong(String.valueOf(obj));
            if(catalogManager.getById(id) == null){
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Genre with ID '" + id + "' not found", null));
                ok = false;
            }
        }
        if(!ok){
            FacesUtil.redirectToHomePage();
        }
    }

    @User
    public void saveOrUpdate() {
        if(!Restrictions.isAdminOrOwner(identity, currentUser, selectedContent.getAuthor())){
            FacesUtil.redirectToDeniedPage();
            return;
        }
        FileEntry mediaFile = null;
        boolean edit = selectedContent.getId() != null;
        super.saveOrUpdate(selectedContent, selectedGenre, null, null);
        if (fileUploadBean.getSize() > 0) {
            mediaFile = fileStorageManager.get().saveFile(
                    new ParentRef(selectedContent.getId(), selectedContent.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.MEDIA_CONTENT);
        }
        FileEntry cover = null;
        if (imgFileUploadBean.getSize() > 0) {
            cover = fileStorageManager.get().saveFile(
                    new ParentRef(selectedContent.getId(),
                            selectedContent.getEntityType()),
                    imgFileUploadBean.getFiles().get(0),
                    DataType.COVER);
        }

        super.saveOrUpdate(selectedContent, selectedGenre, mediaFile, cover);
        if (edit) {
            messages.info("Updated successfull!");
            updateEvent.fire(new UpdateContentEvent(selectedContent.getId()));
        } else {
            messages.info("Created successfull!");
            createEvent.fire(new CreateContentEvent(selectedContent.getId(), getExpandedCatalogIds()));
        }

        updateCatalogEvent.fire(new UpdateCatalogEvent(selectedGenre.getId()));
        fileUploadBean.clearUploadData();
        imgFileUploadBean.clearUploadData();
    }

    @Admin
    public void delete() {
        if(!Restrictions.isAdminOrOwner(identity, currentUser, selectedContent.getAuthor())){
            FacesUtil.redirectToDeniedPage();
            return;
        }
        DeleteContentEvent event = new DeleteContentEvent(selectedContent.getId(), getExpandedCatalogIds());
        super.delete(selectedContent);
        messages.info("Deleted successfull!");
        deleteEvent.fire(event);
        selectedContent = null;
        selectedGenre = catalogManager.getById(selectedGenre.getId());
        fileUploadBean.clearUploadData();
        imgFileUploadBean.clearUploadData();
    }

    //TODO redirect to list view
    public void cancelEdit(){
        ConversationUtils.safeEnd(conversation);
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
        String absFilePath = fileStorageManager.get().getMediaFileUrl(selectedContent.getMediaFile(), true);
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
        return viewHelper.get().getVideoUrl(selectedContent.getMediaFile());
    }

    @Override
    protected String getCurrentContentName() {
        return getMediaFileFullName();
    }

    public String getHeader() {
        boolean edit = selectedContent != null && selectedContent.getId() != null;
        if (edit) {
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
        return viewHelper.get().getImgUrlExt(selectedContent.getCover(), format);
    }


}

