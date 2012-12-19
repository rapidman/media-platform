package com.mediaplatform.manager.media;

import com.mediaplatform.manager.AbstractManager;
import com.mediaplatform.model.Catalog;
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

    public List<Catalog> getRootCatalogs(){
        String hql = "select distinct c from Catalog c left join fetch c.children where c.parent is null order by c.id";
        return appEm.createQuery(hql).getResultList();
    }

    public Catalog getById(Long id){
        Catalog result = appEm.find(Catalog.class, id);
        result.getContentList().size();
        result.getChildren().size();
        return result;
    }

    public void delete(Catalog catalog){
        if(catalog.getParent() != null){
            catalog.getParent().getChildren().remove(catalog);
        }
        appEm.remove(catalog);
    }

    public void update(Catalog catalog, FileEntry icon) {
        if(icon != null){
            appEm.persist(icon);
            catalog.setIcon(icon);
        }
        appEm.merge(catalog);
    }

    public TwoTuple<Catalog, List<Content>> getCatalogContentList(Long catalogId) {
        Catalog catalog = getById(catalogId);
        List<Content> contentList = new ArrayList<Content>(catalog.getContentList());
        addContent(catalog.getChildren(), contentList);
        return new TwoTuple<Catalog, List<Content>>(catalog, contentList);
    }

    private void addContent(List<Catalog> catalogList, List<Content> contentList) {
        for(Catalog catalog:catalogList){
            contentList.addAll(catalog.getContentList());
            addContent(catalog.getChildren(), contentList);
        }
    }
}
