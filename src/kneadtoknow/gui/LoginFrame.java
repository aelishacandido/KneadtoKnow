package kneadtoknow.gui;

// import kneadtoknow.core.Inventory; // will uncomment once Inventory is ready
import kneadtoknow.model.AdminUser;
import kneadtoknow.model.StaffUser;
import kneadtoknow.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());
    
    // private final Inventory inventory;
    private final List<User> users;
    
    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
        
        txtUsername.setText("");
        txtPassword.setText("");
        
        this.users = new ArrayList<>();
        this.users.add(new AdminUser("admin", "admin123"));
        this.users.add(new StaffUser("staff", "staff123"));
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter both username and password.",
                    "Missing Credentials",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username) && user.checkPassword(password)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Welcome, " + user.getUsername() + "!\n"
                        + "Role: " + user.getRoleName() + "\n"
                        + "Can Modify Catalog: " + user.canModifyCatalog(),
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE
                );

                // TODO: Once Inventory and InventoryGUI are ready, replace the popup above with:
                // new InventoryGUI(inventory, user).setVisible(true);
                // this.dispose();
                return;
            }
        }
        
        txtPassword.setText("");
        JOptionPane.showMessageDialog(
                this,
                "Invalid username or password. Please try again.",
                "Authentication Failed",
                JOptionPane.ERROR_MESSAGE
        );
    }
    
    private void handleCancel() {
        this.dispose();
        System.exit(0);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblLogin = new javax.swing.JLabel();
        lblUsername = new javax.swing.JLabel();
        sublblLogin = new javax.swing.JLabel();
        txtUsername = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        btnLogin = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Knead to Know - Login");
        setBackground(new java.awt.Color(249, 246, 240));
        setMaximumSize(new java.awt.Dimension(400, 300));

        lblLogin.setFont(new java.awt.Font("Bookman Old Style", 1, 18)); // NOI18N
        lblLogin.setText("Login");

        lblUsername.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        lblUsername.setText("Username:");

        sublblLogin.setFont(new java.awt.Font("Bookman Old Style", 0, 12)); // NOI18N
        sublblLogin.setText("Please sign in to continue.");

        txtUsername.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        txtUsername.setText("Enter your username");

        lblPassword.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        lblPassword.setText("Password:");

        txtPassword.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        txtPassword.setText("12345");
        txtPassword.addActionListener(this::txtPasswordActionPerformed);

        btnLogin.setFont(new java.awt.Font("Bookman Old Style", 0, 13)); // NOI18N
        btnLogin.setText("Login");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        btnCancel.setFont(new java.awt.Font("Bookman Old Style", 0, 13)); // NOI18N
        btnCancel.setText("Cancel");
        btnCancel.addActionListener(this::btnCancelActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sublblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblUsername, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                                .addGap(36, 36, 36)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                                    .addComponent(txtPassword)))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(btnLogin)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel)))
                .addContainerGap(33, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(lblLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sublblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUsername)
                    .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLogin)
                    .addComponent(btnCancel))
                .addGap(43, 43, 43))
        );

        lblLogin.getAccessibleContext().setAccessibleName("lblLogin");
        lblLogin.getAccessibleContext().setAccessibleDescription("");
        sublblLogin.getAccessibleContext().setAccessibleName("sublblLogin");

        getAccessibleContext().setAccessibleName("LoginFrame");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        handleLogin();
    }//GEN-LAST:event_btnLoginActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        handleCancel();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        handleLogin();
    }//GEN-LAST:event_txtPasswordActionPerformed
 
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnLogin;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel sublblLogin;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
