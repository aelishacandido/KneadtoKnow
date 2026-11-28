package kneadtoknow.core;

import kneadtoknow.exception.InsufficientStockException;
import kneadtoknow.exception.InvalidInputException;
import kneadtoknow.exception.ProductNotFoundException;
import kneadtoknow.io.FileStorage;
import kneadtoknow.model.BakingTools;
import kneadtoknow.model.PackagingItems;
import kneadtoknow.model.Product;
import kneadtoknow.model.RawIngredient;
import kneadtoknow.model.StockTransaction;
import kneadtoknow.model.Supplier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Inventory {

    private final List<Product> products;
    private final List<StockTransaction> transactions;
    private final List<Supplier> suppliers;
    private final FileStorage storage;

    private static final String PRODUCTS_FILE = "data/products.csv";
    private static final String TRANSACTIONS_FILE = "data/transactions.csv";
    private static final String SUPPLIERS_FILE = "data/suppliers.csv";

    public Inventory() {
        this.products = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.suppliers = new ArrayList<>();
        this.storage = new FileStorage();

        if (!loadSuppliersFromFile()) {
            initDefaultSuppliers();
            saveSuppliersQuietly();
        }

        if (!loadProductsFromFile()) {
            seedSampleData();
            saveAllQuietly();
        }
        loadTransactionsFromFile();
    }

    private void initDefaultSuppliers() {
        suppliers.clear();
        Supplier supTool1 = new Supplier("SUP-T01", "Sandok & Spatula Co.", "0912-345-6789");
        Supplier supTool2 = new Supplier("SUP-T02", "Whisk Takers Supply Co.", "0911-223-3445");

        Supplier supRaw1 = new Supplier("SUP-R01", "Butter Believe It Ingredients", "0920-321-1231");
        Supplier supRaw2 = new Supplier("SUP-R02", "Harina Potter & Co.", "0967-146-0202");
        Supplier supRaw3 = new Supplier("SUP-R03", "Pink Ribbon Corp.", "0917-555-0223");

        Supplier supPack1 = new Supplier("SUP-P01", "Tupper-Where? Packaging Co.", "0928-565-2324");
        Supplier supPack2 = new Supplier("SUP-P02", "Pack It Up Supplies", "0910-333-9988");

        suppliers.addAll(Arrays.asList(supTool1, supTool2, supRaw1, supRaw2, supRaw3, supPack1, supPack2));
    }

    private Supplier findSupplierByName(String name) {
        if (name == null || name.trim().isEmpty() || "None".equalsIgnoreCase(name.trim())) {
            return null;
        }
        for (Supplier s : suppliers) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    private Supplier findSupplierById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }
        for (Supplier s : suppliers) {
            if (s.getSupplierId().equalsIgnoreCase(id.trim())) {
                return s;
            }
        }
        return null;
    }

    private void seedSampleData() {
        products.clear();
        transactions.clear();
        if (suppliers.isEmpty()) {
            initDefaultSuppliers();
        }

        Supplier supTool1 = findSupplierById("SUP-T01");
        Supplier supTool2 = findSupplierById("SUP-T02");
        Supplier supRaw1 = findSupplierById("SUP-R01");
        Supplier supRaw2 = findSupplierById("SUP-R02");
        Supplier supRaw3 = findSupplierById("SUP-R03");
        Supplier supPack1 = findSupplierById("SUP-P01");
        Supplier supPack2 = findSupplierById("SUP-P02");

        try {
            products.add(new BakingTools("TOL-101", "Stainless Steel Wire Whisk (12 inches)", 80.00, 6, 8, supTool2, 6));
            products.add(new BakingTools("TOL-102", "9.52x5x2.5 Large Loaf Pan (Carbon Steel)", 429.00, 18, 10, supTool1, 12));
            products.add(new BakingTools("TOL-103", "Heat Resistant Silicone Spatula Scraper Set (5 pcs)", 299.00, 9, 10, supTool1, 6));
            products.add(new BakingTools("TOL-104", "Stainless Steel Measuring Spoons Set of 6", 199.00, 22, 12, supTool2, 12));
            products.add(new BakingTools("TOL-105", "5pcs Clear Plastic Measuring Cup (20ml-500ml)", 599.00, 14, 8, supTool2, 6));
            products.add(new BakingTools("TOL-106", "Wooden Rolling Pin 38cm", 229.00, 4, 6, supTool1, 6));
            products.add(new BakingTools("TOL-107", "Non-Stick Parchment Baking Paper Roll", 359.00, 25, 10, supTool1, 0));
            products.add(new BakingTools("TOL-108", "LCD Digital Kitchen Timer", 56.00, 10, 10, supTool2, 3));
            products.add(new BakingTools("TOL-109", "Flat Baking Sheet - Pandesal Tray (Aluminum)", 119.00, 16, 10, supTool1, 12));
            products.add(new BakingTools("TOL-110", "Heat-Resistant Oven Gloves", 555.00, 7, 8, supTool2, 6));

            products.add(new RawIngredient("ING-001", "Wooden Spoon All-Purpose Flour (25kg)", 1840.25, 25, 12, supRaw2, "25 kg sack", "2027-06-30"));
            products.add(new RawIngredient("ING-002", "Eagle Instant Dry Yeast (125g)", 120.00, 15, 10, supRaw3, "125 g pack", "2027-03-15"));
            products.add(new RawIngredient("ING-003", "Dark Brown Sugar (1kg)", 98.15, 5, 10, supRaw1, "1 kg pack", "2027-08-20"));
            products.add(new RawIngredient("ING-004", "Bensdorp DSR Cocoa Powder (500g)", 485.00, 20, 10, supRaw3, "500 g pack", "2027-05-10"));
            products.add(new RawIngredient("ING-005", "Ram Baking Soda (125g)", 12.25, 8, 12, supRaw2, "125 g box", "2027-11-30"));
            products.add(new RawIngredient("ING-006", "Beryl's 8% Milk Compound", 365.00, 30, 15, supRaw1, "1 kg bar", "2027-04-18"));
            products.add(new RawIngredient("ING-007", "Light Brown Sugar (1kg)", 94.15, 18, 10, supRaw3, "1 kg pack", "2027-08-20"));
            products.add(new RawIngredient("ING-008", "UFC Hapi Fiesta Vegetable Oil (500ml)", 75.50, 10, 10, supRaw1, "500 ml bottle", "2027-02-28"));
            products.add(new RawIngredient("ING-009", "RICOA Chocolate Syrup", 259.20, 15, 8, supRaw3, "bottle", "2027-07-12"));
            products.add(new RawIngredient("ING-010", "Cowbell Evaporada (370ml)", 25.00, 8, 10, supRaw1, "370 ml can", "2027-09-01"));

            products.add(new PackagingItems("PCK-201", "White Plain Cake Board Round", 240.75, 24, 10, supPack2, "12 inches round", 10));
            products.add(new PackagingItems("PCK-202", "Rectangular Microwavable Clear Pack", 63.00, 7, 10, supPack1, "Standard Rectangular", 10));
            products.add(new PackagingItems("PCK-203", "Cake Container Ops", 130.00, 19, 10, supPack1, "C35 Standard", 10));
            products.add(new PackagingItems("PCK-204", "PCC Otso Bottle", 15.00, 6, 8, supPack1, "300 ml", 1));
            products.add(new PackagingItems("PCK-205", "Tri Cake Slice Case", 12.50, 35, 15, supPack2, "Large Triangle", 1));
            products.add(new PackagingItems("PCK-206", "Revolving Cake Decorating Stand", 1820.00, 4, 5, supPack2, "10 inches diameter", 1));
            products.add(new PackagingItems("PCK-207", "RM Pre-Formed Solo Cupcake Box", 245.05, 16, 10, supPack2, "3.25 x 3.25 x 2.75 in", 10));
            products.add(new PackagingItems("PCK-208", "Wooden Spoon", 190.00, 9, 10, supPack2, "16 cm", 100));
            products.add(new PackagingItems("PCK-209", "Sealable Pastry Pouch", 90.00, 28, 12, supPack2, "10 x 10 cm", 10));
            products.add(new PackagingItems("PCK-210", "Medium Kraft Paper Cake & Pastry Box with Handle", 185.00, 8, 10, supPack1, "8 x 8 x 4 in", 20));

            for (Product p : products) {
                transactions.add(new StockTransaction("INITIAL_LOAD", p.getId(), p.getName(), p.getQuantity()));
            }
        } catch (InvalidInputException e) {
            System.err.print("Seed error: " + e.getMessage() + "\n");
        }
    }

    public Product searchByIdRecursive(String id, int index) {
        if (id == null || index >= products.size()) {
            return null;
        }
        if (products.get(index).getId().equalsIgnoreCase(id.trim())) {
            return products.get(index);
        }
        return searchByIdRecursive(id, index + 1);
    }

    public int countLowStockRecursive(int index) {
        if (index >= products.size()) {
            return 0;
        }
        int current = 0;
        if (products.get(index).isAlertTriggered()) {
            current = 1;
        }
        return current + countLowStockRecursive(index + 1);
    }

    public void addSupplier(Supplier s) throws InvalidInputException {
        if (s == null) {
            throw new InvalidInputException("Supplier cannot be null.");
        }
        if (findSupplierById(s.getSupplierId()) != null) {
            throw new InvalidInputException("Duplicate Supplier ID: " + s.getSupplierId());
        }
        suppliers.add(s);
        saveSuppliersQuietly();
    }

    public void addProduct(Product p) throws InvalidInputException {
        if (p == null) {
            throw new InvalidInputException("Product cannot be null.");
        }
        if (searchByIdRecursive(p.getId(), 0) != null) {
            throw new InvalidInputException("Duplicate ID: " + p.getId());
        }
        products.add(p);
        transactions.add(new StockTransaction("ADD", p.getId(), p.getName(), p.getQuantity()));
        saveAllQuietly();
    }

    public void updateProduct(String id, String name, double price, int reorder, String catField)
            throws ProductNotFoundException, InvalidInputException {
        Product p = searchByIdRecursive(id, 0);
        if (p == null) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        p.setName(name);
        p.setUnitPrice(price);
        p.setReorderLevel(reorder);
        if (catField != null && !catField.trim().isEmpty()) {
            p.updateCategoryField(catField);
        }
        transactions.add(new StockTransaction("UPDATE", p.getId(), p.getName(), 0));
        saveAllQuietly();
    }

    public void deleteProduct(String id) throws ProductNotFoundException, InvalidInputException {
        Product p = searchByIdRecursive(id, 0);
        if (p == null) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        products.remove(p);
        transactions.add(new StockTransaction("DELETE", p.getId(), p.getName(), p.getQuantity()));
        saveAllQuietly();
    }

    public void stockIn(String id, int amount) throws ProductNotFoundException, InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        Product p = searchByIdRecursive(id, 0);
        if (p == null) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        p.stockIn(amount);
        transactions.add(new StockTransaction("STOCK_IN", p.getId(), p.getName(), amount));
        saveAllQuietly();
    }

    public boolean stockOut(String id, int amount, String type)
            throws ProductNotFoundException, InvalidInputException, InsufficientStockException {
        if (amount <= 0) {
            throw new InvalidInputException("Amount must be greater than zero.");
        }
        Product p = searchByIdRecursive(id, 0);
        if (p == null) {
            throw new ProductNotFoundException("Product not found: " + id);
        }
        if (amount > p.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock available.");
        }
        p.stockOut(amount);
        transactions.add(new StockTransaction(type, p.getId(), p.getName(), amount));
        saveAllQuietly();
        return p.isAlertTriggered();
    }

    public String undoLastTransaction() throws InvalidInputException, ProductNotFoundException {
        for (int i = transactions.size() - 1; i >= 0; i--) {
            StockTransaction tx = transactions.get(i);
            String txType = tx.getType();
            if ("STOCK_IN".equals(txType) || "STOCK_OUT".equals(txType) || "POS_SALE".equals(txType)) {
                Product p = searchByIdRecursive(tx.getProductId(), 0);
                if (p == null) {
                    throw new ProductNotFoundException("Product no longer exists.");
                }
                if ("STOCK_IN".equals(txType)) {
                    if (p.getQuantity() < tx.getQuantityChanged()) {
                        throw new InvalidInputException("Cannot undo; items already used or sold.");
                    }
                    p.stockOut(tx.getQuantityChanged());
                } else {
                    p.stockIn(tx.getQuantityChanged());
                }
                transactions.remove(i);
                transactions.add(new StockTransaction("REVERSAL", p.getId(), p.getName(), tx.getQuantityChanged()));
                saveAllQuietly();
                return "Reversed transaction for " + p.getName();
            }
        }
        throw new InvalidInputException("No reversible transaction found.");
    }

    public void sortProducts(String criteria, boolean ascending) {
        Comparator<Product> comp;
        if ("Price".equalsIgnoreCase(criteria)) {
            comp = Comparator.comparingDouble(Product::getUnitPrice);
        } else if ("Quantity".equalsIgnoreCase(criteria)) {
            comp = Comparator.comparingInt(Product::getQuantity);
        } else {
            comp = Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
        }
        if (!ascending) {
            comp = comp.reversed();
        }
        products.sort(comp);
    }

    public String generateSummaryReport() {
        StringBuilder sb = new StringBuilder();
        int totalUnits = 0;
        double totalVal = 0.0;
        for (Product p : products) {
            totalUnits += p.getQuantity();
            totalVal += p.calculateInventoryValue();
        }
        sb.append("Total Products: ").append(products.size())
          .append(" | Total Units: ").append(totalUnits)
          .append(String.format(" | Total Value: PHP %.2f%n", totalVal));
        sb.append("Low Stock Alert Count (Recursive): ").append(countLowStockRecursive(0)).append("\n");
        return sb.toString();
    }

    public void exportReport(String filePath) throws IOException {
        StringBuilder body = new StringBuilder();
        body.append(generateSummaryReport());
        body.append("\n--- TRANSACTION LEDGER ---\n");
        for (StockTransaction tx : transactions) {
            body.append(tx).append("\n");
        }
        storage.exportReport(filePath, "KNEAD TO KNOW - INVENTORY REPORT", body.toString());
    }

    public void saveSuppliersToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Supplier s : suppliers) {
            lines.add(s.getSupplierId() + "," + s.getName() + "," + s.getContact());
        }
        storage.saveLines(SUPPLIERS_FILE, lines);
    }

    public boolean loadSuppliersFromFile() {
        try {
            List<String> lines = storage.readLines(SUPPLIERS_FILE);
            if (lines.isEmpty()) {
                return false;
            }
            suppliers.clear();
            for (String line : lines) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 3) {
                    suppliers.add(new Supplier(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
            return !suppliers.isEmpty();
        } catch (IOException e) {
            System.err.print("Warning: Could not load suppliers file: " + e.getMessage() + "\n");
            return false;
        }
    }

    private void saveSuppliersQuietly() {
        try {
            saveSuppliersToFile();
        } catch (IOException e) {
            System.err.print("Warning: Could not save suppliers: " + e.getMessage() + "\n");
        }
    }

    public void saveProductsToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (Product p : products) {
            lines.add(p.toCsvLine());
        }
        storage.saveLines(PRODUCTS_FILE, lines);
    }

    public boolean loadProductsFromFile() {
        try {
            List<String> lines = storage.readLines(PRODUCTS_FILE);
            if (lines.isEmpty()) {
                return false;
            }
            products.clear();
            for (String line : lines) {
                Product parsed = parseProductLine(line);
                if (parsed != null) {
                    products.add(parsed);
                }
            }
            return !products.isEmpty();
        } catch (IOException e) {
            System.err.print("Warning: Could not load products file: " + e.getMessage() + "\n");
            return false;
        }
    }

    private Product parseProductLine(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 8) {
            return null;
        }
        try {
            String category = parts[0].trim();
            String id = parts[1].trim();
            String name = parts[2].trim();
            double unitPrice = Double.parseDouble(parts[3].trim());
            int quantity = Integer.parseInt(parts[4].trim());
            int reorderLevel = Integer.parseInt(parts[5].trim());
            Supplier supplier = findSupplierByName(parts[6].trim());

            if ("Baking Tool".equalsIgnoreCase(category) || "Baking Tools".equalsIgnoreCase(category)) {
                int warrantyMonths = Integer.parseInt(parts[7].trim());
                return new BakingTools(id, name, unitPrice, quantity, reorderLevel, supplier, warrantyMonths);
            } else if ("Raw Ingredient".equalsIgnoreCase(category) || "Raw Ingredients".equalsIgnoreCase(category)) {
                if (parts.length < 9) {
                    return null;
                }
                String unitOfMeasure = parts[7].trim();
                String expirationDate = parts[8].trim();
                return new RawIngredient(id, name, unitPrice, quantity, reorderLevel, supplier, unitOfMeasure, expirationDate);
            } else if ("Packaging".equalsIgnoreCase(category) || "Packaging Item".equalsIgnoreCase(category) || "Packaging Items".equalsIgnoreCase(category)) {
                if (parts.length < 9) {
                    return null;
                }
                String dimensions = parts[7].trim();
                int piecesPerPack = Integer.parseInt(parts[8].trim());
                return new PackagingItems(id, name, unitPrice, quantity, reorderLevel, supplier, dimensions, piecesPerPack);
            }
        } catch (NumberFormatException | InvalidInputException e) {
            System.err.print("Skipping invalid row: " + line + "\n");
        }
        return null;
    }

    public void saveTransactionsToFile() throws IOException {
        List<String> lines = new ArrayList<>();
        for (StockTransaction tx : transactions) {
            lines.add(tx.toCsvLine());
        }
        storage.saveLines(TRANSACTIONS_FILE, lines);
    }

    public boolean loadTransactionsFromFile() {
        try {
            List<String> lines = storage.readLines(TRANSACTIONS_FILE);
            if (lines.isEmpty()) {
                return false;
            }
            transactions.clear();
            for (String line : lines) {
                StockTransaction tx = StockTransaction.fromCsvLine(line);
                if (tx != null) {
                    transactions.add(tx);
                }
            }
            return true;
        } catch (IOException e) {
            System.err.print("Warning: Could not load transactions file: " + e.getMessage() + "\n");
            return false;
        }
    }

    public void saveAll() throws IOException {
        saveSuppliersToFile();
        saveProductsToFile();
        saveTransactionsToFile();
    }

    private void saveAllQuietly() {
        try {
            saveAll();
        } catch (IOException e) {
            System.err.print("Warning: Could not save data: " + e.getMessage() + "\n");
        }
    }

    public List<Product> getProducts() {
        return products;
    }

    public List<Product> getAllProducts() {
        return products;
    }

    public List<StockTransaction> getTransactions() {
        return transactions;
    }

    public List<Supplier> getSuppliers() {
        return suppliers;
    }
}