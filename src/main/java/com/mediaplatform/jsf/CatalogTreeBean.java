package com.mediaplatform.jsf;

import com.mediaplatform.event.*;
import com.mediaplatform.manager.media.CatalogManager;
import com.mediaplatform.util.ConversationUtils;
import com.mediaplatform.model.Genre;
import org.jboss.seam.faces.context.conversation.Begin;
import org.jboss.seam.faces.context.conversation.End;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Stateful;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.swing.tree.TreeNode;
import java.io.Serializable;
import java.util.*;

/**
 * User: timur
 * Date: 11/25/12
 * Time: 11:51 PM
 */
@Stateful
@SessionScoped
@Named
public class CatalogTreeBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<CatalogTreeNode> rootNodes = new ArrayList<CatalogTreeNode>();
    @Inject
    private CatalogManager catalogManager;
    private TreeNode selectedNode;

    @PostConstruct
    public void init() {
        refreshTree(null);
    }

    @PreDestroy
    public void destroy(){
    }

    private void refreshTree(Set<Long> expandedIds) {
        rootNodes.clear();
        List<Genre> rootGenres = catalogManager.getRootCatalogs();
        for (Genre genre : rootGenres) {
            rootNodes.add(new CatalogTreeNode(genre, null, expandedIds));
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

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeGenreSelect(@Observes SelectGenreEvent selectGenreEvent) {
        Set<Long> expandedIds = selectGenreEvent.getExpandedCatalogIds();
        fillExpandedIds(rootNodes, expandedIds);
        refreshTree(expandedIds);
        selectedNode = getTreeNodeById(selectGenreEvent.getGenreId(), rootNodes);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeCatalogUpdate(@Observes UpdateCatalogEvent updateEvent) {
        Set<Long> expandedIds = new HashSet<Long>();
        fillExpandedIds(rootNodes, expandedIds);
        refreshTree(expandedIds);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeCatalogDelete(@Observes DeleteCatalogEvent deleteEvent) {
        Set<Long> expandedIds = new HashSet<Long>();
        fillExpandedIds(rootNodes, expandedIds);
        expandedIds.remove(deleteEvent.getCatalogId());
        refreshTree(expandedIds);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeContentDelete(@Observes DeleteContentEvent deleteEvent) {
        refreshTree(deleteEvent.getExpandedCatalogIds());
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void observeContentCreate(@Observes CreateContentEvent createEvent) {
        refreshTree(createEvent.getExpandedCatalogIds());
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

    private CatalogTreeNode getTreeNodeById(Long nodeId, List<CatalogTreeNode> nodes) {
        for(CatalogTreeNode node:nodes){
            if(node.getId().equals(nodeId)){
                return node;
            }
            CatalogTreeNode result = getTreeNodeById(nodeId, node.getChildCatalogs());
            if(result != null) return result;
        }
        return null;
    }
}
