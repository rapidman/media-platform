package com.mediaplatform.manager;

import com.mediaplatform.account.CurrentUserManager;
import com.mediaplatform.i18n.DefaultBundleKey;
import com.mediaplatform.model.User;
import com.mediaplatform.security.Restrictions;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.util.jsf.FacesUtil;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * User: timur
 * Date: 12/23/12
 * Time: 9:13 PM
 */
@Stateful
@ConversationScoped
@Named
public class UserManager extends AbstractManager {
    @Inject
    protected Conversation conversation;

    @Inject
    private CurrentUserManager currentUserManager;

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

    @com.mediaplatform.security.User
    public void save() {
        if (checkRights()) return;
        appEm.merge(selectedUser);
        if(Restrictions.isOwner(currentUser, selectedUser)){
            currentUserManager.updateCurrentUser(selectedUser);
        }
        messages.info(new DefaultBundleKey("account_saved")).defaults("Account successfully updated.");
        users = null;
        ConversationUtils.safeEnd(conversation);
    }

    private boolean checkRights() {
        if (!Restrictions.isAdminOrOwner(identity, currentUser, selectedUser)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient rights", null));
            FacesUtil.redirectToDeniedPage();
            return true;
        }
        return false;
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
        if (checkRights()) return;
        User user = getById(currentUser.getUsername());
        appEm.remove(user);
        refresh();
    }

    public void viewUser(String id) {
        refresh();
        ConversationUtils.safeBegin(conversation);
        this.selectedUser = getById(id);
    }

    public User getById(String id) {
        return appEm.find(User.class, id);
    }

    public void validateUserId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj){
        boolean ok = FacesUtil.validateRequired(facesContext, obj, "Username not defined");
        if(ok){
            if(getById(String.valueOf(obj)) == null){
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
}

