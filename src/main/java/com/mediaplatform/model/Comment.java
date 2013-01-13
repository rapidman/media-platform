package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    public Comment() {
        super(EntityType.COMMENT);
    }

    @ManyToOne
    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }
}
