package org.jboss.seam.examples.booking.event;

import org.jboss.solder.core.Veto;

import java.util.Set;

/**
 * User: timur
 * Date: 12/3/12
 * Time: 5:00 PM
 */
@Veto
public class DeleteContentEvent extends AbstractContentEvent {
    private Set<Long> expandedCatalogIds;

    public DeleteContentEvent(long contentId, Set<Long> expandedCatalogIds) {
        super(contentId);
        this.expandedCatalogIds = expandedCatalogIds;
    }

    public Set<Long> getExpandedCatalogIds() {
        return expandedCatalogIds;
    }
}
