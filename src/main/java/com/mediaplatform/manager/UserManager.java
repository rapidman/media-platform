package com.mediaplatform.manager;

import com.mediaplatform.model.User;
import com.mediaplatform.security.Admin;
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
import java.util.Iterator;
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

    private List<User> users;

    private User currentUser;

    @Inject
    private FacesContext facesContext;

    private Boolean canEdit;

    private Boolean owner;

    @com.mediaplatform.security.User
    public void save() {
        if (checkRights()) return;
        appEm.merge(currentUser);
        users = null;
    }

    private boolean checkRights() {
        if (!Restrictions.isAdminOrOwner(identity, currentUserInstance.get(), currentUser)) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Insufficient rights", null));
            FacesUtil.redirectToDeniedPage();
            return true;
        }
        return false;
    }

    public boolean isCanEdit(){
        if(canEdit == null && identity.isLoggedIn()){
            canEdit = Restrictions.isAdminOrOwner(identity, currentUserInstance.get(), currentUser);
        }else{
            canEdit = false;
        }
        return canEdit;
    }

    public boolean isOwner(){
        if(owner == null && identity.isLoggedIn()){
            owner = Restrictions.isOwner(currentUserInstance.get(), currentUser);
        }else{
            owner = false;
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
        this.currentUser = getById(id);
    }

    public User getById(String id) {
        return appEm.find(User.class, id);
    }

    public void validateUserId(javax.faces.context.FacesContext facesContext, javax.faces.component.UIComponent uiComponent, java.lang.Object obj){
        boolean error = FacesUtil.validateRequired(facesContext, obj, "Username not defined");
        if(!error){
            if(getById(String.valueOf(obj)) == null){
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Content with ID '" + obj + "' not found", null));
                error = true;
            }
        }
        if(error){
            FacesUtil.redirectToHomePage();
        }
    }

    private void refresh() {
        currentUser = null;
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

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
}

