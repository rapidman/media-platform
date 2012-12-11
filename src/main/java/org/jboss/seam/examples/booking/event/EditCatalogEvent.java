package org.jboss.seam.examples.booking.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 11/28/12
 * Time: 8:03 PM
 */
@Veto
public class EditCatalogEvent extends AbstractCatalogEvent {
    public EditCatalogEvent(long catalogId) {
        super(catalogId);
    }
}
