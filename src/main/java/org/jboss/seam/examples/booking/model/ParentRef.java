package org.jboss.seam.examples.booking.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/**
 * User: timur
 * Date: 12/1/12
 * Time: 8:38 PM
 */
@Embeddable
public class ParentRef {
    private Long id;
    private EntityType entityType;

    public ParentRef() {
    }

    public ParentRef(Long id, EntityType entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    @Column(name = "parentref_id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Transient
    public String getShortName() {
        return entityType.name().toLowerCase() + "_" + id;
    }
}
