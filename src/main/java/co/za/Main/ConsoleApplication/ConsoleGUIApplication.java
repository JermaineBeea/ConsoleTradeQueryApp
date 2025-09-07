package co.za.Main.ConsoleApplication;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.SQLException;

public class ConsoleGUIApplication {
    
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private ConsoleDatabase db;
    private JTextField spreadField, rateKAField, ratePNField;
    private JCheckBox marketRateCheckbox;
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ConsoleGUIApplication().createAndShowGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    public void createAndShowGUI() {
        // Initialize database
        BigDecimal spread = new BigDecimal("0.01");
        BigDecimal rateKA = new BigDecimal("17.7055");
        BigDecimal ratePN = new BigDecimal("1.0");
        
        db = new ConsoleDatabase(spread, rateKA, ratePN);
        
        // Create main frame
        frame = new JFrame("Trade Console Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        
        // Create top panel with controls
        JPanel controlPanel = createControlPanel();
        frame.add(controlPanel, BorderLayout.NORTH);
        
        // Create table
        createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Create bottom panel with buttons
        JPanel buttonPanel = createButtonPanel();
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        // Load initial data
        loadDataIntoTable();
        
        // Show frame
        frame.setSize(800, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Trading Parameters"));
        
        // Spread input
        panel.add(new JLabel("Spread:"));
        spreadField = new JTextField("0.01", 8);
        panel.add(spreadField);
        
        // Rate KA input
        panel.add(new JLabel("Rate KA:"));
        rateKAField = new JTextField("17.7055", 8);
        panel.add(rateKAField);
        
        // Rate PN input
        panel.add(new JLabel("Rate PN:"));
        ratePNField = new JTextField("1.0", 8);
        panel.add(ratePNField);
        
        // Market rate checkbox
        marketRateCheckbox = new JCheckBox("Based on Market Rate");
        panel.add(marketRateCheckbox);
        
        return panel;
    }
    
    private void createTable() {
        // Define column names
        String[] columnNames = {"Variable", "Minimum", "Maximum", "Return Min", "Return Max"};
        
        // Create table model
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow editing of Minimum and Maximum columns (columns 1 and 2)
                return column == 1 || column == 2;
            }
        };
        
        // Create table
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(25);
        
        // Add listener for cell edits
        table.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 1 || col == 2) { // Minimum or Maximum column
                    updateDatabaseFromTable(row, col);
                }
            }
        });
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(120); // Variable
        table.getColumnModel().getColumn(1).setPreferredWidth(120); // Minimum
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Maximum
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Return Min
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Return Max
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        
        // Run Calculations button
        JButton calculateButton = new JButton("Run Calculations");
        calculateButton.addActionListener(e -> runCalculations());
        panel.add(calculateButton);
        
        // Load Examples button
        JButton examplesButton = new JButton("Load Examples");
        examplesButton.addActionListener(e -> loadExampleData());
        panel.add(examplesButton);
        
        // Reset button
        JButton resetButton = new JButton("Reset to Zero");
        resetButton.addActionListener(e -> resetData());
        panel.add(resetButton);
        
        // Export button
        JButton exportButton = new JButton("Export Data");
        exportButton.addActionListener(e -> exportData());
        panel.add(exportButton);
        
        return panel;
    }
    
    private void loadDataIntoTable() {
        try {
            // Clear existing rows
            tableModel.setRowCount(0);
            
            String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "buyvariable", "sellvariable"};
            
            for (String variable : variables) {
                BigDecimal min = db.getValueFromColumn(variable, "minimum");
                BigDecimal max = db.getValueFromColumn(variable, "maximum");
                BigDecimal returnMin = db.getValueFromColumn(variable, "returnmin");
                BigDecimal returnMax = db.getValueFromColumn(variable, "returnmax");
                
                Object[] row = {
                    variable,
                    min.toPlainString(),
                    max.toPlainString(),
                    returnMin.toPlainString(),
                    returnMax.toPlainString()
                };
                
                tableModel.addRow(row);
            }
        } catch (Exception e) {
            showError("Error loading data: " + e.getMessage());
        }
    }
    
    private void updateDatabaseFromTable(int row, int col) {
        try {
            String variable = (String) tableModel.getValueAt(row, 0);
            String value = (String) tableModel.getValueAt(row, col);
            String columnName = col == 1 ? "minimum" : "maximum";
            
            BigDecimal bdValue = new BigDecimal(value);
            db.updateValue(variable, columnName, bdValue);
            
        } catch (Exception e) {
            showError("Error updating value: " + e.getMessage());
            loadDataIntoTable(); // Reload to revert invalid changes
        }
    }
    
    private void runCalculations() {
        try {
            // Update database parameters
            BigDecimal spread = new BigDecimal(spreadField.getText());
            BigDecimal rateKA = new BigDecimal(rateKAField.getText());
            BigDecimal ratePN = new BigDecimal(ratePNField.getText());
            boolean basedOnMarketRate = marketRateCheckbox.isSelected();
            
            // Recreate database with new parameters
            db.close();
            db = new ConsoleDatabase(spread, rateKA, ratePN);
            db.setBasedOnMarketRate(basedOnMarketRate);
            
            // Get current table values and update database
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String variable = (String) tableModel.getValueAt(i, 0);
                String minStr = (String) tableModel.getValueAt(i, 1);
                String maxStr = (String) tableModel.getValueAt(i, 2);
                
                db.updateValue(variable, "minimum", new BigDecimal(minStr));
                db.updateValue(variable, "maximum", new BigDecimal(maxStr));
            }
            
            // Run calculations
            db.populateQueryVariables();
            
            // Reload table with results
            loadDataIntoTable();
            
            showInfo("Calculations completed successfully!");
            
        } catch (Exception e) {
            showError("Error running calculations: " + e.getMessage());
        }
    }
    
    private void loadExampleData() {
        try {
            // Example data
            db.updateValue("tradeprofit", "minimum", new BigDecimal("-88.000000000"));
            db.updateValue("tradeprofit", "maximum", new BigDecimal("-88.000000000"));
            db.updateValue("profitfactor", "minimum", new BigDecimal("-0.000497021"));
            db.updateValue("profitfactor", "maximum", new BigDecimal("-0.000497021"));
            db.updateValue("tradeamount", "minimum", new BigDecimal("10000.00"));
            db.updateValue("tradeamount", "maximum", new BigDecimal("10000.00"));
            db.updateValue("buyvariable", "minimum", new BigDecimal("17.7055"));
            db.updateValue("buyvariable", "maximum", new BigDecimal("17.7055"));
            db.updateValue("sellvariable", "minimum", new BigDecimal("17.6967"));
            db.updateValue("sellvariable", "maximum", new BigDecimal("17.6967"));
            
            loadDataIntoTable();
            showInfo("Example data loaded!");
            
        } catch (Exception e) {
            showError("Error loading examples: " + e.getMessage());
        }
    }
    
    private void resetData() {
        int result = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to reset all values to zero?",
            "Reset Confirmation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            try {
                String[] variables = {"tradeprofit", "profitfactor", "tradeamount", "buyvariable", "sellvariable"};
                
                for (String variable : variables) {
                    db.updateValue(variable, "minimum", BigDecimal.ZERO);
                    db.updateValue(variable, "maximum", BigDecimal.ZERO);
                    db.updateValue(variable, "returnmin", BigDecimal.ZERO);
                    db.updateValue(variable, "returnmax", BigDecimal.ZERO);
                }
                
                loadDataIntoTable();
                showInfo("All values reset to zero!");
                
            } catch (Exception e) {
                showError("Error resetting data: " + e.getMessage());
            }
        }
    }
    
    private void exportData() {
        try {
            db.exportToSQL();
            db.exportToCSV();
            showInfo("Data exported successfully!");
        } catch (Exception e) {
            showError("Error exporting data: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void showInfo(String message) {
        JOptionPane.showMessageDialog(frame, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }
}