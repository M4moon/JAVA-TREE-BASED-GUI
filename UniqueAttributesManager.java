package com.mycompany.hmmm;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class UniqueAttributesManager {

    public static UniqueAttributesManager instance;
    private List<String> uniqueAttributeNames;

    UniqueAttributesManager() {
        uniqueAttributeNames = new ArrayList<>();
    }

    public static synchronized UniqueAttributesManager getInstance() {
        if (instance == null) {
            instance = new UniqueAttributesManager();
        }
        return instance;
    }

    public List<String> getUniqueAttributeNames() {
        return uniqueAttributeNames;
    }

    public void addUniqueAttributeName(String attributeName) {
        if (!uniqueAttributeNames.contains(attributeName)) {
            uniqueAttributeNames.add(attributeName);
            System.out.println("Unique Value Added");
        } else {
            System.out.println("Att Exists");
        }
    }

    public void displayUniqueAtts() {
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Attributes");

        // Populate the table model with data from the list
        for (String attribute : uniqueAttributeNames) {
            tableModel.addRow(new Object[]{attribute});
        }

        // Create a JTable using the table model
        JTable table = new JTable(tableModel);

        // Create a JScrollPane to add the table to
        JScrollPane scrollPane = new JScrollPane(table);

        // Create a JFrame to display the table
        JFrame frame = new JFrame("Unique Attributes");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.add(scrollPane);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }
}
