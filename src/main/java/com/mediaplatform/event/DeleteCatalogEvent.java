package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 11/28/12
 * Time: 8:03 PM
 */
@Veto
public class DeleteCatalogEvent extends AbstractCatalogEvent {
    public DeleteCatalogEvent(long catalogId) {
        super(catalogId);
    }
}
