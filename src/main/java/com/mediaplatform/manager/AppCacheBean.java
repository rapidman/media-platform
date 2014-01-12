package com.mediaplatform.manager;

import com.mediaplatform.event.*;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.User;

import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: timur
 * Date: 1/20/13
 * Time: 8:46 PM
 */
@Stateful
@ApplicationScoped
@Named
public class AppCacheBean {
    private List<User> topUsers;
    private List<User> allUsers;

    public List<User> getTopUsers() {
        if(topUsers == null){
            topUsers = new ArrayList<User>();
        }
        return topUsers;
    }

    public void setTopUsers(List<User> topUsers) {
        this.topUsers = topUsers;
    }

    public void setAllUsers(List<User> allUsers) {
        this.allUsers = allUsers;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeDeleteUser(@Observes User deletedUser){
        refreshUsers();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeUpdateUser(@Observes User updatedUser){
        refreshUsers();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeUpdateUser(@Observes CreateContentEvent createContentEvent){
        refreshUsers();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeUpdateContent(@Observes UpdateContentEvent updateContentEvent){
        refreshUsers();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeUpdateCatalog(@Observes UpdateCatalogEvent updateCatalogEvent){
        refreshUsers();
    }

    private void refreshUsers() {
        allUsers = null;
        topUsers = null;
    }
}
