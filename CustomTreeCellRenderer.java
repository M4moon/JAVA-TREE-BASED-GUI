package com.mycompany.hmmm;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

class CustomTreeCellRenderer extends DefaultTreeCellRenderer {
    private ImageIcon customIcon = new ImageIcon("CARElogo.png");

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean selected, boolean expanded,
                                                  boolean leaf, int row, boolean hasFocus) {
        Component component = super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

        if (value instanceof DefaultMutableTreeNode) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object userObject = node.getUserObject();
            // Here you can check the userObject and set different icons based on your conditions
            // For example:
            if (userObject.equals("CARE")) {
                ImageIcon scaledIcon = getScaledIcon(customIcon, 24, 24); // Specify the desired width and height
                setIcon(scaledIcon);
            } else {
                // Set default icon for other nodes
                setIcon(null);
            }
        }

        return component;
    }

    private ImageIcon getScaledIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage();
        Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}