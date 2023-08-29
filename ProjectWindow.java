package com.mycompany.hmmm;

/*import com.mycompany.hmmm.JCheckBoxTree;
import com.mycompany.hmmm.AttributesTableModel;
import com.mycompany.hmmm.UniqueAttributesManager;
 */
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

public class ProjectWindow extends JPanel {

    private JCheckBoxTree checkBoxTree;
    private JTextArea displayArea;
    private Groups groupsPanel;
    private AttributesTableModel attributesTableModel;
    private HashMap<DefaultMutableTreeNode, AttributesTableModel> nodeToAttributesTableModel;
    private String selectedNodePath = null;
    private UniqueAttributesManager uniqueAtts = new UniqueAttributesManager();

    public ProjectWindow() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Section 1
        JPanel TreePanel = new JPanel();
        TreePanel.setBackground(new Color(240, 240, 240));
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridx = 1;
        gbc.gridy = 0;
        add(TreePanel, gbc);

        // Section 2
        JPanel editPanel = new JPanel();
        editPanel.setBackground(new Color(180, 200, 220));
        gbc.weightx = 1;
        gbc.weighty = 0.6;
        gbc.gridx = 1;
        gbc.gridy = 1;
        add(editPanel, gbc);

        // Section 3
        JPanel dataPanel = new JPanel();
        dataPanel.setBackground(new Color(225, 225, 220));
        gbc.weightx = 1.7;
        gbc.weighty = 0.5;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        add(dataPanel, gbc);

        JPanel TablePanel = new JPanel();
        dataPanel.setBackground(new Color(225, 225, 220));
        gbc.weightx = 0.5;
        gbc.weighty = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridheight = 2;
        add(TablePanel, gbc);

        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("CARE");
        checkBoxTree = new JCheckBoxTree(rootNode);
        checkBoxTree.setCellRenderer(new CustomTreeCellRenderer());
        // Add the tree to the TreePanel
        TreePanel.setLayout(new BorderLayout());
        TreePanel.add(new JScrollPane(checkBoxTree), BorderLayout.CENTER);

        Font treeFont = checkBoxTree.getFont().deriveFont(Font.BOLD, 16); // Change the size to your desired size
        checkBoxTree.setFont(treeFont);

        // Create the display area in the Data Panel
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        dataPanel.setLayout(new BorderLayout());
        dataPanel.add(new JScrollPane(displayArea), BorderLayout.CENTER);
        // Add table to dataPanel

        Font displayFont = new Font("Arial", Font.BOLD, 24);
        displayArea.setFont(displayFont);

        nodeToAttributesTableModel = new HashMap<>();

        // Add TreeSelectionListener to the JTree
        checkBoxTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                TreePath selectedPath = e.getPath();
                // Inside the TreeSelectionListener valueChanged method
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();

                // Get the name of the selected node, its parent's name, and its grandparent's name
                String nodeName = selectedNode.getUserObject().toString();
                String parentNodeName = (parentNode != null) ? parentNode.getUserObject().toString() : "";
                String grandParentNodeName = "";
                if (parentNode != null && parentNode.getParent() != null) {
                    grandParentNodeName = parentNode.getParent().toString();
                    if (!nodeToAttributesTableModel.containsKey(checkBoxTree.getLastSelectedPathComponent())) {
                    }

                    // Display the information in the Display Panel
                    StringBuilder displayText = new StringBuilder("Selected Node: " + nodeName + "\n");
                    displayText.append("Parent Node: " + parentNodeName + "\n");
                    displayText.append("Grandparent Node: " + grandParentNodeName + "\n");

                    displayArea.setText(displayText.toString());
                } else {
                    attributesTableModel = nodeToAttributesTableModel.get(checkBoxTree.getLastSelectedPathComponent());
                    if (attributesTableModel != null)
                    {//attributesTableModel.displayTable(nodeName);
                    StringBuilder displayText = new StringBuilder("Selected Node: " + nodeName + "\n");
                    displayText.append("Parent Node: " + parentNodeName + "\n");
                    displayText.append("Grandparent Node: " + grandParentNodeName + "\n");
                    System.out.println("NODE HAS SOME ATTS!");

                    displayArea.setText(displayText.toString());
                    }else{
                        System.out.println("NO ATTS");
                }}

            }
        });

        // Add a button to add a new node
        JButton addButton = new JButton("Add Node");
        addButton.addActionListener(e -> {
            // Inside the addButton.addActionListener block
            String nodeName = JOptionPane.showInputDialog(this, "Enter the name for the new node:", "Add Node",
                    JOptionPane.PLAIN_MESSAGE);
            if (nodeName != null && !nodeName.trim().isEmpty()) {
                DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(nodeName);
                DefaultTreeModel model = (DefaultTreeModel) checkBoxTree.getModel();
                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) checkBoxTree.getLastSelectedPathComponent();
                if (selectedNode == null) {
                    selectedNode = rootNode;
                }
                model.insertNodeInto(newNode, selectedNode, selectedNode.getChildCount());

            }

        });

        // Add a button to delete the selected node
        JButton deleteButton = new JButton("Delete Node");
        deleteButton.addActionListener(e -> {
            TreePath selectedPath = checkBoxTree.getSelectionPath();
            if (selectedPath != null) {
                int result = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this node?",
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.YES_OPTION) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
                    if (selectedNode.getParent() != null) {
                        DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
                        DefaultTreeModel model = (DefaultTreeModel) checkBoxTree.getModel();
                        model.removeNodeFromParent(selectedNode);

                        attributesTableModel.clearNodeAttributes(selectedNode.getUserObject().toString()
                        );
                    }
                }
            }
        });

        JButton grpButton = new JButton("Group Nodes");
        grpButton.addActionListener(e -> {
            TreePath[] selectedPaths = checkBoxTree.getSelectionPaths();
            if (selectedPaths != null && selectedPaths.length > 0) {
                List<String> selectedNodes = new ArrayList<>();
                for (TreePath path : selectedPaths) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                    selectedNodes.add(node.getUserObject().toString());
                }
                groupsPanel.addNodes(selectedNodes);
            } else {
                JOptionPane.showMessageDialog(this, "No nodes selected.", "Group Nodes", JOptionPane.WARNING_MESSAGE);
            }
        });
