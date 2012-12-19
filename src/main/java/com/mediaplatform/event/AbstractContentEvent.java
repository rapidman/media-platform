package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 4:58 PM
 */
@Veto
public class AbstractContentEvent {
    private long contentId;

    public AbstractContentEvent(long contentId) {
        this.contentId = contentId;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }
}
