package kneadtoknow.model;

import kneadtoknow.exception.InvalidInputException;

public class RawIngredient extends Product {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private String unitOfMeasure;
    private String expirationDate;

    public RawIngredient(String id, String name, double unitPrice, int quantity, int reorderLevel,
            Supplier supplier, String unitOfMeasure, String expirationDate) throws InvalidInputException {
        super(id, name, unitPrice, quantity, reorderLevel, supplier);
        this.unitOfMeasure = unitOfMeasure;
        this.expirationDate = expirationDate;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) throws InvalidInputException {
        if (unitOfMeasure == null || unitOfMeasure.trim().isEmpty()) {
            throw new InvalidInputException("Unit of measure cannot be empty.");
        }
        this.unitOfMeasure = unitOfMeasure.trim();
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(String expirationDate) throws InvalidInputException {
        if (expirationDate == null || expirationDate.trim().isEmpty()) {
            throw new InvalidInputException("Expiration date cannot be empty.");
        }
        this.expirationDate = expirationDate.trim();
    }

    @Override
    public String getCategory() {
        return "Raw Ingredient";
    }

    @Override
    public String getCategoryDetails() {
        return "Unit: " + unitOfMeasure + " | Exp: " + expirationDate;
    }
    
    @Override
    public void updateCategoryField(String newFieldValue) throws InvalidInputException {
        setUnitOfMeasure(newFieldValue);
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
        return baseCsvFields() + "," + escapeCsv(unitOfMeasure) + "," + escapeCsv(expirationDate);
    }
}