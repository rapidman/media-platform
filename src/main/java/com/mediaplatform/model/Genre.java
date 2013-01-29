package com.mediaplatform.model;

import org.hibernate.annotations.Type;
import org.jboss.solder.core.Veto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 2:28 PM
 */
@Cacheable
@Entity
@Table(name = "genre")
@Veto
public class Genre extends AbstractEntity implements HtmlContainer{
    private String title;
    private String description;
    private Genre parent;
    private List<Genre> children;
    private List<Content> contentList;
    private FileEntry icon;

    public Genre() {
        super(EntityType.GENRE);
    }

    public Genre(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Type(type="org.hibernate.type.StringClobType")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public Genre getParent() {
        return parent;
    }

    public void setParent(Genre parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public List<Genre> getChildren() {
        if (children == null) {
            children = new ArrayList<Genre>();
        }
        return children;
    }

    public void setChildren(List<Genre> children) {
        this.children = children;
    }

    @OneToMany(mappedBy = "genre", cascade = CascadeType.ALL)
    public List<Content> getContentList() {
        if (contentList == null) {
            contentList = new ArrayList<Content>();
        }
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public FileEntry getIcon() {
        return icon;
    }

    public void setIcon(FileEntry icon) {
        this.icon = icon;
    }
}
