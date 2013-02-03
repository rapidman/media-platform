package com.mediaplatform.manager.media;

import com.mediaplatform.jsf.fileupload.FileUploadBean;
import com.mediaplatform.manager.AbstractManager;
import com.mediaplatform.model.Genre;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.FileEntry;
import com.mediaplatform.util.TwoTuple;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 9:33 PM
 */
public class AbstractCatalogManager extends AbstractManager {

    @Inject
    protected Conversation conversation;

    public List<Genre> getRootCatalogs(){
        String hql = "select distinct c from Genre c left join fetch c.children where c.parent is null order by c.id";
        return appEm.createQuery(hql).getResultList();
    }

    public Genre getById(Long id){
        Genre result = appEm.find(Genre.class, id);
        if(result == null) return null;
        result.getContentList().size();
        result.getChildren().size();
        return result;
    }

    public void delete(Genre genre){
        genre = getById(genre.getId());
        if(genre.getParent() != null){
            genre.getParent().getChildren().remove(genre);
        }
        appEm.remove(genre);
    }

    public void saverOrUpdate(Genre genre, FileEntry icon) {
        if(icon != null){
            appEm.persist(icon);
            genre.setIcon(icon);
        }
        if(genre.getId() == null){
            appEm.persist(genre);
        }else{
            appEm.merge(genre);
        }
    }

    public TwoTuple<Genre, List<Content>> getCatalogContentList(Long catalogId) {
        Genre genre = getById(catalogId);
        List<Content> contentList = new ArrayList<Content>(genre.getContentList());
        addContent(genre.getChildren(), contentList);
        return new TwoTuple<Genre, List<Content>>(genre, contentList);
    }

    private void addContent(List<Genre> genreList, List<Content> contentList) {
        for(Genre genre : genreList){
            contentList.addAll(genre.getContentList());
            addContent(genre.getChildren(), contentList);
        }
    }



    public Conversation getConversation() {
        return conversation;
    }
}
