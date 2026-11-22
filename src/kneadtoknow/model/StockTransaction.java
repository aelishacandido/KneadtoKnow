package kneadtoknow.model;

import java.io.Serializable;

public class StockTransaction implements Serializable {

    private int transactionId, quantity; 
    private String transactionType, productId, timestamp;

    public StockTransaction(int transactionId, String transactionType, String productId, int quantity, String timestamp) {

        this.transactionId = transactionId;
        this.transactionType = transactionType;
        this.productId = productId;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
               "\nType: " + transactionType +
               "\nProduct ID: " + productId +
               "\nQuantity: " + quantity +
               "\nTimestamp: " + timestamp;
    }
}