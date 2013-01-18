package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

import java.util.Set;

/**
 * User: timur
 * Date: 1/6/13
 * Time: 1:36 PM
 */
@Veto
public class SelectGenreEvent {
    private long genreId;
    private Set<Long> expandedCatalogIds;

    public SelectGenreEvent(long genreId, Set<Long> expandedCatalogIds) {
        this.genreId = genreId;
        this.expandedCatalogIds = expandedCatalogIds;
    }

    public Set<Long> getExpandedCatalogIds() {
        return expandedCatalogIds;
    }

    public long getGenreId() {
        return genreId;
    }
}
