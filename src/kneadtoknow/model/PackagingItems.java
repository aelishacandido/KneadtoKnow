package kneadtoknow.model;

public class PackagingItems extends Product{
    
    public PackagingItems (String productID, String productName,int quantity, double price) {
        super(productID, productName, quantity, price);
    }
    
    @Override
    public String getCategory() {
        return "Packaging Item";
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
