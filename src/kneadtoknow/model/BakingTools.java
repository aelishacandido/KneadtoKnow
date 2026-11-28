package kneadtoknow.model;

public class BakingTools extends Product{
    
    public BakingTools (String productID, String productName,int quantity, double price) {
        super(productID, productName, quantity, price);
    }
    
    @Override
    public String getCategory() {
        return "Baking Tool";
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
