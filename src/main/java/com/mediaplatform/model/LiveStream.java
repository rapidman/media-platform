package com.mediaplatform.model;

import org.jboss.solder.core.Veto;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: timur
 * Date: 12/31/12
 * Time: 12:19 AM
 */
@Cacheable
@Entity
@Table(name = "live_stream")
@Veto
public class LiveStream extends AbstractEntity{
    private String url;
    private String title;
    private String description;
    private boolean published;

    public LiveStream() {
        super(EntityType.LIVE_STREAM);
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
