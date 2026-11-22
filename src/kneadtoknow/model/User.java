package kneadtoknow.model;

import java.io.Serializable;

/*
Abstract class representing a system user (Admin or Staff).
*/

public abstract class User implements Serializable {

    private String username, password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String input) {
        return this.password.equals(input);
    }

    /** Determines if the user role has permission to add, edit, or delete catalog items.
     ** Implemented differently by subclasses (true for Admin, false for Staff).
     */
    public abstract boolean canModifyCatalog();

    public abstract String getRoleName();
}
