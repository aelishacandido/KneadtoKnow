package kneadtoknow.inventory.main;

import java.awt.EventQueue;
import kneadtoknow.gui.LoginFrame;

public class KK_inventory_app {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setLocationRelativeTo(null);
            loginFrame.setVisible(true);
        });
    }
}