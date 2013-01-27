package com.mediaplatform.jsf;

import com.google.common.collect.Iterators;
import com.mediaplatform.model.Comment;

import javax.swing.tree.TreeNode;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;

/**
 * User: timur
 * Date: 1/27/13
 * Time: 11:33 PM
 */
public class CommentTreeNode extends NamedNode implements TreeNode {
    private CommentTreeNode parent;
    private List<CommentTreeNode> children = new ArrayList<CommentTreeNode>();
    private Comment entity;

    public CommentTreeNode(Comment entity, CommentTreeNode parent, Set<Long> expandedIds) {
        this.entity = entity;
        this.setType(entity.getType());
        setId(entity.getId());
        this.parent = parent;
        this.title = entity.getTitle();
        this.description = entity.getDescription();
        if(expandedIds != null && expandedIds.contains(entity.getId())) setExpanded(true);
        for(Comment child: entity.getReplies()){
            children.add(new CommentTreeNode(child, this, expandedIds));
        }
    }

    @Override
    public int getChildrenSize() {
        return 0;
    }

    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);
    }

    public int getChildCount() {
        return children.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Enumeration children() {
        return Iterators.asEnumeration(children.iterator());
    }

    public Comment getEntity() {
        return entity;
    }

    @Override
    public boolean isExpanded() {
        return true;
    }

}
