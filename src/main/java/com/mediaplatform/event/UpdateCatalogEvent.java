package com.mediaplatform.event;

import org.jboss.solder.core.Veto;

/**
 * User: timur
 * Date: 11/28/12
 * Time: 6:30 PM
 */
@Veto
public class UpdateCatalogEvent extends AbstractCatalogEvent{
    public UpdateCatalogEvent(long catalogId) {
        super(catalogId);
    }
}
