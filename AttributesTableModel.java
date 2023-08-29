package com.mycompany.hmmm;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class AttributesTableModel extends AbstractTableModel {

    private Map<String, List<String>> nodeAttributes = new HashMap<>();
    private List<String> attributeNames = new ArrayList<>();
    private List<String> attributeValues = new ArrayList<>();

    

    public void setNodeAttributes(String nodeName, String attributeName, String attributeValue) {
        if (!nodeAttributes.containsKey(nodeName)) {
            nodeAttributes.put(nodeName, new ArrayList<>());
        }
        nodeAttributes.get(nodeName).add(attributeName);
        attributeNames.add(attributeName);
        attributeValues.add(attributeValue);
        fireTableDataChanged();
        System.out.println("Added Value");
        UniqueAttributesManager uniqueAttributesManager = UniqueAttributesManager.getInstance();
        uniqueAttributesManager.addUniqueAttributeName(attributeName);
    }

    public void clearNodeAttributes(String nodeName) {
        nodeAttributes.remove(nodeName);
        attributeNames.clear();
        attributeValues.clear();
        fireTableDataChanged();
    }

    public boolean hasAttribute(String attributeName, String attributeValue) {
        for (int i = 0; i < attributeNames.size(); i++) {
            if (attributeNames.get(i).equals(attributeName) && attributeValues.get(i).equals(attributeValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRowCount() {
        return attributeNames.size();
    }

    @Override
    public int getColumnCount() {
        return 2; // Two columns: Attribute Name and Attribute Value
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return attributeNames.get(rowIndex);
        } else if (columnIndex == 1) {
            return attributeValues.get(rowIndex);
        }
        return null;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Attribute Name";
        } else if (column == 1) {
            return "Attribute Value";
        }
        return super.getColumnName(column);
    }

    public void displayTable(String nodeName) {
        if (!nodeAttributes.containsKey(nodeName)) {
            System.out.println("NO Attributes Exist");
            JOptionPane.showMessageDialog(null, "NO ATTRIBUTES ADDED!.", "ERROR", JOptionPane.WARNING_MESSAGE);

        }
        JFrame tableFrame = new JFrame(nodeName + " Attributes ");
        tableFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        tableFrame.setSize(400, 400);
        JTable table = new JTable(this);
        JScrollPane scrollPane = new JScrollPane(table);
        tableFrame.add(scrollPane);
        tableFrame.setVisible(true);

    }
    public String[][] getTableAsMatrix() {
        String[][] tableMatrix = new String[attributeNames.size()][2];
        for (int i = 0; i < attributeNames.size(); i++) {
            tableMatrix[i][0] = attributeNames.get(i);
            tableMatrix[i][1] = attributeValues.get(i);
        }
        return tableMatrix;
    }

}
