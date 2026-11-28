package kneadtoknow.inventory.main;

import java.awt.EventQueue;
import kneadtoknow.core.Inventory;
import kneadtoknow.gui.LoginFrame;

public class KK_inventory_app {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Inventory inventory = new Inventory();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setInventory(inventory);
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}