package kneadtoknow.model;

import java.io.Serializable;

public class Supplier {

    private String supplierId, name, contact;

    public Supplier(String supplierId, String name, String contact) {
        this.supplierId = supplierId;
        this.name = name;
        this.contact = contact;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String toCsvLine() {
        return supplierId + "," + name + "," + contact;
    }

    @Override
    public String toString() {
        return supplierId + " - " + name;
    }
}