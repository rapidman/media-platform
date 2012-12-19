package com.mediaplatform.manager.media;

import com.mediaplatform.manager.AbstractManager;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.FileEntry;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import java.util.List;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 9:18 PM
 */
public class AbstractContentManager extends AbstractManager {
    @Inject
    protected Conversation conversation;

    public Content getById(Long id){
        return appEm.find(Content.class, id);
    }

    public void update(Content content, FileEntry mediaFile) {
        if(mediaFile != null){
            appEm.persist(mediaFile);
        }
        content.setMediaFile(mediaFile);
        appEm.merge(content);
    }

    public void update(Content content) {
        appEm.merge(content);
    }

    public void delete(Content content) {
        content = getById(content.getId());
        content.getCatalog().getContentList().remove(content);
        appEm.remove(content);
    }

    public List<Content> findLatestList(int maxResult) {
        return findPopularList(maxResult);
    }

    public List<Content> findPopularList(int maxResult) {
        return appEm.createQuery("select c from Content c order by c.id desc").setMaxResults(maxResult).getResultList();
    }
}
