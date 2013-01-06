package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

import java.util.Set;

/**
 * User: timur
 * Date: 1/6/13
 * Time: 1:36 PM
 */
@Veto
public class CreateContentEvent extends UpdateContentEvent {
    private Set<Long> expandedCatalogIds;

    public CreateContentEvent(long contentId, Set<Long> expandedCatalogIds) {
        super(contentId);
        this.expandedCatalogIds = expandedCatalogIds;
    }

    public Set<Long> getExpandedCatalogIds() {
        return expandedCatalogIds;
    }
}
