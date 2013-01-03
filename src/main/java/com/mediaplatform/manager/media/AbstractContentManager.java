package com.mediaplatform.manager.media;

import com.mediaplatform.manager.AbstractManager;
import com.mediaplatform.model.Content;
import com.mediaplatform.model.FileEntry;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import java.util.List;
import static com.mediaplatform.web.NotificationListenerServlet.*;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 9:18 PM
 */
public abstract class AbstractContentManager extends AbstractManager {

    @Inject
    protected Conversation conversation;

    public Content getById(Long id){
        return appEm.find(Content.class, id);
    }

    public void update(Content content, FileEntry mediaFile) {
        if(mediaFile != null){
            appEm.persist(mediaFile);
            content.setMediaFile(mediaFile);
        }
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

    public Content findContentByFileName(String name) {
        return (Content) appEm.createQuery("select c from Content c where c.mediaFile.name = :name").setParameter("name", name).getSingleResult();
    }

    public String getCallbackQueryParams(){
        String out = CONTENT_NAME_PARAM_NAME + "=" + getCurrentContentName();
        if(identity.isLoggedIn()){
            out+="&" + UNAME_PARAM_NAME + "=" + identity.getUser().getId();
        }
        return out;
    }

    protected abstract String getCurrentContentName();

}
