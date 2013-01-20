package com.mediaplatform.manager;

import com.mediaplatform.event.*;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.User;

import javax.ejb.Stateful;
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
    private List<Content> popularContents;

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

    public void setPopularContents(List<Content> popularContents) {
        this.popularContents = popularContents;
    }

    public List<Content> getPopularContents() {
        return popularContents;
    }

    public void observeCatalogDelete(@Observes DeleteCatalogEvent deleteEvent) {
        popularContents = null;
    }

    public void observeContentDelete(@Observes DeleteContentEvent deleteEvent) {
        popularContents = null;
    }

    public void observeContentCreate(@Observes CreateContentEvent createEvent) {
        popularContents = null;
    }

    public void observeDeleteUser(@Observes User deletedUser){
        refreshUsers();
    }

    public void observeUpdateUser(@Observes User updatedUser){
        refreshUsers();
    }

    private void refreshUsers() {
        allUsers = null;
        topUsers = null;
    }
}