package com.mediaplatform.manager;

import com.mediaplatform.account.CurrentUserManager;
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

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
public class UserManager extends AbstractManager implements Serializable{
    @Inject
    protected Conversation conversation;

    @Inject
    private Instance<CurrentUserManager> currentUserManager;

    private List<User> users;

    private User selectedUser;

    @Inject
    private FacesContext facesContext;

    private Boolean canEdit;

    private Boolean owner;

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

    private boolean changePassword = false;

    @com.mediaplatform.security.User
    public void save() {
        if (!checkRights()) return;

        UploadedFile uploadedAvatar = null;
        if(imgFileUploadBean.getSize() > 0){
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
        if(Restrictions.isOwner(currentUser, selectedUser)){
            currentUserManager.get().updateCurrentUser(selectedUser);
        }
        messages.info(new DefaultBundleKey("account_saved")).defaults("Account successfully updated.");
        ConversationUtils.safeEnd(conversation);
    }

    private boolean checkRights() {
        if (!Restrictions.isAdminOrOwner(identity, currentUser, selectedUser)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient rights", null));
            FacesUtil.redirectToDeniedPage();
            return false;
        }
        return true;
    }

    public boolean isCanEdit(){
        if(!identity.isLoggedIn()) return false;
        if(canEdit == null){
            if(Restrictions.isAdminOrOwner(identity, currentUser, selectedUser)){
                canEdit = true;
            }else{
                canEdit = false;
            }
        }
        return canEdit;
    }

    public boolean isOwner(){
        if(!identity.isLoggedIn()) return false;
        if(owner == null){
            if(Restrictions.isOwner(currentUser, selectedUser)){
                owner = true;
            }else{
                owner = false;
            }
        }
        return owner;
    }

    public void show() {
        ConversationUtils.safeBegin(conversation);
        refresh();
    }

    @com.mediaplatform.security.User
    public void remove(User currentUser) {
        if (!checkRights()) return;
        User user = findByUsername(currentUser.getUsername());
        appEm.remove(user);
        refresh();
    }

    @com.mediaplatform.security.User
    public void viewUser(String userName) {
        if (!checkRights()) return;
        refresh();
        ConversationUtils.safeBegin(conversation);
        this.selectedUser = findByUsername(userName);
    }

    public User findByUsername(String userName){
        try {
            return (User) appEm.createQuery("select u from User u where u.username = :username").setParameter("username", userName).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void validateUserId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj){
        if("conversation_ended".equals(obj)){
            FacesUtil.redirect("conversation_ended");
            return;
        }

        boolean ok = FacesUtil.validateRequired(facesContext, obj, "Username not defined");
        if(ok){
            if(findByUsername(String.valueOf(obj)) == null){
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content with ID '" + obj + "' not found", null));
                ok = false;
            }
        }
        if(!ok){
            FacesUtil.redirectToHomePage();
        }
    }

    private void refresh() {
        selectedUser = null;
        users = null;
        canEdit = null;
        owner = null;
    }

    public List<User> getUsers() {
        if (users == null) {
            users = appEm.createQuery("select u from User u").getResultList();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
}

