package com.mediaplatform.manager;

import javax.inject.Named;

/**
 * User: timur
 * Date: 6/1/13
 * Time: 4:21 PM
 */
@Named
public class NavigationBean {
    public String getContentAfterEdit(int id){
        return "/content/viewVideoOnDemand.xhtml?faces-redirect=true&id=" + id;
    }
}
