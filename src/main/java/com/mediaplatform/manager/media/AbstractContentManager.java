package com.mediaplatform.manager.media;

import com.mediaplatform.manager.AbstractManager;
import com.mediaplatform.manager.UserManager;
import com.mediaplatform.model.*;
import com.mediaplatform.model.User;
import com.mediaplatform.security.*;

import javax.ejb.TransactionAttribute;
import javax.enterprise.context.Conversation;
import javax.enterprise.inject.Instance;
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
    protected Instance<UserManager> userManagerInstance;

    @Inject
    protected Conversation conversation;

    public Content getContentById(Long id) {
        return appEm.find(Content.class, id);
    }

    protected void saveOrUpdate(Content content, Genre genre, FileEntry video, FileEntry cover) {
        content.setGenre(genre);
        if (video != null) {
            appEm.persist(video);
            content.setMediaFile(video);
        }
        if (cover != null) {
            appEm.persist(cover);
            content.setCover(cover);
        }
        if (content.getId() == null) {
            User author = currentUser;
            content.setAuthor(author);
            appEm.persist(content);
            author = userManagerInstance.get().findByUsername(author.getUsername());
            author.getContents().add(content);
            appEm.merge(author);
        } else {
            appEm.merge(content);
        }
        if (!genre.getContentList().contains(content)) {
            genre.getContentList().add(content);
            appEm.merge(genre);
        }
    }

    protected void update(Content content) {
        appEm.merge(content);
    }

    protected void delete(Content content) {
        content = getContentById(content.getId());
        Genre genre = content.getGenre();
        genre.getContentList().remove(content);
        appEm.merge(genre);
        appEm.remove(content);
    }

    public LiveStream getStreamById(Long id) {
        return appEm.find(LiveStream.class, id);
    }

    public List<Content> findLatestList(int maxResult) {
        return findPopularList(maxResult);
    }

    public List<Content> findPopularList(int maxResult) {
        if(appCacheBean.getPopularContents() == null){
            appCacheBean.setPopularContents(appEm.createQuery("select c from Content c order by c.id desc").setMaxResults(maxResult).getResultList());
        }
        return appCacheBean.getPopularContents();
    }

    public Content findContentByFileName(String name) {
        return (Content) appEm.createQuery("select c from Content c where c.mediaFile.name = :name").setParameter("name", name).getSingleResult();
    }

    public String getCallbackQueryParams() {
        String out = CONTENT_NAME_PARAM_NAME + "=" + getCurrentContentName();
        if (identity.isLoggedIn()) {
            out += "&" + UNAME_PARAM_NAME + "=" + identity.getUser().getId();
        }
        return out;
    }

    protected abstract String getCurrentContentName();

    public Conversation getConversation() {
        return conversation;
    }
}