// Add a button to add attributes to the selected node
        JButton addAttributeButton = new JButton("Add Attribute");
        addAttributeButton.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) checkBoxTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                String attributeName = JOptionPane.showInputDialog(this, "Enter the attribute name:", "Add Attribute",
                        JOptionPane.PLAIN_MESSAGE);
                if (attributeName != null && !attributeName.trim().isEmpty()) {
                    String attributeValue = JOptionPane.showInputDialog(this, "Enter the attribute value:", "Add Attribute",
                            JOptionPane.PLAIN_MESSAGE);
                    if (attributeValue != null) {
                        // Check if the selected node is an instance of DefaultMutableTreeNode
                        if (selectedNode instanceof DefaultMutableTreeNode) {

                            attributesTableModel = nodeToAttributesTableModel.get(selectedNode);
                            if (attributesTableModel == null) {
                                attributesTableModel = new AttributesTableModel();
                                nodeToAttributesTableModel.put(selectedNode, attributesTableModel);
                            }

                            // Set the attribute for the selected node
                            checkBoxTree.setAttributeForNode(selectedNode, attributeName, attributeValue);
                            // Update the table model with the new attribute
                            attributesTableModel.setNodeAttributes(selectedNode.getUserObject().toString(), attributeName, attributeValue);
                            // Debug: Print the attributes after adding
                            System.out.println("Added Attribute: " + attributeName + " = " + attributeValue + " to " + selectedNode.getUserObject().toString());
                            uniqueAtts.addUniqueAttributeName(attributeName);
                            // attributesTableModel.displayTable(selectedNode.getUserObject().toString());
                        } else {
                            // Handle the case where the selected node is not an DefaultMutableTreeNode
                            JOptionPane.showMessageDialog(this, "Cannot add attributes to this node.", "Add Attribute", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "No node selected.", "Add Attribute", JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton dispAttributeButton = new JButton("Display Attributes");
        dispAttributeButton.addActionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) checkBoxTree.getLastSelectedPathComponent();
            if (selectedNode != null) {
                attributesTableModel.displayTable(selectedNode.getUserObject().toString());
            } else {
                JOptionPane.showMessageDialog(this, "No node selected.", "Add Attribute", JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton uniqueList = new JButton("Display Unique Attributes") {
            {
                addActionListener(e -> {
                    uniqueAtts.displayUniqueAtts();
                });
            }
        };
        JButton selectAllButton = new JButton("Select All");
        selectAllButton.addActionListener(e -> {
            DefaultMutableTreeNode irootNode = (DefaultMutableTreeNode) checkBoxTree.getModel().getRoot();
            checkBoxTree.selectAllNodes(irootNode);
        });
        JButton selectByAttributeButton = new JButton("Select by Attribute");
        selectByAttributeButton.addActionListener(e -> {
            String attributeName1 = JOptionPane.showInputDialog(this, "Enter the first attribute name:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);
            String attributeValue1 = JOptionPane.showInputDialog(this, "Enter the first attribute value:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);

            String attributeName2 = JOptionPane.showInputDialog(this, "Enter the second attribute name:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);
            String attributeValue2 = JOptionPane.showInputDialog(this, "Enter the second attribute value:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);

            String attributeName3 = JOptionPane.showInputDialog(this, "Enter the third attribute name:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);
            String attributeValue3 = JOptionPane.showInputDialog(this, "Enter the third attribute value:", "Select by Attribute", JOptionPane.PLAIN_MESSAGE);

            DefaultMutableTreeNode orootNode = (DefaultMutableTreeNode) checkBoxTree.getModel().getRoot();
            List<DefaultMutableTreeNode> selectedNodes = checkBoxTree.findNodesWithAttributes(rootNode, nodeToAttributesTableModel, attributeName1, attributeValue1, attributeName2, attributeValue2, attributeName3, attributeValue3);

            TreePath[] pathsToSelect = new TreePath[selectedNodes.size()];
            for (int i = 0; i < selectedNodes.size(); i++) {
                pathsToSelect[i] = new TreePath(selectedNodes.get(i).getPath());
            }
            checkBoxTree.setSelectionPaths(pathsToSelect);
        });

        editPanel.setLayout(new GridLayout(4, 2));
        addButton.setPreferredSize(new Dimension(40, 40));
        editPanel.add(addButton);
        editPanel.add(deleteButton);
        editPanel.add(grpButton);
        editPanel.add(addAttributeButton);
        editPanel.add(dispAttributeButton);
        editPanel.add(uniqueList);
        editPanel.add(selectAllButton);
        editPanel.add(selectByAttributeButton);

        // Initialize the Groups panel
        groupsPanel = new Groups();
        // Add the Groups panel to the right side of the main panel
        gbc.weightx = 0.7;
        gbc.weighty = 1;
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        add(groupsPanel, gbc);
    }
}
