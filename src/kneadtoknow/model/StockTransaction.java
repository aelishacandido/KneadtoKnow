package kneadtoknow.model;

import kneadtoknow.exception.InvalidInputException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class StockTransaction {

    private static final AtomicInteger ID_COUNTER = new AtomicInteger(1000);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int transactionId;
    private String type;
    private String productId;
    private String productName;
    private int quantityChanged;
    private String timestamp;

    public StockTransaction(String type, String productId, String productName, int quantityChanged)
            throws InvalidInputException {
        this(ID_COUNTER.incrementAndGet(), type, productId, productName, quantityChanged,
                LocalDateTime.now().format(FORMATTER));
    }

    public StockTransaction(int transactionId, String type, String productId, String productName,
            int quantityChanged, String timestamp) throws InvalidInputException {
        if (transactionId <= 0) {
            throw new InvalidInputException("Transaction ID must be greater than zero.");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new InvalidInputException("Transaction type cannot be empty.");
        }
        if (productId == null || productId.trim().isEmpty()) {
            throw new InvalidInputException("Product ID cannot be empty.");
        }
        if (productName == null || productName.trim().isEmpty()) {
            throw new InvalidInputException("Product name cannot be empty.");
        }
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new InvalidInputException("Timestamp cannot be empty.");
        }

        this.transactionId = transactionId;
        this.type = type.trim();
        this.productId = productId.trim();
        this.productName = productName.trim();
        this.quantityChanged = quantityChanged;
        this.timestamp = timestamp.trim();

        if (transactionId >= ID_COUNTER.get()) {
            ID_COUNTER.set(transactionId);
        }
    }

    public int getTransactionId() {
        return transactionId;
    }

    public String getType() {
        return type;
    }

    public String getTransactionType() {
        return type;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantityChanged() {
        return quantityChanged;
    }

    public int getQuantity() {
        return quantityChanged;
    }

    public String getTimestamp() {
        return timestamp;
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(",", " ").trim();
    }

    public String toCsvLine() {
        return String.join(",",
                String.valueOf(transactionId),
                escapeCsv(type),
                escapeCsv(productId),
                escapeCsv(productName),
                String.valueOf(quantityChanged),
                escapeCsv(timestamp)
        );
    }

    public static StockTransaction fromCsvLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        String[] parts = line.split(",", -1);
        if (parts.length < 6) {
            return null;
        }
        try {
            int txId = Integer.parseInt(parts[0].trim());
            String txType = parts[1].trim();
            String prodId = parts[2].trim();
            String prodName = parts[3].trim();
            int qty = Integer.parseInt(parts[4].trim());
            String time = parts[5].trim();
            return new StockTransaction(txId, txType, prodId, prodName, qty, time);
        } catch (NumberFormatException | InvalidInputException e) {
            System.err.println("Skipping invalid transaction row: " + line);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId
                + "\nType: " + type
                + "\nProduct ID: " + productId
                + "\nProduct Name: " + productName
                + "\nQuantity: " + quantityChanged
                + "\nTimestamp: " + timestamp;
    }
}