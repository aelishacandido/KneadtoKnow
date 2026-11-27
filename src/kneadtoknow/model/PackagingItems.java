package kneadtoknow.model;

import kneadtoknow.exception.InvalidInputException;

public class PackagingItems extends Product {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private String dimensions;
    private int piecesPerPack;

    public PackagingItems(String id, String name, double unitPrice, int quantity, int reorderLevel,
            Supplier supplier, String dimensions, int piecesPerPack) throws InvalidInputException {
        super(id, name, unitPrice, quantity, reorderLevel, supplier);
        this.dimensions = dimensions;
        this.piecesPerPack = piecesPerPack;
    }

    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) throws InvalidInputException {
        if (dimensions == null || dimensions.trim().isEmpty()) {
            throw new InvalidInputException("Dimensions cannot be empty.");
        }
        this.dimensions = dimensions.trim();
    }

    public int getPiecesPerPack() {
        return piecesPerPack;
    }

    public void setPiecesPerPack(int piecesPerPack) throws InvalidInputException {
        if (piecesPerPack <= 0) {
            throw new InvalidInputException("Pieces per pack must be greater than zero.");
        }
        this.piecesPerPack = piecesPerPack;
    }

    @Override
    public String getCategory() {
        return "Packaging Item";
    }

    @Override
    public String getCategoryDetails() {
        return "Size: " + dimensions + " | " + piecesPerPack + " pcs/pack";
    }
    
    @Override
    public void updateCategoryField(String newFieldValue) throws InvalidInputException {
        setDimensions(newFieldValue);
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
        return baseCsvFields() + "," + escapeCsv(dimensions) + "," + piecesPerPack;
    }
}