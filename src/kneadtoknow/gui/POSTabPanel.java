package kneadtoknow.gui;

import kneadtoknow.core.Inventory;
import kneadtoknow.exception.InsufficientStockException;
import kneadtoknow.exception.InvalidInputException;
import kneadtoknow.exception.ProductNotFoundException;
import kneadtoknow.model.BakingTools;
import kneadtoknow.model.PackagingItems;
import kneadtoknow.model.Product;
import kneadtoknow.model.RawIngredient;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class POSTabPanel extends javax.swing.JPanel {

    private Inventory inventory;
    private final List<CartItem> cart = new ArrayList<>();

    private static class CartItem {
        final Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        double getLineSubtotal() {
            return product.getUnitPrice() * quantity;
        }
    }

    public POSTabPanel() {
        initComponents();
        setupPOSControls();
    }

    public POSTabPanel(Inventory inventory) {
        initComponents();
        this.inventory = inventory;
        setupPOSControls();
        refreshCatalogTable();
        recalculateTotals();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        refreshCatalogTable();
        recalculateTotals();
    }

    private void setupPOSControls() {
        cmbCategory.setModel(new DefaultComboBoxModel<>(new String[]{
            "All", "Baking Tools", "Raw Ingredients", "Packaging Items"
        }));
        spnQty.setModel(new SpinnerNumberModel(1, 1, 999, 1));
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable2.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTextArea1.setEditable(false);
        jTextArea1.setFont(new Font("Monospaced", Font.PLAIN, 12));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { refreshCatalogTable(); }
            @Override public void removeUpdate(DocumentEvent e) { refreshCatalogTable(); }
            @Override public void changedUpdate(DocumentEvent e) { refreshCatalogTable(); }
        });

        cmbCategory.addActionListener(e -> refreshCatalogTable());
        chkLoyaltyDiscount.addActionListener(e -> recalculateTotals());

        txtCashTendered.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { updateChangeDisplay(); }
            @Override public void removeUpdate(DocumentEvent e) { updateChangeDisplay(); }
            @Override public void changedUpdate(DocumentEvent e) { updateChangeDisplay(); }
        });

        btnClear.addActionListener(this::btnClearActionPerformed);
        btnAddToCart.addActionListener(this::btnAddToCartActionPerformed);
        btnRemoveItem.addActionListener(this::btnRemoveItemActionPerformed);
        btnClearCart.addActionListener(this::btnClearCartActionPerformed);
        btnCheckout.addActionListener(this::btnCheckoutActionPerformed);
        btnPrintReceipt.addActionListener(this::btnPrintReceiptActionPerformed);
        btnNewSale.addActionListener(this::btnNewSaleActionPerformed);
    }

    public void refreshCatalogTable() {
        if (inventory == null) return;

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);

        String query = txtSearch.getText().trim().toLowerCase();
        String selectedCat = (cmbCategory.getSelectedItem() != null)
                ? cmbCategory.getSelectedItem().toString() : "All";

        for (Product p : inventory.getAllProducts()) {
            if (!matchesCategory(p, selectedCat)) continue;

            boolean matchesQuery = query.isEmpty()
                    || p.getId().toLowerCase().contains(query)
                    || p.getName().toLowerCase().contains(query);
            if (!matchesQuery) continue;

            model.addRow(new Object[]{
                p.getId(),
                p.getName(),
                String.format("%.2f", p.getUnitPrice()),
                p.getQuantity()
            });
        }
    }

    private boolean matchesCategory(Product p, String selectedCat) {
        if ("All".equalsIgnoreCase(selectedCat) || "All Categories".equalsIgnoreCase(selectedCat)) {
            return true;
        }
        if (selectedCat.toLowerCase().contains("tool")) {
            return p instanceof BakingTools;
        }
        if (selectedCat.toLowerCase().contains("ingredient")) {
            return p instanceof RawIngredient;
        }
        if (selectedCat.toLowerCase().contains("packag")) {
            return p instanceof PackagingItems;
        }
        return true;
    }

    private void refreshCartTable() {
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);

        for (CartItem item : cart) {
            model.addRow(new Object[]{
                item.product.getName(),
                item.quantity,
                String.format("%.2f", item.getLineSubtotal())
            });
        }
    }

    private double getSubtotal() {
        double sum = 0.0;
        for (CartItem item : cart) {
            sum += item.getLineSubtotal();
        }
        return sum;
    }

    private int getTotalCartUnits() {
        int units = 0;
        for (CartItem item : cart) {
            units += item.quantity;
        }
        return units;
    }

    private double getDiscountAmount(double subtotal) {
        int totalUnits = getTotalCartUnits();
        double bulkRate = (totalUnits >= 5) ? 0.10 : 0.0;
        double loyaltyRate = chkLoyaltyDiscount.isSelected() ? 0.05 : 0.0;
        return subtotal * (bulkRate + loyaltyRate);
    }

    private void recalculateTotals() {
        double subtotal = getSubtotal();
        int totalUnits = getTotalCartUnits();
        double discount = getDiscountAmount(subtotal);
        double total = Math.max(0.0, subtotal - discount);

        if (totalUnits >= 5) {
            lblBulkDiscount.setText("Bulk Discount (10% APPLIED - " + totalUnits + " items)");
        } else {
            lblBulkDiscount.setText("Bulk Discount: 5+ items for 10%");
        }

        lblSubtotal.setText(String.format("PHP %.2f", subtotal));
        lblDiscount.setText(String.format("-PHP %.2f", discount));
        lblTotal.setText(String.format("PHP %.2f", total));

        updateChangeDisplay();
    }

    private void updateChangeDisplay() {
        double subtotal = getSubtotal();
        double total = Math.max(0.0, subtotal - getDiscountAmount(subtotal));
        String rawCash = txtCashTendered.getText().trim();

        if (rawCash.isEmpty()) {
            lblChange.setText("PHP 0.00");
            return;
        }

        try {
            double cash = Double.parseDouble(rawCash);
            double change = cash - total;
            lblChange.setText(change >= 0 ? String.format("PHP %.2f", change) : "Insufficient");
        } catch (NumberFormatException e) {
            lblChange.setText("Invalid");
        }
    }

    private void btnClearActionPerformed(java.awt.event.ActionEvent evt) {
        txtSearch.setText("");
        if (cmbCategory.getItemCount() > 0) {
            cmbCategory.setSelectedIndex(0);
        }
        refreshCatalogTable();
        lblStatus.setText("Filters cleared.");
    }

    private void btnAddToCartActionPerformed(java.awt.event.ActionEvent evt) {
        if (inventory == null) return;

        int selectedRow = jTable1.getSelectedRow();
        if (selectedRow < 0) {
            lblStatus.setText("Select a product from the catalog first.");
            return;
        }

        int modelRow = jTable1.convertRowIndexToModel(selectedRow);
        String productId = String.valueOf(jTable1.getModel().getValueAt(modelRow, 0));
        Product product = inventory.searchByIdRecursive(productId, 0);

        if (product == null) {
            lblStatus.setText("Product not found in inventory.");
            return;
        }

        int qtyToAdd = (Integer) spnQty.getValue();
        CartItem existing = null;
        for (CartItem item : cart) {
            if (item.product.getId().equalsIgnoreCase(product.getId())) {
                existing = item;
                break;
            }
        }

        int alreadyInCart = (existing != null) ? existing.quantity : 0;
        if (alreadyInCart + qtyToAdd > product.getQuantity()) {
            lblStatus.setText("Insufficient stock for " + product.getName()
                    + " (Available: " + product.getQuantity() + ").");
            return;
        }

        if (existing != null) {
            existing.quantity += qtyToAdd;
        } else {
            cart.add(new CartItem(product, qtyToAdd));
        }

        spnQty.setValue(1);
        refreshCartTable();
        recalculateTotals();
        lblStatus.setText("Added " + qtyToAdd + " x " + product.getName() + " to cart.");
    }

    private void btnRemoveItemActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedRow = jTable2.getSelectedRow();
        if (selectedRow < 0) {
            lblStatus.setText("Select a cart item to remove.");
            return;
        }

        int modelRow = jTable2.convertRowIndexToModel(selectedRow);
        if (modelRow >= 0 && modelRow < cart.size()) {
            CartItem removed = cart.remove(modelRow);
            refreshCartTable();
            recalculateTotals();
            lblStatus.setText("Removed " + removed.product.getName() + " from cart.");
        }
    }

    private void btnClearCartActionPerformed(java.awt.event.ActionEvent evt) {
        cart.clear();
        chkLoyaltyDiscount.setSelected(false);
        txtCashTendered.setText("");
        refreshCartTable();
        recalculateTotals();
        lblStatus.setText("Cart cleared.");
    }

    private void btnCheckoutActionPerformed(java.awt.event.ActionEvent evt) {
        if (inventory == null) return;

        if (cart.isEmpty()) {
            lblStatus.setText("Cart is empty. Add items before checking out.");
            return;
        }

        double subtotal = getSubtotal();
        double discount = getDiscountAmount(subtotal);
        double total = Math.max(0.0, subtotal - discount);

        double cashTendered;
        try {
            cashTendered = Double.parseDouble(txtCashTendered.getText().trim());
        } catch (NumberFormatException e) {
            lblStatus.setText("Enter a valid cash amount.");
            return;
        }

        if (cashTendered < total) {
            lblStatus.setText(String.format("Insufficient cash. Total is PHP %.2f.", total));
            return;
        }

        List<String> lowStockWarnings = new ArrayList<>();
        try {
            for (CartItem item : cart) {
                boolean lowAlert = inventory.stockOut(item.product.getId(), item.quantity, "POS_SALE");
                if (lowAlert) {
                    lowStockWarnings.add(item.product.getName() + " (Qty: " + item.product.getQuantity() + ")");
                }
            }
        } catch (ProductNotFoundException | InvalidInputException | InsufficientStockException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double change = cashTendered - total;
        jTextArea1.setText(buildReceiptText(subtotal, discount, total, cashTendered, change));

        cart.clear();
        refreshCartTable();
        refreshCatalogTable();
        recalculateTotals();
        lblChange.setText(String.format("PHP %.2f", change));

        if (!lowStockWarnings.isEmpty()) {
            lblStatus.setText("Sale complete! Low stock alert: " + String.join(", ", lowStockWarnings));
        } else {
            lblStatus.setText("Sale completed and saved to inventory.");
        }
    }

    private String buildReceiptText(double subtotal, double discount, double total, double cash, double change) {
        StringBuilder sb = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        sb.append("========================================\n");
        sb.append("        KNEAD TO KNOW BAKING SHOP       \n");
        sb.append("          OFFICIAL POS RECEIPT          \n");
        sb.append("========================================\n");
        sb.append("Date: ").append(timestamp).append("\n");
        sb.append("----------------------------------------\n");

        for (CartItem item : cart) {
            sb.append(String.format("%-22s x%-3d %10.2f%n",
                    trimName(item.product.getName(), 22),
                    item.quantity,
                    item.getLineSubtotal()));
        }

        sb.append("----------------------------------------\n");
        sb.append(String.format("Subtotal:               PHP %10.2f%n", subtotal));
        sb.append(String.format("Discount:              -PHP %10.2f%n", discount));
        sb.append(String.format("TOTAL:                  PHP %10.2f%n", total));
        sb.append(String.format("Cash Tendered:          PHP %10.2f%n", cash));
        sb.append(String.format("Change:                 PHP %10.2f%n", change));
        sb.append("========================================\n");
        sb.append("     Thank you for baking with us!      \n");
        return sb.toString();
    }

    private String trimName(String name, int maxLen) {
        return (name.length() <= maxLen) ? name : name.substring(0, maxLen - 3) + "...";
    }

    private void btnPrintReceiptActionPerformed(java.awt.event.ActionEvent evt) {
        String receipt = jTextArea1.getText();
        if (receipt == null || receipt.trim().isEmpty()) {
            lblStatus.setText("No receipt to copy.");
            return;
        }
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(receipt), null);
        lblStatus.setText("Receipt copied to clipboard!");
    }

    private void btnNewSaleActionPerformed(java.awt.event.ActionEvent evt) {
        cart.clear();
        chkLoyaltyDiscount.setSelected(false);
        txtCashTendered.setText("");
        jTextArea1.setText("");
        lblChange.setText("PHP 0.00");
        refreshCartTable();
        refreshCatalogTable();
        recalculateTotals();
        lblStatus.setText("Ready for new sale.");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlTop = new javax.swing.JPanel();
        pnlHeader = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        pnlFilter = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        cmbCategory = new javax.swing.JComboBox<>();
        btnClear = new javax.swing.JButton();
        pnlMain = new javax.swing.JPanel();
        pnlCatalog = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        tblProducts = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        pnlCatalogBottom = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        spnQty = new javax.swing.JSpinner();
        btnAddToCart = new javax.swing.JButton();
        pnlCart = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        tblCart = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        pnlCartBottom = new javax.swing.JPanel();
        pnlActionRow = new javax.swing.JPanel();
        btnRemoveItem = new javax.swing.JButton();
        btnClearCart = new javax.swing.JButton();
        pnlLoyalty = new javax.swing.JPanel();
        chkLoyaltyDiscount = new javax.swing.JCheckBox();
        pnlBulk = new javax.swing.JPanel();
        lblBulkDiscount = new javax.swing.JLabel();
        pnlSubTotal = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        lblSubtotal = new javax.swing.JLabel();
        pnlDiscount = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        lblDiscount = new javax.swing.JLabel();
        pnlTotal = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        pnlCashChange = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        txtCashTendered = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        lblChange = new javax.swing.JLabel();
        pnlCheckout = new javax.swing.JPanel();
        btnCheckout = new javax.swing.JButton();
        pnlReceipt = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        txtReceipt = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        btnPrintReceipt = new javax.swing.JButton();
        btnNewSale = new javax.swing.JButton();
        pnlStatus = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 204, 204));
        setBorder(javax.swing.BorderFactory.createEtchedBorder());
        setForeground(new java.awt.Color(255, 204, 204));
        setLayout(new java.awt.BorderLayout());

        pnlTop.setBackground(new java.awt.Color(250, 218, 221));
        pnlTop.setLayout(new java.awt.GridLayout(2, 1));

        pnlHeader.setBackground(new java.awt.Color(242, 166, 184));
        pnlHeader.setMaximumSize(new java.awt.Dimension(32767, 52));
        pnlHeader.setPreferredSize(new java.awt.Dimension(600, 38));

        jLabel2.setFont(new java.awt.Font("Bookman Old Style", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(84, 51, 36));
        jLabel2.setText("Point of Sale / POS");

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(748, Short.MAX_VALUE))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlTop.add(pnlHeader);

        pnlFilter.setBackground(new java.awt.Color(250, 218, 221));
        pnlFilter.setPreferredSize(new java.awt.Dimension(525, 18));

        jLabel3.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(107, 68, 52));
        jLabel3.setText("Search:");
        pnlFilter.add(jLabel3);

        txtSearch.setBackground(new java.awt.Color(245, 240, 230));
        txtSearch.setColumns(18);
        txtSearch.setFont(new java.awt.Font("Baskerville Old Face", 0, 12)); // NOI18N
        txtSearch.setForeground(new java.awt.Color(107, 68, 52));
        pnlFilter.add(txtSearch);

        jLabel4.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(107, 68, 52));
        jLabel4.setText("Category:");
        pnlFilter.add(jLabel4);

        cmbCategory.setBackground(new java.awt.Color(245, 240, 230));
        cmbCategory.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        cmbCategory.setForeground(new java.awt.Color(107, 68, 52));
        cmbCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "Baking Tools", "Raw Ingredients", "Packaging Items" }));
        pnlFilter.add(cmbCategory);

        btnClear.setBackground(new java.awt.Color(219, 215, 208));
        btnClear.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnClear.setForeground(new java.awt.Color(41, 39, 38));
        btnClear.setText("Clear");
        pnlFilter.add(btnClear);

        pnlTop.add(pnlFilter);

        add(pnlTop, java.awt.BorderLayout.NORTH);

        pnlMain.setBackground(new java.awt.Color(250, 218, 221));
        pnlMain.setLayout(new java.awt.GridLayout(1, 3, 10, 10));

        pnlCatalog.setBackground(new java.awt.Color(250, 218, 221));
        pnlCatalog.setLayout(new java.awt.BorderLayout(6, 6));

        jLabel5.setBackground(new java.awt.Color(250, 218, 221));
        jLabel5.setFont(new java.awt.Font("Bookman Old Style", 1, 16)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(107, 68, 52));
        jLabel5.setText("Available Baking Supplies");
        pnlCatalog.add(jLabel5, java.awt.BorderLayout.NORTH);

        jTable1.setAutoCreateRowSorter(true);
        jTable1.setBackground(new java.awt.Color(245, 240, 230));
        jTable1.setFont(new java.awt.Font("Baskerville Old Face", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Name", "Price", "Qty"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.setColumnSelectionAllowed(true);
        jTable1.getTableHeader().setReorderingAllowed(false);
        tblProducts.setViewportView(jTable1);
        jTable1.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        pnlCatalog.add(tblProducts, java.awt.BorderLayout.CENTER);

        pnlCatalogBottom.setBackground(new java.awt.Color(250, 218, 221));
        pnlCatalogBottom.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel6.setFont(new java.awt.Font("Baskerville Old Face", 1, 12)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(107, 68, 52));
        jLabel6.setText("Qty:");
        pnlCatalogBottom.add(jLabel6);
        pnlCatalogBottom.add(spnQty);

        btnAddToCart.setBackground(new java.awt.Color(98, 148, 196));
        btnAddToCart.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnAddToCart.setForeground(new java.awt.Color(59, 36, 24));
        btnAddToCart.setText("Add to Cart");
        pnlCatalogBottom.add(btnAddToCart);

        pnlCatalog.add(pnlCatalogBottom, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlCatalog);

        pnlCart.setBackground(new java.awt.Color(250, 218, 221));
        pnlCart.setLayout(new java.awt.BorderLayout(6, 6));

        jLabel7.setBackground(new java.awt.Color(250, 218, 221));
        jLabel7.setFont(new java.awt.Font("Bookman Old Style", 1, 16)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(107, 68, 52));
        jLabel7.setText("Current Sale / Cart");
        pnlCart.add(jLabel7, java.awt.BorderLayout.NORTH);

        jTable2.setBackground(new java.awt.Color(245, 240, 230));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Item", "Qty", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Integer.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.setColumnSelectionAllowed(true);
        jTable2.getTableHeader().setReorderingAllowed(false);
        tblCart.setViewportView(jTable2);
        jTable2.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        pnlCart.add(tblCart, java.awt.BorderLayout.CENTER);

        pnlCartBottom.setBackground(new java.awt.Color(250, 218, 221));
        pnlCartBottom.setLayout(new javax.swing.BoxLayout(pnlCartBottom, javax.swing.BoxLayout.Y_AXIS));

        pnlActionRow.setBackground(new java.awt.Color(250, 218, 221));
        pnlActionRow.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        btnRemoveItem.setBackground(new java.awt.Color(219, 98, 110));
        btnRemoveItem.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnRemoveItem.setForeground(new java.awt.Color(59, 36, 24));
        btnRemoveItem.setText("Remove Selected");
        pnlActionRow.add(btnRemoveItem);

        btnClearCart.setBackground(new java.awt.Color(219, 215, 208));
        btnClearCart.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnClearCart.setForeground(new java.awt.Color(41, 39, 38));
        btnClearCart.setText("Clear Cart");
        pnlActionRow.add(btnClearCart);

        pnlCartBottom.add(pnlActionRow);

        pnlLoyalty.setBackground(new java.awt.Color(250, 218, 221));
        pnlLoyalty.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 2));

        chkLoyaltyDiscount.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        chkLoyaltyDiscount.setForeground(new java.awt.Color(107, 68, 52));
        chkLoyaltyDiscount.setText("Loyalty Discount (5%)");
        pnlLoyalty.add(chkLoyaltyDiscount);

        pnlCartBottom.add(pnlLoyalty);

        pnlBulk.setBackground(new java.awt.Color(250, 218, 221));
        pnlBulk.setMaximumSize(new java.awt.Dimension(32767, 30));
        pnlBulk.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        lblBulkDiscount.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        lblBulkDiscount.setForeground(new java.awt.Color(102, 168, 134));
        lblBulkDiscount.setText("Bulk Discount: 5+ items for 10%");
        pnlBulk.add(lblBulkDiscount);

        pnlCartBottom.add(pnlBulk);

        pnlSubTotal.setBackground(new java.awt.Color(250, 218, 221));
        pnlSubTotal.setMaximumSize(new java.awt.Dimension(32767, 30));
        pnlSubTotal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        jLabel9.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(107, 68, 52));
        jLabel9.setText("Subtotal:");
        pnlSubTotal.add(jLabel9);

        lblSubtotal.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        lblSubtotal.setForeground(new java.awt.Color(107, 68, 52));
        lblSubtotal.setText("PHP 0.00");
        pnlSubTotal.add(lblSubtotal);

        pnlCartBottom.add(pnlSubTotal);

        pnlDiscount.setBackground(new java.awt.Color(250, 218, 221));
        pnlDiscount.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        jLabel10.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(107, 68, 52));
        jLabel10.setText("Discount:");
        pnlDiscount.add(jLabel10);

        lblDiscount.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        lblDiscount.setForeground(new java.awt.Color(219, 98, 110));
        lblDiscount.setText("-PHP 0.00");
        pnlDiscount.add(lblDiscount);

        pnlCartBottom.add(pnlDiscount);

        pnlTotal.setBackground(new java.awt.Color(250, 218, 221));
        pnlTotal.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        jLabel8.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(107, 68, 52));
        jLabel8.setText("Total:");
        pnlTotal.add(jLabel8);

        lblTotal.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(107, 68, 52));
        lblTotal.setText("PHP 0.00");
        pnlTotal.add(lblTotal);

        pnlCartBottom.add(pnlTotal);

        pnlCashChange.setBackground(new java.awt.Color(250, 218, 221));
        pnlCashChange.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        jLabel11.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(107, 68, 52));
        jLabel11.setText("Cash:");
        pnlCashChange.add(jLabel11);

        txtCashTendered.setBackground(new java.awt.Color(245, 240, 230));
        txtCashTendered.setColumns(10);
        pnlCashChange.add(txtCashTendered);

        jLabel12.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(107, 68, 52));
        jLabel12.setText("Change:");
        pnlCashChange.add(jLabel12);

        lblChange.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        lblChange.setForeground(new java.awt.Color(107, 68, 52));
        lblChange.setText("PHP 0.00");
        pnlCashChange.add(lblChange);

        pnlCartBottom.add(pnlCashChange);

        pnlCheckout.setBackground(new java.awt.Color(250, 218, 221));
        pnlCheckout.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 4, 2));

        btnCheckout.setBackground(new java.awt.Color(102, 168, 134));
        btnCheckout.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnCheckout.setForeground(new java.awt.Color(107, 68, 52));
        btnCheckout.setText("Checkout");
        pnlCheckout.add(btnCheckout);

        pnlCartBottom.add(pnlCheckout);

        pnlCart.add(pnlCartBottom, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlCart);

        pnlReceipt.setBackground(new java.awt.Color(250, 218, 221));
        pnlReceipt.setLayout(new java.awt.BorderLayout(4, 6));

        jLabel13.setBackground(new java.awt.Color(250, 218, 221));
        jLabel13.setFont(new java.awt.Font("Bookman Old Style", 1, 16)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(107, 68, 52));
        jLabel13.setText("Receipt Preview");
        pnlReceipt.add(jLabel13, java.awt.BorderLayout.NORTH);

        jTextArea1.setEditable(false);
        jTextArea1.setBackground(new java.awt.Color(245, 240, 230));
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        txtReceipt.setViewportView(jTextArea1);

        pnlReceipt.add(txtReceipt, java.awt.BorderLayout.CENTER);

        jPanel1.setBackground(new java.awt.Color(250, 218, 221));
        jPanel1.setLayout(new java.awt.GridLayout(2, 1, 6, 6));

        btnPrintReceipt.setBackground(new java.awt.Color(98, 148, 196));
        btnPrintReceipt.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnPrintReceipt.setForeground(new java.awt.Color(107, 68, 52));
        btnPrintReceipt.setText("Print / Copy");
        jPanel1.add(btnPrintReceipt);

        btnNewSale.setBackground(new java.awt.Color(102, 168, 134));
        btnNewSale.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        btnNewSale.setForeground(new java.awt.Color(107, 68, 52));
        btnNewSale.setText("New Sale");
        jPanel1.add(btnNewSale);

        pnlReceipt.add(jPanel1, java.awt.BorderLayout.SOUTH);

        pnlMain.add(pnlReceipt);

        add(pnlMain, java.awt.BorderLayout.CENTER);

        pnlStatus.setBackground(new java.awt.Color(250, 218, 221));

        jLabel1.setFont(new java.awt.Font("Baskerville Old Face", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(107, 68, 52));
        jLabel1.setText("Status:");

        lblStatus.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(0, 153, 51));
        lblStatus.setText("Ready");

        javax.swing.GroupLayout pnlStatusLayout = new javax.swing.GroupLayout(pnlStatus);
        pnlStatus.setLayout(pnlStatusLayout);
        pnlStatusLayout.setHorizontalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(640, Short.MAX_VALUE))
        );
        pnlStatusLayout.setVerticalGroup(
            pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlStatusLayout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(pnlStatusLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblStatus))
                .addContainerGap(68, Short.MAX_VALUE))
        );

        add(pnlStatus, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddToCart;
    private javax.swing.JButton btnCheckout;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnClearCart;
    private javax.swing.JButton btnNewSale;
    private javax.swing.JButton btnPrintReceipt;
    private javax.swing.JButton btnRemoveItem;
    private javax.swing.JCheckBox chkLoyaltyDiscount;
    private javax.swing.JComboBox<String> cmbCategory;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JLabel lblBulkDiscount;
    private javax.swing.JLabel lblChange;
    private javax.swing.JLabel lblDiscount;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblSubtotal;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JPanel pnlActionRow;
    private javax.swing.JPanel pnlBulk;
    private javax.swing.JPanel pnlCart;
    private javax.swing.JPanel pnlCartBottom;
    private javax.swing.JPanel pnlCashChange;
    private javax.swing.JPanel pnlCatalog;
    private javax.swing.JPanel pnlCatalogBottom;
    private javax.swing.JPanel pnlCheckout;
    private javax.swing.JPanel pnlDiscount;
    private javax.swing.JPanel pnlFilter;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlLoyalty;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlReceipt;
    private javax.swing.JPanel pnlStatus;
    private javax.swing.JPanel pnlSubTotal;
    private javax.swing.JPanel pnlTop;
    private javax.swing.JPanel pnlTotal;
    private javax.swing.JSpinner spnQty;
    private javax.swing.JScrollPane tblCart;
    private javax.swing.JScrollPane tblProducts;
    private javax.swing.JTextField txtCashTendered;
    private javax.swing.JScrollPane txtReceipt;
    private javax.swing.JTextField txtSearch;
    // End of variables declaration//GEN-END:variables
}
