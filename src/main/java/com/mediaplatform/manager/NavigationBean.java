package com.mediaplatform.manager;

import javax.inject.Named;
import java.io.Serializable;

/**
 * User: timur
 * Date: 6/1/13
 * Time: 4:21 PM
 */
@Named
public class NavigationBean implements Serializable {
    public String getContentAfterEdit(long id){
        return "/content/viewVideoOnDemand.xhtml?faces-redirect=true&id=" + id;
    }

    public String getLiveStreamList(){
        return "/content/viewLiveStreamList.xhtml?faces-redirect=true";
    }

    public String getContentList(long genreId){
        return "/content/viewContentList.xhtml?faces-redirect=true&id=" + genreId;
    }
}
