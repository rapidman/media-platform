package com.mediaplatform.manager;

import com.mediaplatform.account.CurrentUserManager;
import com.mediaplatform.account.PasswordManager;
import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.jsf.fileupload.UploadedFile;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.*;
import com.mediaplatform.security.Restrictions;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.ViewHelper;
import com.mediaplatform.util.jsf.FacesUtil;
import org.richfaces.component.SortOrder;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;

/**
 * User: timur
 * Date: 12/23/12
 * Time: 9:13 PM
 */
@Stateful
@ConversationScoped
@Named
public class UserManager extends AbstractUserManager{
    @Inject
    protected Conversation conversation;

    @Inject
    private Instance<CurrentUserManager> currentUserManager;

    private User selectedUser;

    @Inject
    private Instance<PasswordManager> passwordManagerInstance;

    @Inject
    private FacesContext facesContext;

    @Inject
    private Instance<ViewHelper> viewHelper;

    private FileUploadBean imgFileUploadBean = new FileUploadBean();

    @Inject
    private Instance<FileStorageManager> fileStorageManager;

    @Inject
    private Event<User> deleteEvent;

    @Inject
    private Event<User> updateEvent;

    private SortOrder nameOrder = SortOrder.unsorted;

    private SortOrder contentCountOrder = SortOrder.unsorted;

    private SortOrder rateOrder = SortOrder.unsorted;

    private List<User> sortedTopUsers;

    public void sortByName() {
        sortedTopUsers = null;
        contentCountOrder = SortOrder.unsorted;
        rateOrder = SortOrder.unsorted;
        if (nameOrder.equals(SortOrder.ascending)) {
            setNameOrder(SortOrder.descending);
        } else {
            setNameOrder(SortOrder.ascending);
        }
    }

    public void sortByPostCount() {
        sortedTopUsers = null;
        nameOrder = SortOrder.unsorted;
        rateOrder = SortOrder.unsorted;
        if (contentCountOrder.equals(SortOrder.ascending)) {
            setContentCountOrder(SortOrder.descending);
        } else {
            setContentCountOrder(SortOrder.ascending);
        }
    }

    @com.mediaplatform.security.User
    public void save() {
        if (!checkRightsToEdit()) return;

        if (imgFileUploadBean.getSize() > 0) {
            UploadedFile uploadedAvatar = imgFileUploadBean.getFiles().get(0);
            FileEntry avatar = fileStorageManager.get().saveFile(
                    new ParentRef(selectedUser.getId(),
                            selectedUser.getEntityType()),
                    uploadedAvatar,
                    DataType.AVATAR);
            appEm.persist(avatar);
            selectedUser.setAvatar(avatar);
        }
        update(selectedUser);
        currentUserManager.get().updateCurrentUser(selectedUser);
        messages.info(new DefaultBundleKey("account_saved")).defaults("Аккаунт обновлен.");
        ConversationUtils.safeEnd(conversation);
        updateEvent.fire(selectedUser);
    }

    @com.mediaplatform.security.User
    public void changePassword() {
        if (!checkRightsToEdit()) return;
        selectedUser.setPassword(passwordManagerInstance.get().getConfirmPassword());
        update(selectedUser);
        messages.info(new DefaultBundleKey("account_password_changed")).defaults("Пароль изменен.");
    }

    @com.mediaplatform.security.User
    public void remove(User currentUser) {
        if (!checkRightsToEdit()) return;
        User user = findByUsername(currentUser.getUsername());
        appEm.remove(user);
        ConversationUtils.safeEnd(conversation);
        refresh();
        deleteEvent.fire(user);
    }

    public void show() {
        ConversationUtils.safeBegin(conversation);
        refresh();
    }

    @com.mediaplatform.security.User
    public void viewUser(String userName) {
        refresh();
        this.selectedUser = findByUsername(userName);
        if (!checkRightsForView()) return;
        ConversationUtils.safeBegin(conversation);
    }

    public void validateUserId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj) {
        if ("conversation_ended".equals(obj)) {
            FacesUtil.redirectToEndConversation();
            return;
        }

        boolean ok = FacesUtil.validateRequired(facesContext, obj, "Не задан ник пользователя");
        if (ok) {
            if (findByUsername(String.valueOf(obj)) == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Контент с указанным '" + obj + "' ИД не найден.", null));
                ok = false;
            }
        }
        if (!ok) {
            FacesUtil.redirectToHomePage();
        }
    }

    private void refresh() {
        selectedUser = null;
    }

    private boolean checkRightsToEdit() {
        if (identity == null || currentUser == null || selectedUser == null) {
            FacesUtil.redirectToEndConversation();
            return false;
        }
        if (!Restrictions.isAdminOrOwner(identity, currentUser, selectedUser)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Недостаточно прав", null));
            FacesUtil.redirectToDeniedPage();
            return false;
        }
        return true;
    }

    private boolean checkRightsForView() {
        if (!identity.isLoggedIn()) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Недостаточно прав", null));
            FacesUtil.redirectToDeniedPage();
            return false;
        }

        if (identity == null || currentUser == null || selectedUser == null) {
            FacesUtil.redirectToEndConversation();
            return false;
        }

        return true;
    }

    public boolean isCanEdit() {
        return checkCanEdit(selectedUser);
    }

    public boolean checkCanEdit(User checkedUser) {
        if (checkedUser == null || !identity.isLoggedIn()) return false;
        boolean canEdit;
        if (Restrictions.isAdminOrOwner(identity, currentUser, checkedUser)) {
            canEdit = true;
        } else {
            canEdit = false;
        }
        return canEdit;
    }

    public boolean isOwner() {
        return checkOwner(selectedUser);
    }

    public boolean checkOwner(User owner) {
        if (!identity.isLoggedIn()) return false;
        boolean isOwner;
        if (Restrictions.isOwner(currentUser, owner)) {
            isOwner = true;
        } else {
            isOwner = false;
        }
        return isOwner;
    }

    public boolean checkAdmin(){
        return Restrictions.checkAdmin(identity);
    }

    public List<User> getSortedTopUserList(){
        if(sortedTopUsers == null){
            sortedTopUsers = findUsers(nameOrder, contentCountOrder, rateOrder);
        }

        return sortedTopUsers;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser) {
        this.selectedUser = selectedUser;
    }

    public String getAvatarUrl(String format) {
        if (selectedUser == null || selectedUser.getAvatar() == null) return null;
        return viewHelper.get().getImgUrlExt(selectedUser.getAvatar(), format);
    }

    public FileUploadBean getImgFileUploadBean() {
        return imgFileUploadBean;
    }

    public void setImgFileUploadBean(FileUploadBean imgFileUploadBean) {
        this.imgFileUploadBean = imgFileUploadBean;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public SortOrder getNameOrder() {
        return nameOrder;
    }

    public void setNameOrder(SortOrder nameOrder) {
        this.nameOrder = nameOrder;
    }

    public SortOrder getContentCountOrder() {
        return contentCountOrder;
    }

    public void setContentCountOrder(SortOrder contentCountOrder) {
        this.contentCountOrder = contentCountOrder;
    }

    public SortOrder getRateOrder() {
        return rateOrder;
    }

    public void setRateOrder(SortOrder rateOrder) {
        this.rateOrder = rateOrder;
    }

    public Gender[] getGenderValues() {
       return Gender.values();
    }
}

