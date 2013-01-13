package com.mediaplatform.manager;

import com.mediaplatform.model.Comment;
import com.mediaplatform.model.Content;

import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 1:05 AM
 */
@ViewScoped
@Named
public class CommentManager extends AbstractManager{
    @Inject
    @Named("currentContent")
    private Content currentContent;
    private Comment currentComment;
    private List<Comment> comments;

    public void addComment(){
        currentComment.setContent(currentContent);
        appEm.persist(currentComment);
        currentContent.getComments().add(currentComment);
        appEm.merge(currentContent);
        comments = null;
    }

    public Content getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(Content currentContent) {
        this.currentContent = currentContent;
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
            comments = appEm.createQuery("select c from Comment c where c.content.id = :contentId order by c.createDateTime desc").setParameter("contentId", currentContent.getId()).getResultList();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
