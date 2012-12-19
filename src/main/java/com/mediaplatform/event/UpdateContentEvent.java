package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 4:58 PM
 */
@Veto
public class UpdateContentEvent extends AbstractContentEvent {
    public UpdateContentEvent(long contentId) {
        super(contentId);
    }
}
