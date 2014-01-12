package com.mediaplatform.model;

import org.hibernate.annotations.CacheConcurrencyStrategy;
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
@Indexed
@NamedQueries(
        {@NamedQuery
                (name = "UsersContent.findAllExceptSelectedContent",
                 query = "select c from Content c where c.author.id= :userId and c.id <> :currentContentId order by c.rate desc",
                 hints = {
                         @QueryHint(name ="org.hibernate.cacheable", value="true"),
                         @QueryHint(name = "org.hibernate.cacheRegion", value = "local-query")
                 }
                ),

        @NamedQuery
               (name = "Content.findPopular",
                query = "select c from Content c where c.moderationStatus= :moderationStatus  order by c.rate desc",
                hints = {
                        @QueryHint(name ="org.hibernate.cacheable", value="true"),
                        @QueryHint(name = "org.hibernate.cacheRegion", value = "local-query")
                }
               ),
        @NamedQuery
                (name = "Content.findLatest",
                        query = "select c from Content c where c.moderationStatus= :moderationStatus  order by c.createDateTime desc",
                        hints = {
                                @QueryHint(name ="org.hibernate.cacheable", value="true"),
                                @QueryHint(name = "org.hibernate.cacheRegion", value = "local-query")
                        }
                )
        }
)
public class Content extends AbstractContent{
    private Genre genre;
    private FileEntry mediaFile;
    private FileEntry cover;
    private ModerationStatus moderationStatus = ModerationStatus.WAITING_FOR_MODERATION;
    private List<Comment> comments;
    private int viewCount;
    private int rate;
    private List<RateInfo> contentRates;
    private String moderationReason;
    private String shortDescription;

    public Content(){
        super(EntityType.CONTENT);
    }

    public Content(String title, String description, Genre genre) {
        this();
        setTitle(title);
        setDescription(description);
        this.genre = genre;
    }

    @Lob
    @Field(index= Index.YES, analyze= Analyze.YES, store= Store.NO)
    @Boost(3)
    @Type(type="org.hibernate.type.StringClobType")
    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
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

    @Transient
    public int getModerationStatusAsInt(){
        return getModerationStatus().ordinal();
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

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public void incViewCount(){
        viewCount++;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    @CollectionTable(name = "content_rates", joinColumns = {@JoinColumn(name="rated_obj_id", referencedColumnName = "id"), @JoinColumn(name="rated_obj_type", referencedColumnName = "entity_type")})
    @ElementCollection(targetClass = RateInfo.class)
    public List<RateInfo> getContentRates() {
        if(contentRates == null){
            contentRates = new ArrayList<RateInfo>();
        }
        return contentRates;
    }

    public void setContentRates(List<RateInfo> contentRates) {
        this.contentRates = contentRates;
    }

    public String getModerationReason() {
        return moderationReason;
    }

    public void setModerationReason(String moderationReason) {
        this.moderationReason = moderationReason;
    }

    public void addRate(boolean up){
        if(up){
            rate++;
        }else{
            rate--;
        }
    }
}
