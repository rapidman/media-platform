package com.mediaplatform.model;

import org.hibernate.annotations.Type;
import org.hibernate.search.annotations.*;
import org.jboss.solder.core.Veto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: timur
 * Date: 11/26/12
 * Time: 2:28 PM
 */
@Cacheable
@Entity
@Table(name = "content")
@Veto
@Indexed
public class Content extends AbstractContent{
    private Genre genre;
    private FileEntry mediaFile;
    private FileEntry cover;
    private ModerationStatus moderationStatus = ModerationStatus.WAITING_FOR_MODERATION;
    private List<Comment> comments;

    public Content(){
        super(EntityType.CONTENT);
    }

    public Content(String title, String description, Genre genre) {
        this();
        setTitle(title);
        setDescription(description);
        this.genre = genre;
    }

    @NotNull
    @ManyToOne
    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
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

    @Enumerated(EnumType.ORDINAL)
    public ModerationStatus getModerationStatus() {
        return moderationStatus;
    }

    public void setModerationStatus(ModerationStatus moderationStatus) {
        this.moderationStatus = moderationStatus;
    }

    @OneToMany(mappedBy = "content")
    public List<Comment> getComments() {
        if(comments == null){
            comments = new ArrayList<Comment>();
        }
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }
}
