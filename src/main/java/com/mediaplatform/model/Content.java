package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 2:28 PM
 */
@Cacheable
@Entity
@Table(name = "content")
@Veto
public class Content extends AbstractEntity{
    private String title;
    private String description;
    private Catalog catalog;
    private FileEntry mediaFile;
    private FileEntry cover;

    public Content(){
        super(EntityType.CONTENT);
    }

    public Content(String title, String description, Catalog catalog) {
        this();
        this.title = title;
        this.description = description;
        this.catalog = catalog;
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

    @NotNull
    @ManyToOne
    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public FileEntry getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(FileEntry mediaFile) {
        this.mediaFile = mediaFile;
    }

    @OneToOne(fetch = FetchType.EAGER)
    public FileEntry getCover() {
        return cover;
    }

    public void setCover(FileEntry cover) {
        this.cover = cover;
    }
}
