package com.mycompany.hmmm;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.tree.DefaultTreeModel;

public class Groups extends JPanel {

    private Map<String, List<String>> groupsMap; // To store groups and their nodes
    private DefaultMutableTreeNode root; // Root node for the tree
    private JTree groupsTree; // JTree to display the groups and nodes
    private JLabel noGroupsLabel; // Label to display "No groups yet" message

    public Groups() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        groupsMap = new HashMap<>();
        root = new DefaultMutableTreeNode("Groups");
        groupsTree = new JTree(root);
        noGroupsLabel = new JLabel("No groups yet");
        noGroupsLabel.setHorizontalAlignment(JLabel.CENTER);
        noGroupsLabel.setVerticalAlignment(JLabel.CENTER);

        JScrollPane scrollPane = new JScrollPane(groupsTree);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void addNodes(List<String> nodes) {
        String groupName = JOptionPane.showInputDialog(this, "Enter the name for the new group:", "Add Group",
                JOptionPane.PLAIN_MESSAGE);
        if (groupName != null && !groupName.trim().isEmpty()) {
            DefaultMutableTreeNode groupNode = new DefaultMutableTreeNode(groupName);
            root.add(groupNode);

            List<String> nodeList = new ArrayList<>(nodes);
            groupsMap.put(groupName, nodeList);
            for (String node : nodeList) {
                groupNode.add(new DefaultMutableTreeNode(node));
            }

            // Update the tree to reflect the changes
            ((DefaultTreeModel) groupsTree.getModel()).reload();

            // Remove the "No groups yet" label if a group is added
            if (noGroupsLabel.getParent() != null) {
                remove(noGroupsLabel);
            }
        }
    }

    public boolean hasGroups() {
        return !groupsMap.isEmpty();
    }
}