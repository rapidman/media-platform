package com.mediaplatform.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * User: timur
 * Date: 12/1/12
 * Time: 8:03 PM
 */
@MappedSuperclass
public abstract class AbstractEntity implements Serializable{
    private Long id;
    private EntityType entityType;

    public AbstractEntity(){
        this(null);
    }

    protected AbstractEntity(EntityType entityType) {
        this.entityType = entityType;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity that = (AbstractEntity) o;

        if (entityType != that.entityType) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        return result;
    }
}
