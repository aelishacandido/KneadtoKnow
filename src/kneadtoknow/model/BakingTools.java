package kneadtoknow.model;

import kneadtoknow.exception.InvalidInputException;

public class BakingTools extends Product {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private int warrantyMonths;

    public BakingTools(String id, String name, double unitPrice, int quantity, int reorderLevel,
            Supplier supplier, int warrantyMonths) throws InvalidInputException {
        super(id, name, unitPrice, quantity, reorderLevel, supplier);
        this.warrantyMonths = warrantyMonths;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) throws InvalidInputException {
        if (warrantyMonths < 0) {
            throw new InvalidInputException("Warranty months cannot be negative.");
        }
        this.warrantyMonths = warrantyMonths;
    }

    @Override
    public String getCategory() {
        return "Baking Tool";
    }

    @Override
    public String getCategoryDetails() {
        return "Warranty: " + warrantyMonths + " months";
    }

    @Override
    public void updateCategoryField(String newFieldValue) throws InvalidInputException {
        if (newFieldValue == null || newFieldValue.trim().isEmpty()) {
            throw new InvalidInputException("Warranty months cannot be empty.");
        }
        try {
            int months = Integer.parseInt(newFieldValue.trim());
            setWarrantyMonths(months);
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Warranty months must be a valid whole number.");
        }
    }

    @Override
    public boolean isAlertTriggered() {
        return getQuantity() <= LOW_STOCK_THRESHOLD;
    }

    @Override
    public String getAlertStatus() {
        if (isAlertTriggered()) {
            return "LOW stock";
        }
        return "IN stock";
    }

    @Override
    public String toCsvLine() {
        return baseCsvFields() + "," + warrantyMonths;
    }
}