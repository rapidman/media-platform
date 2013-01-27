package com.mediaplatform.manager;

import com.mediaplatform.jsf.CommentTreeNode;
import com.mediaplatform.manager.media.ContentManager;
import com.mediaplatform.model.Comment;
import com.mediaplatform.model.Content;
import com.mediaplatform.security.User;

import javax.ejb.TransactionAttribute;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 1:05 AM
 */

@ViewScoped
@Named
public class CommentManager extends AbstractManager implements Serializable{

    private Comment currentComment;
    private List<CommentTreeNode> comments;
    private Long contentId;
    private Long parentCommentId;
    private Long commentId;
    @Inject
    private ContentManager contentManager;

    @TransactionAttribute
    @User
    public void addComment(){
        Content currentContent = saveComment();
        currentContent.getComments().add(currentComment);
        appEm.merge(currentContent);
        comments = null;
        currentComment = null;
    }

    private Content saveComment() {
        Content currentContent = contentManager.getContentById(contentId);
        currentComment.setContent(currentContent);
        currentComment.setAuthor(currentUser);
        appEm.persist(currentComment);
        return currentContent;
    }

    @TransactionAttribute
    @User
    public void replyComment(){
        Comment parentComment = findById(parentCommentId);
        currentComment.setParent(parentComment);
        saveComment();
        parentComment.getReplies().add(currentComment);
        appEm.merge(parentComment);
        comments = null;
        currentComment = null;
        parentCommentId = null;
    }

    @TransactionAttribute
    @User
    public void delete(Comment comment){
        comment = findById(comment.getId());
        comment.setDeleted(true);
        appEm.merge(comment);
        messages.info("Комментарий удален.");
        refresh();
    }

    @TransactionAttribute
    @User
    public void deleteReply(){
        Comment comment = findById(commentId);
        delete(comment);
        commentId = null;
    }

    private Comment findById(Long id) {
        return appEm.find(Comment.class, id);
    }

    private void refresh() {
        comments = null;
    }

    public void assignContentId(String id) {
        if(id != null){
            this.contentId = Long.parseLong(id);
        }
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

    public List<CommentTreeNode> getComments() {
        if(comments == null){
            comments = new ArrayList<CommentTreeNode>();
            List<Comment> result = appEm.createQuery("select c from Comment c where c.content.id = :contentId and c.parent is null order by c.createDateTime desc").setParameter("contentId", contentId).getResultList();
            for(Comment comment:result){
                comments.add(new CommentTreeNode(comment, null, null));
            }
        }
        return comments;
    }

    public void setComments(List<CommentTreeNode> comments) {
        this.comments = comments;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getParentCommentId() {
        return parentCommentId;
    }

    public void setParentCommentId(Long parentCommentId) {
        this.parentCommentId = parentCommentId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }
}
