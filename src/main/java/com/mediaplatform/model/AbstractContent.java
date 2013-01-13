package com.mediaplatform.model;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * User: timur
 * Date: 1/14/13
 * Time: 12:31 AM
 */
@MappedSuperclass
public abstract class AbstractContent extends AbstractEntity{
    private String title;
    private String description;
    private User author;
    private Date createDateTime = new Date();

    public AbstractContent(){
        this(null);
    }

    protected AbstractContent(EntityType entityType) {
        super(entityType);
    }

    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Lob
    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    @Boost(3)
    @Type(type="org.hibernate.type.StringClobType")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @ManyToOne
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    @Temporal(TemporalType.TIMESTAMP)
    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }
}
