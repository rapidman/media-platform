package org.jboss.seam.examples.booking.jsf;

import org.jboss.seam.examples.booking.booking.CatalogManager;
import org.jboss.seam.examples.booking.booking.ConversationUtils;
import org.jboss.seam.examples.booking.event.DeleteCatalogEvent;
import org.jboss.seam.examples.booking.event.DeleteContentEvent;
import org.jboss.seam.examples.booking.event.UpdateCatalogEvent;
import org.jboss.seam.examples.booking.model.Catalog;
import org.jboss.seam.faces.context.conversation.Begin;
import org.jboss.seam.faces.context.conversation.End;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: timur
 * Date: 11/25/12
 * Time: 11:51 PM
 */
@Stateful
@ConversationScoped
@Named
public class CatalogTreeBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CatalogTreeNode> rootNodes = new ArrayList<CatalogTreeNode>();
    @Inject
    private CatalogManager catalogManager;
    private TreeNode selectedNode;
    @Inject
    private Conversation conversation;

    @PostConstruct
    @Begin
    public void init() {
        refreshTree(null);
    }

    @PreDestroy
    @End
    public void destroy(){
    }

    private void refreshTree(Set<Long> expandedIds) {
        ConversationUtils.safeBegin(conversation);
        rootNodes.clear();
        List<Catalog> rootCatalogs = catalogManager.getRootCatalogs();
        for (Catalog catalog : rootCatalogs) {
            rootNodes.add(new CatalogTreeNode(catalog, null, expandedIds));
        }
    }

    public List<CatalogTreeNode> getRootNodes() {
        return rootNodes;
    }

    public void setRootNodes(List<CatalogTreeNode> rootNodes) {
        this.rootNodes = rootNodes;
    }

    public void changeSelection(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public boolean isSelected(TreeNode other) {
        return selectedNode != null && selectedNode.equals(other);
    }

    public void observeCatalogUpdate(@Observes UpdateCatalogEvent updateEvent) {
        Set<Long> expandedIds = new HashSet<Long>();
        fillExpandedIds(rootNodes, expandedIds);
        refreshTree(expandedIds);
    }

    public void observeCatalogDelete(@Observes DeleteCatalogEvent deleteEvent) {
        Set<Long> expandedIds = new HashSet<Long>();
        fillExpandedIds(rootNodes, expandedIds);
        expandedIds.remove(deleteEvent.getCatalogId());
        refreshTree(expandedIds);
    }

    public void observeContentDelete(@Observes DeleteContentEvent deleteEvent) {
        refreshTree(deleteEvent.getExpandedCatalogIds());
    }

    public void fillExpandedIds(List<CatalogTreeNode> nodes, Set<Long> expanded){
        for (TreeNode treeNode:nodes){
            CatalogTreeNode node = (CatalogTreeNode) treeNode;
            if(node.isExpanded()){
                expanded.add(node.getId());
                fillExpandedIds(node.getChildCatalogs(), expanded);
            }
        }

    }
}
