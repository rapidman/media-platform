package com.mediaplatform.jsf;

import java.io.Serializable;

/**
 * User: timur
 * Date: 11/25/12
 * Time: 11:55 PM
 */
public abstract class NamedNode implements Serializable {
    protected String type;
    protected String title;
    protected String description;
    private boolean expanded;
    private Long id;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return this.title;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public abstract int getChildrenSize();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NamedNode namedNode = (NamedNode) o;

        if (id != null ? !id.equals(namedNode.id) : namedNode.id != null) return false;
        if (type != null ? !type.equals(namedNode.type) : namedNode.type != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
