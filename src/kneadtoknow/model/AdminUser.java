package kneadtoknow.model;

public class AdminUser extends User {

    public AdminUser(String u, String p) {
        super(u, p);
    }

    @Override
    public boolean canModifyCatalog() {
        return true;
    }

    @Override
    public String getRoleName() {
        return "Admin";
    }
}
