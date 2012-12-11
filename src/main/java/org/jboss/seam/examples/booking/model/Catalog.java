package org.jboss.seam.examples.booking.model;

import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 2:28 PM
 */
@Entity
@Table(name = "catalog")
@Veto
public class Catalog extends AbstractEntity {
    private String title;
    private String description;
    private Catalog parent;
    private List<Catalog> children;
    private List<Content> contentList;
    private FileEntry icon;

    public Catalog() {
        super(EntityType.CATALOG);
    }

    public Catalog(String title, String description) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ManyToOne
    public Catalog getParent() {
        return parent;
    }

    public void setParent(Catalog parent) {
        this.parent = parent;
    }

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    public List<Catalog> getChildren() {
        if (children == null) {
            children = new ArrayList<Catalog>();
        }
        return children;
    }

    public void setChildren(List<Catalog> children) {
        this.children = children;
    }

    @OneToMany(mappedBy = "catalog", cascade = CascadeType.ALL)
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
