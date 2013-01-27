package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 12:26 AM
 */

@Cacheable
@Entity
@Table(name = "comment")
@Veto
public class Comment extends AbstractContent{
    private Content content;
    private List<Comment> replies;
    private Comment parent;
    private Boolean deleted;

    public Comment() {
        super(EntityType.COMMENT);
    }

    @NotNull
    @ManyToOne
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @OneToMany(mappedBy = "parent")
    public List<Comment> getReplies() {
        if(replies == null){
            replies = new ArrayList<Comment>();
        }
        return replies;
    }

    public void setReplies(List<Comment> replies) {
        this.replies = replies;
    }

    @ManyToOne
    public Comment getParent() {
        return parent;
    }

    public void setParent(Comment parent) {
        this.parent = parent;
    }

    @Transient
    public String getType(){
        return parent == null ? "root" : "reply";
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
