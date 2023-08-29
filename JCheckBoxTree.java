/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.mycompany.hmmm;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JCheckBoxTree extends JTree {

    private static final long serialVersionUID = -4194122328392241790L;
    private Hashtable<String, String> attributes;

    JCheckBoxTree selfPointer = this;

    public JCheckBoxTree(DefaultMutableTreeNode rootNode) {
        super(rootNode);
        // Initialize the tree with the given root node
        this.setModel(new DefaultTreeModel(rootNode));
        attributes = new Hashtable<>();

    }

    // Defining data structure that will enable to fast check-indicate the state of each node
    // It totally replaces the "selection" mechanism of the JTree
    private class CheckedNode {

        boolean isSelected;
        boolean hasChildren;
        boolean allChildrenSelected;

        public CheckedNode(boolean isSelected_, boolean hasChildren_, boolean allChildrenSelected_) {
            isSelected = isSelected_;
            hasChildren = hasChildren_;
            allChildrenSelected = allChildrenSelected_;
        }
    }
    HashMap<TreePath, CheckedNode> nodesCheckingState;
    HashSet<TreePath> checkedPaths = new HashSet<TreePath>();

    // Defining a new event type for the checking mechanism and preparing event-handling mechanism
    protected EventListenerList listenerList = new EventListenerList();

    public class CheckChangeEvent extends EventObject {

        private static final long serialVersionUID = -8100230309044193368L;

        public CheckChangeEvent(Object source) {
            super(source);
        }
    }

    public interface CheckChangeEventListener extends EventListener {

        public void checkStateChanged(CheckChangeEvent event);
    }

    public void addCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerList.add(CheckChangeEventListener.class, listener);
    }

    public void removeCheckChangeEventListener(CheckChangeEventListener listener) {
        listenerList.remove(CheckChangeEventListener.class, listener);
    }

    void fireCheckChangeEvent(CheckChangeEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == CheckChangeEventListener.class) {
                ((CheckChangeEventListener) listeners[i + 1]).checkStateChanged(evt);
            }
        }
    }

    // Override
    public void setModel(TreeModel newModel) {
        super.setModel(newModel);
        resetCheckingState();
    }

    // New method that returns only the checked paths (totally ignores original "selection" mechanism)
    public TreePath[] getCheckedPaths() {
        return checkedPaths.toArray(new TreePath[checkedPaths.size()]);
    }

    // Returns true in case that the node is selected, has children but not all of them are selected
    public boolean isSelectedPartially(TreePath path) {
        CheckedNode cn = nodesCheckingState.get(path);
        return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
    }

    private void resetCheckingState() {
        nodesCheckingState = new HashMap<TreePath, CheckedNode>();
        checkedPaths = new HashSet<TreePath>();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) getModel().getRoot();
        if (node == null) {
            return;
        }
        addSubtreeToCheckingStateTracking(node);
    }

    // Creating data structure of the current model for the checking mechanism
    private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
        TreeNode[] path = node.getPath();
        TreePath tp = new TreePath(path);
        CheckedNode cn = new CheckedNode(false, node.getChildCount() > 0, false);
        nodesCheckingState.put(tp, cn);
        for (int i = 0; i < node.getChildCount(); i++) {
            addSubtreeToCheckingStateTracking((DefaultMutableTreeNode) tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
        }
    }

    // Overriding cell renderer by a class that ignores the original "selection" mechanism
    // It decides how to show the nodes due to the checking-mechanism
    private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {

        private static final long serialVersionUID = -7341833835878991719L;
        JCheckBox checkBox;

        public CheckBoxCellRenderer() {
            super();
            this.setLayout(new BorderLayout());
            checkBox = new JCheckBox();
            add(checkBox, BorderLayout.CENTER);
            setOpaque(false);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            TreePath tp = new TreePath(node.getPath());
            CheckedNode cn = nodesCheckingState.get(tp);
            if (cn == null) {
                return this;
            }
            checkBox.setSelected(cn.isSelected);
            checkBox.setText(obj.toString());
            checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
            return this;
        }
    }

    public JCheckBoxTree() {
        super();
        // Disabling toggling by double-click
        this.setToggleClickCount(0);
        // Overriding cell renderer by new one defined above
        CheckBoxCellRenderer cellRenderer = new CheckBoxCellRenderer();
        this.setCellRenderer(cellRenderer);

        // Overriding selection model by an empty one
        DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
            private static final long serialVersionUID = -8190634240451667286L;

            // Totally disabling the selection mechanism
            public void setSelectionPath(TreePath path) {
            }

            public void addSelectionPath(TreePath path) {
            }

            public void removeSelectionPath(TreePath path) {
            }

            public void setSelectionPaths(TreePath[] pPaths) {
            }
        };
        // Calling checking mechanism on mouse click
        this.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent arg0) {
                TreePath tp = selfPointer.getPathForLocation(arg0.getX(), arg0.getY());
                if (tp == null) {
                    return;
                }
                boolean checkMode = !nodesCheckingState.get(tp).isSelected;
                checkSubTree(tp, checkMode);
                updatePredecessorsWithCheckMode(tp, checkMode);
                // Firing the check change event
                fireCheckChangeEvent(new CheckChangeEvent(new Object()));
                // Repainting tree after the data structures were updated
                selfPointer.repaint();
            }

            public void mouseEntered(MouseEvent arg0) {
            }

            public void mouseExited(MouseEvent arg0) {
            }

            public void mousePressed(MouseEvent arg0) {
            }

            public void mouseReleased(MouseEvent arg0) {
            }
        });
        this.setSelectionModel(dtsm);
    }

    // When a node is checked/unchecked, updating the states of the predecessors
    protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
        TreePath parentPath = tp.getParentPath();
        // If it is the root, stop the recursive calls and return
        if (parentPath == null) {
            return;
        }
        CheckedNode parentCheckedNode = nodesCheckingState.get(parentPath);
        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
        parentCheckedNode.allChildrenSelected = true;
        parentCheckedNode.isSelected = false;
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            CheckedNode childCheckedNode = nodesCheckingState.get(childPath);
            // It is enough that even one subtree is not fully selected
            // to determine that the parent is not fully selected
            if (!childCheckedNode.allChildrenSelected) {
                parentCheckedNode.allChildrenSelected = false;
            }
            // If at least one child is selected, selecting also the parent
            if (childCheckedNode.isSelected) {
                parentCheckedNode.isSelected = true;
            }
        }
        if (parentCheckedNode.isSelected) {
            checkedPaths.add(parentPath);
        } else {
            checkedPaths.remove(parentPath);
        }
        // Go to upper predecessor
        updatePredecessorsWithCheckMode(parentPath, check);
    }

    // Recursively checks/unchecks a subtree
    protected void checkSubTree(TreePath tp, boolean check) {
        CheckedNode cn = nodesCheckingState.get(tp);
        cn.isSelected = check;
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tp.getLastPathComponent();
        for (int i = 0; i < node.getChildCount(); i++) {
            checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
        }
        cn.allChildrenSelected = check;
        if (check) {
            checkedPaths.add(tp);
        } else {
            checkedPaths.remove(tp);
        }
    }

    public void setAttributeForNode(DefaultMutableTreeNode node, String name, String value) {
        attributes.put(getPathString(node), name + ": " + value);
    }

    // Gets the attribute of the given node
    public String getAttributeForNode(DefaultMutableTreeNode node, String name) {
        return attributes.get(getPathString(node));
    }

    // Gets all attributes of the given node
    public Hashtable<String, String> getAllAttributesForNode(DefaultMutableTreeNode node) {
        Hashtable<String, String> nodeAttributes = new Hashtable<>();
        for (String key : attributes.keySet()) {
            if (key.startsWith(getPathString(node))) {
                nodeAttributes.put(key, attributes.get(key));
            }
        }
        return nodeAttributes;
    }

    // Helper method to convert TreePath to a string representation
    private String getPathString(DefaultMutableTreeNode node) {
        if (node == null) {
            return null;
        }

        TreePath path = new TreePath(((DefaultTreeModel) getModel()).getPathToRoot(node));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.getPathCount(); i++) {
            if (i > 0) {
                sb.append("/");
            }
            sb.append(path.getPathComponent(i).toString());
        }
        return sb.toString();
    }

    public void selectAllNodes(DefaultMutableTreeNode node) {
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            addSelectionPath(path);
            for (int i = 0; i < node.getChildCount(); i++) {
                selectAllNodes((DefaultMutableTreeNode) node.getChildAt(i));
            }
        }
    }

    public List<DefaultMutableTreeNode> findNodesWithAttributes(DefaultMutableTreeNode node,HashMap<DefaultMutableTreeNode, AttributesTableModel> nodeToAttributesTableModel, String attributeName1, String attributeValue1, String attributeName2, String attributeValue2, String attributeName3, String attributeValue3) {
        List<DefaultMutableTreeNode> resultNodes = new ArrayList<>();

        if (node != null) {
             AttributesTableModel attributesTableModel = nodeToAttributesTableModel.get(node);
             System.out.println(node.getUserObject().toString());
            if (attributesTableModel != null
                    && (attributesTableModel.hasAttribute(attributeName1, attributeValue1)
                    || attributesTableModel.hasAttribute(attributeName2, attributeValue2)
                    || attributesTableModel.hasAttribute(attributeName3, attributeValue3))) {
                resultNodes.add(node);
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                resultNodes.addAll(findNodesWithAttributes((DefaultMutableTreeNode) node.getChildAt(i),nodeToAttributesTableModel, attributeName1, attributeValue1, attributeName2, attributeValue2, attributeName3, attributeValue3));
            }
        }

        return resultNodes;
    } 

}
