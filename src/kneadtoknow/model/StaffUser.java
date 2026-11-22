package kneadtoknow.model;

public class StaffUser extends User {

    public StaffUser(String u, String p) {
        super(u, p);
    }

    @Override
    public boolean canModifyCatalog() {
        return false;
    }

    @Override
    public String getRoleName() {
        return "Staff";
    }
}
