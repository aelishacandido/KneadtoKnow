package kneadtoknow.gui;

import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import kneadtoknow.core.Inventory;
import kneadtoknow.model.StockTransaction;
import kneadtoknow.model.Supplier;

public class ReportsTabPanel extends javax.swing.JPanel {

    private Inventory inventory;

    public ReportsTabPanel() {
        initComponents();
        setupActionsAndStyling();
    }

    private void setupActionsAndStyling() {
        txtReportArea.setMargin(new Insets(10, 12, 10, 12));
        btnSuppliersSummary.setFont(new java.awt.Font("Baskerville Old Face", 1, 14));

        btnGenerateRestock.addActionListener(e -> onGenerateRestock());
        btnExportSupplierRestock.addActionListener(e -> onExportSupplierRestock());
        btnShowSummary.addActionListener(e -> refreshSummaryView());
        btnExportSummaryLedger.addActionListener(e -> onExportSummaryAndLedger());
        btnSuppliersSummary.addActionListener(e -> onShowAllSuppliersSummary());
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        refreshSuppliers();
        refreshSummaryView();
    }

    public void refreshSuppliers() {
        if (cmbSupplier == null) {
            return;
        }
        Supplier selectedBefore = (Supplier) cmbSupplier.getSelectedItem();
        DefaultComboBoxModel<Supplier> model = new DefaultComboBoxModel<>();

        if (inventory != null) {
            List<Supplier> supplierList = inventory.getSuppliers();
            if (supplierList != null) {
                for (Supplier s : supplierList) {
                    model.addElement(s);
                }
            }
        }

        cmbSupplier.setModel(model);

        if (selectedBefore != null) {
            for (int i = 0; i < model.getSize(); i++) {
                Supplier item = model.getElementAt(i);
                if (item.getSupplierId().equalsIgnoreCase(selectedBefore.getSupplierId())) {
                    cmbSupplier.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void refreshSummaryView() {
        if (inventory == null || txtReportArea == null) {
            return;
        }
        txtReportArea.setText(inventory.generateSummaryReport());
        txtReportArea.setCaretPosition(0);
    }

    private void onGenerateRestock() {
        if (inventory == null) {
            return;
        }
        Supplier selected = (Supplier) cmbSupplier.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier first.",
                    "No Supplier Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        txtReportArea.setText(inventory.generateSupplierRestockReport(selected));
        txtReportArea.setCaretPosition(0);
    }

    private void onExportSupplierRestock() {
        if (inventory == null) {
            return;
        }
        Supplier selected = (Supplier) cmbSupplier.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier before exporting.",
                    "No Supplier Selected",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }
        String report = inventory.generateSupplierRestockReport(selected);
        String fileName = "restock_" + selected.getSupplierId().toLowerCase() + ".txt";
        exportToTxt(report, fileName, "Export Supplier Restock to TXT");
    }

    private void onExportSummaryAndLedger() {
        if (inventory == null) {
            return;
        }
        StringBuilder body = new StringBuilder();
        body.append(inventory.generateSummaryReport());
        body.append("\n--- TRANSACTION LEDGER ---\n");
        for (StockTransaction tx : inventory.getTransactions()) {
            body.append(tx).append("\n");
        }
        exportToTxt(body.toString(), "inventory_summary_ledger.txt", "Export Summary Report & Ledger to TXT");
    }

    private void onShowAllSuppliersSummary() {
        if (inventory == null || txtReportArea == null) {
            return;
        }
        txtReportArea.setText(inventory.generateAllSuppliersSummaryReport());
        txtReportArea.setCaretPosition(0);
    }

    private void exportToTxt(String content, String defaultName, String dialogTitle) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(dialogTitle);
        chooser.setSelectedFile(new File(defaultName));
        chooser.setFileFilter(new FileNameExtensionFilter("Text Files (*.txt)", "txt"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File target = chooser.getSelectedFile();
            if (!target.getName().toLowerCase().endsWith(".txt")) {
                target = new File(target.getParentFile(), target.getName() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(target))) {
                writer.write(content);
                JOptionPane.showMessageDialog(
                        this,
                        "Exported successfully to:\n" + target.getAbsolutePath(),
                        "Export Complete",
                        JOptionPane.INFORMATION_MESSAGE
                );
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to export file: " + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cmbSupplier = new javax.swing.JComboBox<>();
        btnGenerateRestock = new javax.swing.JButton();
        btnShowSummary = new javax.swing.JButton();
        btnSuppliersSummary = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtReportArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        btnExportSummaryLedger = new javax.swing.JButton();
        btnExportSupplierRestock = new javax.swing.JButton();

        setBackground(new java.awt.Color(250, 218, 221));
        setLayout(new java.awt.BorderLayout());

        jPanel1.setBackground(new java.awt.Color(250, 218, 221));
        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Bookman Old Style", 1, 16)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(107, 68, 52));
        jLabel1.setText("REPORTS & EXPORT");
        jPanel1.add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel2.setBackground(new java.awt.Color(250, 218, 221));
        jPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));

        jLabel2.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(107, 68, 52));
        jLabel2.setText("Supplier:");
        jPanel2.add(jLabel2);

        cmbSupplier.setBackground(new java.awt.Color(245, 240, 230));
        cmbSupplier.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        jPanel2.add(cmbSupplier);

        btnGenerateRestock.setBackground(new java.awt.Color(208, 167, 123));
        btnGenerateRestock.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnGenerateRestock.setForeground(new java.awt.Color(41, 39, 38));
        btnGenerateRestock.setText("Generate Restock List");
        jPanel2.add(btnGenerateRestock);

        btnShowSummary.setBackground(new java.awt.Color(208, 167, 123));
        btnShowSummary.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnShowSummary.setForeground(new java.awt.Color(41, 39, 38));
        btnShowSummary.setText("Show Summary");
        jPanel2.add(btnShowSummary);

        btnSuppliersSummary.setBackground(new java.awt.Color(208, 167, 123));
        btnSuppliersSummary.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnSuppliersSummary.setForeground(new java.awt.Color(41, 39, 38));
        btnSuppliersSummary.setText("Suppliers Summary");
        jPanel2.add(btnSuppliersSummary);

        jPanel1.add(jPanel2, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.NORTH);

        txtReportArea.setEditable(false);
        txtReportArea.setBackground(new java.awt.Color(245, 240, 230));
        txtReportArea.setColumns(20);
        txtReportArea.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtReportArea.setRows(5);
        jScrollPane1.setViewportView(txtReportArea);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel3.setBackground(new java.awt.Color(250, 218, 221));
        jPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 4));

        btnExportSummaryLedger.setBackground(new java.awt.Color(102, 168, 134));
        btnExportSummaryLedger.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnExportSummaryLedger.setForeground(new java.awt.Color(41, 39, 38));
        btnExportSummaryLedger.setText("Export Summary Report & Ledger to TXT");
        jPanel3.add(btnExportSummaryLedger);

        btnExportSupplierRestock.setBackground(new java.awt.Color(102, 168, 134));
        btnExportSupplierRestock.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnExportSupplierRestock.setForeground(new java.awt.Color(41, 39, 38));
        btnExportSupplierRestock.setText("Export Supplier Restock to TXT");
        jPanel3.add(btnExportSupplierRestock);

        add(jPanel3, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExportSummaryLedger;
    private javax.swing.JButton btnExportSupplierRestock;
    private javax.swing.JButton btnGenerateRestock;
    private javax.swing.JButton btnShowSummary;
    private javax.swing.JButton btnSuppliersSummary;
    private javax.swing.JComboBox<Supplier> cmbSupplier;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtReportArea;
    // End of variables declaration//GEN-END:variables
}
