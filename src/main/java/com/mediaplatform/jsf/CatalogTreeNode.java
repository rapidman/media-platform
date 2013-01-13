package com.mediaplatform.jsf;

import com.google.common.collect.Iterators;
import com.mediaplatform.model.Genre;
import com.mediaplatform.model.FileEntry;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * User: timur
 * Date: 11/25/12
 * Time: 11:52 PM
 */
public class CatalogTreeNode extends NamedNode implements TreeNode {
    private List<CatalogTreeNode> childCatalogs = new ArrayList<CatalogTreeNode>();
    protected int childrenSize = 0;
    private FileEntry icon;

    private CatalogTreeNode parent;

    public CatalogTreeNode(Genre genre, CatalogTreeNode parent, Set<Long> expandedIds) {
        this.setType("catalog");
        setId(genre.getId());
        this.parent = parent;
        this.title = genre.getTitle();
        this.description = genre.getDescription();
        if(expandedIds != null && expandedIds.contains(genre.getId())) setExpanded(true);
        for(Genre child: genre.getChildren()){
            childCatalogs.add(new CatalogTreeNode(child, this, expandedIds));
        }
        childrenSize+= genre.getContentList().size();
        if(parent != null){
            parent.addChildrenSize(childrenSize);
        }
        this.icon = genre.getIcon();
    }

    private void addChildrenSize(int childrenSize) {
        this.childrenSize+=childrenSize;
        if(parent != null){
            parent.addChildrenSize(childrenSize);
        }
    }

    public TreeNode getChildAt(int childIndex) {
        return childCatalogs.get(childIndex);
    }

    public int getChildCount() {
        return childCatalogs.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return childCatalogs.indexOf(node);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return childCatalogs.isEmpty();
    }

    public Enumeration children() {
        return Iterators.asEnumeration(childCatalogs.iterator());
    }

    @Override
    public boolean isExpanded() {
        return !isLeaf() && super.isExpanded();
    }

    @Override
    public int getChildrenSize() {
        return childrenSize;
    }

    public List<CatalogTreeNode> getChildCatalogs() {
        return childCatalogs;
    }

    public FileEntry getIcon() {
        return icon;
    }
}
