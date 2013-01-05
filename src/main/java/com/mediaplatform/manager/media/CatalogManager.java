package com.mediaplatform.manager.media;

import com.mediaplatform.event.DeleteCatalogEvent;
import com.mediaplatform.event.EditCatalogEvent;
import com.mediaplatform.event.UpdateCatalogEvent;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.ViewHelper;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.Catalog;
import com.mediaplatform.model.DataType;
import com.mediaplatform.model.FileEntry;
import com.mediaplatform.model.ParentRef;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 3:41 PM
 */
@Stateful
@ConversationScoped
@Named
public class CatalogManager extends AbstractCatalogManager {
    private Catalog selectedCatalog;
    @Inject
    private Event<EditCatalogEvent> editEvent;
    @Inject
    private Event<UpdateCatalogEvent> updateEvent;
    @Inject
    private Event<DeleteCatalogEvent> deleteEvent;

    @Inject
    private FileStorageManager fileStorageManager;
    @Inject
    private ViewHelper viewHelper;


    @Admin
    public void editCatalog(Long id) {
        ConversationUtils.safeBegin(conversation);
        selectedCatalog = getById(id);
        editEvent.fire(new EditCatalogEvent(selectedCatalog.getId()));
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void addCatalog(Long parentId) {
        ConversationUtils.safeBegin(conversation);
        selectedCatalog = new Catalog();
        selectedCatalog.setParent(getById(parentId));
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void addRootCatalog() {
        ConversationUtils.safeBegin(conversation);
        selectedCatalog = new Catalog();
        fileUploadBean.clearUploadData();
    }

    public Catalog getSelectedCatalog() {
        return selectedCatalog;
    }

    public String getHeader() {
        boolean edit = selectedCatalog.getId() != null;
        if (edit) {
            return "Edit " + selectedCatalog.getTitle();
        }
        if (selectedCatalog.getParent() != null) {
            return "Adding new catalog to " + selectedCatalog.getParent().getTitle();
        }

        return "Adding root catalog";
    }

    public void setSelectedCatalog(Catalog selectedCatalog) {
        this.selectedCatalog = selectedCatalog;
    }

    public String getIconUrl(String format) {
        if (selectedCatalog == null || selectedCatalog.getIcon() == null) return "";
        return viewHelper.getImgUrlExt(selectedCatalog.getIcon(), format);
    }

    @Admin
    public void saveOrUpdate() {
        FileEntry icon = null;
        if (fileUploadBean.getSize() > 0) {
            icon = fileStorageManager.saveFile(
                    new ParentRef(selectedCatalog.getId(),
                            selectedCatalog.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.ICON);
        }
        boolean update = selectedCatalog.getId() != null;
        super.saverOrUpdate(selectedCatalog, icon);
        updateEvent.fire(new UpdateCatalogEvent(selectedCatalog.getId()));

        if (update) {
            messages.info("Update successfull");
        } else {
            messages.info("Create successfull");
        }
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void delete() {
        super.delete(selectedCatalog);
        deleteEvent.fire(new DeleteCatalogEvent(selectedCatalog.getId()));
        this.selectedCatalog = null;
        messages.info("Delete successfull");

        if (!conversation.isTransient()) {
            conversation.end();
        }
        ConversationUtils.safeEnd(conversation);
        fileUploadBean.clearUploadData();
    }

}

