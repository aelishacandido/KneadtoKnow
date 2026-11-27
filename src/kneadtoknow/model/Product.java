package kneadtoknow.model;

import kneadtoknow.exception.InvalidInputException;

public abstract class Product implements Auditable {

    private String id;
    private String name;
    private double unitPrice;
    private int quantity;
    private int reorderLevel;
    private Supplier supplier;

    public Product(String id, String name, double unitPrice, int quantity, int reorderLevel, Supplier supplier)
            throws InvalidInputException {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
        this.supplier = supplier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) throws InvalidInputException {
        if (id == null || id.trim().isEmpty()) {
            throw new InvalidInputException("Product ID cannot be empty.");
        }
        this.id = id.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidInputException {
        if (name == null || name.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        this.name = name.trim();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) throws InvalidInputException {
        if (unitPrice < 0) {
            throw new InvalidInputException("Unit price cannot be negative.");
        }
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) throws InvalidInputException {
        if (quantity < 0) {
            throw new InvalidInputException("Quantity cannot be negative.");
        }
        this.quantity = quantity;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }

    public void setReorderLevel(int reorderLevel) throws InvalidInputException {
        if (reorderLevel < 0) {
            throw new InvalidInputException("Reorder level cannot be negative.");
        }
        this.reorderLevel = reorderLevel;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public void stockIn(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Stock-in amount must be greater than zero.");
        }
        setQuantity(this.quantity + amount);
    }

    public void stockOut(int amount) throws InvalidInputException {
        if (amount <= 0) {
            throw new InvalidInputException("Stock-out amount must be greater than zero.");
        }
        if (amount > this.quantity) {
            throw new InvalidInputException(
                    "Insufficient stock: requested " + amount + ", available " + this.quantity + "."
            );
        }
        setQuantity(this.quantity - amount);
    }

    public double calculateInventoryValue() {
        return unitPrice * quantity;
    }

    public boolean isBelowReorderLevel() {
        return quantity <= reorderLevel;
    }

    protected String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ").trim();
    }

    protected String baseCsvFields() {
        String supplierName = "";
        if (supplier != null) {
            supplierName = escapeCsv(supplier.getName());
        }

        return String.join(",",
                escapeCsv(getCategory()),
                escapeCsv(id),
                escapeCsv(name),
                String.valueOf(unitPrice),
                String.valueOf(quantity),
                String.valueOf(reorderLevel),
                supplierName
        );
    }

    public abstract String getCategory();

    public abstract String getCategoryDetails();

    public abstract void updateCategoryField(String newFieldValue) throws InvalidInputException;

    public abstract boolean isAlertTriggered();

    public abstract String getAlertStatus();

    public abstract String toCsvLine();
}