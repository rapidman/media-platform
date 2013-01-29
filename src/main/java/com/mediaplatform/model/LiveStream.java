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
public class LiveStream extends AbstractContent{
    private String source;
    private boolean published;

    public LiveStream(String source, String title, String description, boolean published) {
        this();
        this.source = source;
        setTitle(title);
        setDescription(description);
        this.published = published;
    }

    public LiveStream() {
        super(EntityType.LIVE_STREAM);
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
