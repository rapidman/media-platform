package com.mediaplatform.manager;

import com.mediaplatform.model.User;
import com.mediaplatform.security.Admin;
import com.mediaplatform.util.ConversationUtils;

import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
public class UserList extends AbstractManager {
    @Inject
    protected Conversation conversation;

    private List<User> users;

    private User currentUser;

    @Admin
    public void save(){
        appEm.merge(currentUser);
        users = null;
    }

    public void show(){
        ConversationUtils.safeBegin(conversation);
        refresh();
    }

    @Admin
    public void remove(User currentUser){
        User user = appEm.find(User.class, currentUser.getUsername());
        appEm.remove(user);
        refresh();
    }

    @Admin
    public void edit(User currentUser){
        ConversationUtils.safeBegin(conversation);
        this.currentUser = currentUser;
    }

    private void refresh() {
        currentUser = null;
        users = null;
    }

    public List<User> getUsers() {
        if(users == null){
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

