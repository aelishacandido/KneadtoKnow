package kneadtoknow.model;

public class RawIngredient extends Product {

    public RawIngredient(String productID, String productName,int quantity, double price) {
        super(productID, productName, quantity, price);
    }

    @Override
    public String getCategory() {
        return "Raw Ingredient";
    }
    
    @Override
    public String getAlertStatus() {
    if (quantity < 10) {
        return "Low Stock";
    } else {
        return "In Stock";
    }
    }
    @Override
    public boolean isAlertTriggered() {
    return quantity < 10;
    }
}