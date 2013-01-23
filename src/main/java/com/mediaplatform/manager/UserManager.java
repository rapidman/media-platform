package com.mediaplatform.manager;

import com.mediaplatform.account.CurrentUserManager;
import com.mediaplatform.event.DeleteCatalogEvent;
import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.jsf.fileupload.UploadedFile;
import com.mediaplatform.manager.file.FileStorageManager;
import com.mediaplatform.model.DataType;
import com.mediaplatform.model.FileEntry;
import com.mediaplatform.model.ParentRef;
import com.mediaplatform.model.User;
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
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.NoResultException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
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
    private FacesContext facesContext;

    @NotNull
    @Size(min = 5, max = 15)
    private String confirmPassword;

    @NotNull
    @Size(min = 5, max = 15)
    private String newPassword;

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

    private boolean changePassword = false;

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
        if (!checkRights()) return;

        UploadedFile uploadedAvatar = null;
        if (imgFileUploadBean.getSize() > 0) {
            uploadedAvatar = imgFileUploadBean.getFiles().get(0);
        }
        FileEntry avatar = fileStorageManager.get().saveFile(
                new ParentRef(selectedUser.getId(),
                        selectedUser.getEntityType()),
                uploadedAvatar,
                DataType.AVATAR);
        appEm.persist(avatar);
        selectedUser.setAvatar(avatar);
        appEm.merge(selectedUser);
        if (Restrictions.isOwner(currentUser, selectedUser)) {
            currentUserManager.get().updateCurrentUser(selectedUser);
        }
        messages.info(new DefaultBundleKey("account_saved")).defaults("Account successfully updated.");
        ConversationUtils.safeEnd(conversation);
        updateEvent.fire(selectedUser);
    }

    @com.mediaplatform.security.User
    public void remove(User currentUser) {
        if (!checkRights()) return;
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
        if (!checkRights()) return;
        ConversationUtils.safeBegin(conversation);
    }

    public void validateUserId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj) {
        if ("conversation_ended".equals(obj)) {
            FacesUtil.redirectToEndConversation();
            return;
        }

        boolean ok = FacesUtil.validateRequired(facesContext, obj, "Username not defined");
        if (ok) {
            if (findByUsername(String.valueOf(obj)) == null) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content with ID '" + obj + "' not found", null));
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

    private boolean checkRights() {
        if (identity == null || currentUser == null || selectedUser == null) {
            FacesUtil.redirectToEndConversation();
            return false;
        }
        if (!Restrictions.isAdminOrOwner(identity, currentUser, selectedUser)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient rights", null));
            FacesUtil.redirectToDeniedPage();
            return false;
        }
        return true;
    }

    public boolean isCanEdit() {
        return checkCanEdit(selectedUser);
    }

    public boolean checkCanEdit(User checkedUser) {
        if (!identity.isLoggedIn()) return false;
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
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

    public boolean isChangePassword() {
        return changePassword;
    }

    public void setChangePassword(boolean changePassword) {
        this.changePassword = changePassword;
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
}

