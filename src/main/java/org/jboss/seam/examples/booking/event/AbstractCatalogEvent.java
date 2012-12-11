package org.jboss.seam.examples.booking.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 11/28/12
 * Time: 8:02 PM
 */
@Veto
public class AbstractCatalogEvent {
    private long catalogId;

    public AbstractCatalogEvent(long catalogId) {
        this.catalogId = catalogId;
    }

    public long getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(long catalogId) {
        this.catalogId = catalogId;
    }

}
