package kneadtoknow.gui;

// import kneadtoknow.core.Inventory; // will uncomment once Inventory is ready
import kneadtoknow.model.AdminUser;
import kneadtoknow.model.StaffUser;
import kneadtoknow.model.User;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class LoginFrame extends javax.swing.JFrame {
        
    // private final Inventory inventory;
    private final List<User> users;
    
    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btnLogin);
        
        txtUsername.setText("");
        txtPassword.setText("");
        
        this.users = new ArrayList<>();
        this.users.add(new AdminUser("admin", "admin123"));
        this.users.add(new StaffUser("staff", "staff123"));
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || username.equals("Username")
            || password.isEmpty() || password.equals("Password")) {
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
        chkShowPassword = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Knead to Know - Login");
        setBackground(new java.awt.Color(249, 246, 240));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMaximumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(395, 320));
        setResizable(false);

        lblLogin.setFont(new java.awt.Font("Bookman Old Style", 1, 18)); // NOI18N
        lblLogin.setForeground(new java.awt.Color(35, 35, 35));
        lblLogin.setText("Login");

        lblUsername.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        lblUsername.setText("Username:");

        sublblLogin.setFont(new java.awt.Font("Bookman Old Style", 0, 12)); // NOI18N
        sublblLogin.setForeground(new java.awt.Color(140, 140, 140));
        sublblLogin.setText("Sign in to manage baking suppiles stock & sales.");

        txtUsername.setBackground(new java.awt.Color(255, 255, 255));
        txtUsername.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        txtUsername.setForeground(new java.awt.Color(62, 39, 35));
        txtUsername.setText("Enter your username");
        txtUsername.setToolTipText("");
        txtUsername.setPreferredSize(new java.awt.Dimension(210, 32));

        lblPassword.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        lblPassword.setText("Password:");

        txtPassword.setBackground(new java.awt.Color(255, 255, 255));
        txtPassword.setFont(new java.awt.Font("Baskerville Old Face", 0, 14)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(62, 39, 35));
        txtPassword.setText("12345");
        txtPassword.setPreferredSize(new java.awt.Dimension(210, 32));
        txtPassword.addActionListener(this::txtPasswordActionPerformed);

        btnLogin.setBackground(new java.awt.Color(74, 44, 24));
        btnLogin.setFont(new java.awt.Font("Bookman Old Style", 0, 13)); // NOI18N
        btnLogin.setForeground(new java.awt.Color(255, 255, 255));
        btnLogin.setText("Login");
        btnLogin.addActionListener(this::btnLoginActionPerformed);

        chkShowPassword.setFont(new java.awt.Font("Baskerville Old Face", 0, 12)); // NOI18N
        chkShowPassword.setText("Show Password");
        chkShowPassword.addActionListener(this::chkShowPasswordActionPerformed);

        jLabel1.setFont(new java.awt.Font("Baskerville Old Face", 0, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(140, 140, 140));
        jLabel1.setText("Need access? Contact your store Admin");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sublblLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(chkShowPassword)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(lblPassword, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblUsername, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                            .addGap(36, 36, 36)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(18, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkShowPassword)
                .addGap(18, 18, 18)
                .addComponent(btnLogin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addContainerGap(33, Short.MAX_VALUE))
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

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        handleLogin();
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void chkShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkShowPasswordActionPerformed
        if (chkShowPassword.isSelected()) {
        txtPassword.setEchoChar((char) 0);
    } else {
        txtPassword.setEchoChar('•');
    }
    }//GEN-LAST:event_chkShowPasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JCheckBox chkShowPassword;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblLogin;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUsername;
    private javax.swing.JLabel sublblLogin;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}
