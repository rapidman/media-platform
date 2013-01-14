package com.mediaplatform.manager;

import com.mediaplatform.manager.media.ContentManager;
import com.mediaplatform.model.Comment;
import com.mediaplatform.model.Content;
import org.jboss.solder.servlet.http.RequestParam;

import javax.enterprise.inject.Instance;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 1:05 AM
 */
@ViewScoped
@Named
public class CommentManager extends AbstractManager implements Serializable{
    @Inject @RequestParam("cntid")
    private Instance<Long> selectedContentId;
    private Comment currentComment;
    private List<Comment> comments;
    private Long contentId;
    @Inject
    private ContentManager contentManager;

    public void addComment(){
        Content currentContent = contentManager.getById(contentId);
        currentComment.setContent(currentContent);
        appEm.persist(currentComment);
        currentContent.getComments().add(currentComment);
        appEm.merge(currentContent);
        comments = null;
    }

    public Comment getCurrentComment() {
        if(currentComment == null){
            currentComment = new Comment();
        }
        return currentComment;
    }

    public void setCurrentComment(Comment currentComment) {
        this.currentComment = currentComment;
    }

    public List<Comment> getComments() {
        if(comments == null){
            comments = appEm.createQuery("select c from Comment c where c.content.id = :contentId order by c.createDateTime desc").setParameter("contentId", contentId).getResultList();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Long getContentId() {
        if(contentId == null){
            contentId = selectedContentId.get();
        }
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

}
