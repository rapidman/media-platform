package com.mediaplatform.manager.media;

import com.mediaplatform.event.DeleteCatalogEvent;
import com.mediaplatform.event.EditCatalogEvent;
import com.mediaplatform.event.UpdateCatalogEvent;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.ViewHelper;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.Genre;
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
    private Genre selectedGenre;
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
        selectedGenre = getById(id);
        editEvent.fire(new EditCatalogEvent(selectedGenre.getId()));
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void addCatalog(Long parentId) {
        ConversationUtils.safeBegin(conversation);
        selectedGenre = new Genre();
        selectedGenre.setParent(getById(parentId));
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void addRootCatalog() {
        ConversationUtils.safeBegin(conversation);
        selectedGenre = new Genre();
        fileUploadBean.clearUploadData();
    }

    public Genre getSelectedGenre() {
        return selectedGenre;
    }

    public String getHeader() {
        boolean edit = selectedGenre.getId() != null;
        if (edit) {
            return "Редактирование " + selectedGenre.getTitle();
        }
        if (selectedGenre.getParent() != null) {
            return "Добавление нового жанра в " + selectedGenre.getParent().getTitle();
        }

        return "Добавление корневого жанра.";
    }

    public void setSelectedGenre(Genre selectedGenre) {
        this.selectedGenre = selectedGenre;
    }

    public String getIconUrl(String format) {
        if (selectedGenre == null || selectedGenre.getIcon() == null) return null;
        return viewHelper.getImgUrlExt(selectedGenre.getIcon(), format);
    }

    @Admin
    public void saveOrUpdate() {
        FileEntry icon = null;
        boolean update = selectedGenre.getId() != null;
        super.saverOrUpdate(selectedGenre, icon);
        if (fileUploadBean.getSize() > 0) {
            icon = fileStorageManager.saveFile(
                    new ParentRef(selectedGenre.getId(),
                            selectedGenre.getEntityType()),
                    fileUploadBean.getFiles().get(0),
                    DataType.ICON);
        }
        super.saverOrUpdate(selectedGenre, icon);
        updateEvent.fire(new UpdateCatalogEvent(selectedGenre.getId()));

        if (update) {
            messages.info("Обновленно успешно!");
        } else {
            messages.info("Создано успешно");
        }
        fileUploadBean.clearUploadData();
    }

    @Admin
    public void delete() {
        super.delete(selectedGenre);
        deleteEvent.fire(new DeleteCatalogEvent(selectedGenre.getId()));
        this.selectedGenre = null;
        messages.info("Удалено успешно");

        if (!conversation.isTransient()) {
            conversation.end();
        }
        fileUploadBean.clearUploadData();
    }

}

