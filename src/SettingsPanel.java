
import java.awt.Image;
import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class SettingsPanel extends javax.swing.JPanel {

    MainFrame mainFrame;

    int userId, selectedUserId;
    String userPassword;

    /**
     * Creates new form SettingsPanel
     */
    public SettingsPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        setThemeIcon(mainFrame.isDark);
        fetchUser();
        fetch();
    }

    public void setThemeIcon(boolean isDark) {
        String themeIconName = isDark ? "moon.png" : "sun.png";
        Image themeIconImg = new ImageIcon(getClass().getResource("/resources/" + themeIconName)).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        jLabel3.setText("");
        jLabel3.setIcon(new ImageIcon(themeIconImg));

        String backIconName = isDark ? "back-dark.png" : "back-light.png";
        Image backIconImg = new ImageIcon(getClass().getResource("/resources/" + backIconName)).getImage().getScaledInstance(35, 35, Image.SCALE_SMOOTH);
        jLabel1.setText("");
        jLabel1.setIcon(new ImageIcon(backIconImg));

        this.revalidate();
        this.repaint();
    }

    public void fetch() {
        mainFrame.Connect();
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT user_id, username FROM users_table ORDER BY user_id DESC");
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) usersTable.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                v2.add(rs.getString("user_id"));
                v2.add(rs.getString("username"));
                dtm.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Fetching Failed!\n" + ex.getLocalizedMessage());
        }
    }

    public void fetchUser() {
        String savedUser = mainFrame.prefs.get("rememberedUser", null);
        if (savedUser != null) {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT * FROM users_table WHERE username = ?");
                pstmt.setString(1, savedUser);

                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    changePasswordUsernameLabel.setText("Username: " + rs.getString("username"));
                    userId = rs.getInt("user_id");
                    userPassword = rs.getString("password");
                }
            } catch (SQLException ex) {
                Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Fetching User Failed!\n" + ex.getLocalizedMessage());
            }
        }
    }

    public void changePassword() {
        try {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All Field Must be Field Out!");
            } else {
                if (userPassword.equals(currentPassword)) {
                    if (newPassword.equals(confirmPassword)) {
                        // Insert into database
                        String sql = "UPDATE users_table SET password = ? WHERE user_id = ?";
                        PreparedStatement pstmt = mainFrame.connection.prepareStatement(sql);

                        pstmt.setString(1, newPassword);
                        pstmt.setInt(2, userId);
                        pstmt.executeUpdate();

                        currentPasswordField.setText("");
                        newPasswordField.setText("");
                        confirmPasswordField.setText("");

                        JOptionPane.showMessageDialog(this, "Change Password Successful!");
                    } else {
                        JOptionPane.showMessageDialog(this, "New and Confirm Password Must be Same!");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Current Password is Incorrect!");
                }
            }
        } catch (java.sql.SQLException ex) {
            Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Changing Failed!\n" + ex.getMessage());
        }
    }

    public void addUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All Field Must be Field Out!");
        } else {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("INSERT INTO users_table (admin, username, password) VALUES (1, ?, ?) ");
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.executeUpdate();

                fetch();
                clearSelectionButtonActionPerformed(null);

                JOptionPane.showMessageDialog(this, "New User Added Successful!");

            } catch (SQLException ex) {
                Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Adding Failed!\n" + ex.getLocalizedMessage());
            }
        }
    }

    public void updateUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        int row = usersTable.getSelectedRow();
        selectedUserId = Integer.parseInt(usersTable.getValueAt(row, 0).toString());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All Field Must be Field Out!");
        } else {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("UPDATE users_table SET username = ?, password = ? WHERE user_id = ?");
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                pstmt.setInt(3, selectedUserId);
                pstmt.executeUpdate();

                fetch();
                clearSelectionButtonActionPerformed(null);

                JOptionPane.showMessageDialog(this, "User Update Successful!");

            } catch (SQLException ex) {
                Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Upadating Failed!\n" + ex.getLocalizedMessage());
            }
        }
    }

    public void removeUser() {
        int row = usersTable.getSelectedRow();
        selectedUserId = Integer.parseInt(usersTable.getValueAt(row, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to Remove this User?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("DELETE FROM users_table WHERE user_id = ?");
                pstmt.setInt(1, selectedUserId);
                pstmt.executeUpdate();

                fetch();
                clearSelectionButtonActionPerformed(null);

                JOptionPane.showMessageDialog(this, "User Remove Successful!");

            } catch (SQLException ex) {
                Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Removing Failed!\n" + ex.getLocalizedMessage());
            }
        }
    }

    public static void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File f : files) {
                    deleteFolder(f); // delete inside files/subfolders
                }
            }
        }
        folder.delete(); // delete the folder or file
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        changePasswordPanel = new javax.swing.JPanel();
        changePasswordUsernameLabel = new javax.swing.JLabel();
        currentPasswordLabel = new javax.swing.JLabel();
        currentPasswordField = new javax.swing.JPasswordField();
        newPasswordLabel = new javax.swing.JLabel();
        newPasswordField = new javax.swing.JPasswordField();
        confirmPasswordLabel = new javax.swing.JLabel();
        confirmPasswordField = new javax.swing.JPasswordField();
        changeButton = new javax.swing.JButton();
        changePasswordShowPassword = new javax.swing.JCheckBox();
        manageUsersPanel = new javax.swing.JPanel();
        addUpdatePanel = new javax.swing.JPanel();
        addUpdateLabel = new javax.swing.JLabel();
        addUpdateUsernameLabel = new javax.swing.JLabel();
        usernameField = new javax.swing.JTextField();
        addUpdatePasswordLabel = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();
        manageUsersShowPassword = new javax.swing.JCheckBox();
        addButton = new javax.swing.JButton();
        tablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        usersTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        clearSelectionButton = new javax.swing.JButton();
        database = new javax.swing.JPanel();
        resetDatabaseButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        jPanel2.setMinimumSize(new java.awt.Dimension(800, 600));
        jPanel2.setPreferredSize(new java.awt.Dimension(1100, 800));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(800, 45));

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(".....");
        jLabel1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel1MouseClicked(evt);
            }
        });

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText(".....");
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 730, Short.MAX_VALUE)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        changePasswordPanel.setLayout(new java.awt.GridBagLayout());

        changePasswordUsernameLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        changePasswordUsernameLabel.setText("Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePasswordPanel.add(changePasswordUsernameLabel, gridBagConstraints);

        currentPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        currentPasswordLabel.setText("Current Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        changePasswordPanel.add(currentPasswordLabel, gridBagConstraints);

        currentPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        currentPasswordField.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePasswordPanel.add(currentPasswordField, gridBagConstraints);

        newPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        newPasswordLabel.setText("New Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        changePasswordPanel.add(newPasswordLabel, gridBagConstraints);

        newPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        newPasswordField.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePasswordPanel.add(newPasswordField, gridBagConstraints);

        confirmPasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        confirmPasswordLabel.setText("Confirm Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 5);
        changePasswordPanel.add(confirmPasswordLabel, gridBagConstraints);

        confirmPasswordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        confirmPasswordField.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        changePasswordPanel.add(confirmPasswordField, gridBagConstraints);

        changeButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        changeButton.setText("CHANGE");
        changeButton.setFocusable(false);
        changeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePasswordPanel.add(changeButton, gridBagConstraints);

        changePasswordShowPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        changePasswordShowPassword.setText("Show Passwords");
        changePasswordShowPassword.setFocusable(false);
        changePasswordShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePasswordShowPasswordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        changePasswordPanel.add(changePasswordShowPassword, gridBagConstraints);

        jTabbedPane1.addTab("Change Password", changePasswordPanel);

        manageUsersPanel.setLayout(new java.awt.GridLayout(1, 0));

        addUpdatePanel.setLayout(new java.awt.GridBagLayout());

        addUpdateLabel.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        addUpdateLabel.setText("Add/Update User");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        addUpdatePanel.add(addUpdateLabel, gridBagConstraints);

        addUpdateUsernameLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addUpdateUsernameLabel.setText("Username:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 5);
        addUpdatePanel.add(addUpdateUsernameLabel, gridBagConstraints);

        usernameField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        usernameField.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        addUpdatePanel.add(usernameField, gridBagConstraints);

        addUpdatePasswordLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        addUpdatePasswordLabel.setText("Password:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 5);
        addUpdatePanel.add(addUpdatePasswordLabel, gridBagConstraints);

        passwordField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        passwordField.setPreferredSize(new java.awt.Dimension(200, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        addUpdatePanel.add(passwordField, gridBagConstraints);

        manageUsersShowPassword.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        manageUsersShowPassword.setText("Show Password");
        manageUsersShowPassword.setFocusable(false);
        manageUsersShowPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageUsersShowPasswordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        addUpdatePanel.add(manageUsersShowPassword, gridBagConstraints);

        addButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        addButton.setText("ADD");
        addButton.setFocusable(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        addUpdatePanel.add(addButton, gridBagConstraints);

        manageUsersPanel.add(addUpdatePanel);

        tablePanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);

        usersTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        usersTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "User Id", "Username"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        usersTable.setFocusable(false);
        usersTable.setShowGrid(true);
        usersTable.setSurrendersFocusOnKeystroke(true);
        usersTable.getTableHeader().setReorderingAllowed(false);
        usersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                usersTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(usersTable);

        tablePanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel5.setPreferredSize(new java.awt.Dimension(400, 50));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        removeButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeButton.setText("REMOVE");
        removeButton.setEnabled(false);
        removeButton.setFocusable(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        jPanel5.add(removeButton, new java.awt.GridBagConstraints());

        updateButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        updateButton.setText("UPDATE");
        updateButton.setEnabled(false);
        updateButton.setFocusable(false);
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        jPanel5.add(updateButton, new java.awt.GridBagConstraints());

        clearSelectionButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        clearSelectionButton.setText("CLEAR SELECTION");
        clearSelectionButton.setEnabled(false);
        clearSelectionButton.setFocusable(false);
        clearSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearSelectionButtonActionPerformed(evt);
            }
        });
        jPanel5.add(clearSelectionButton, new java.awt.GridBagConstraints());

        tablePanel.add(jPanel5, java.awt.BorderLayout.SOUTH);

        manageUsersPanel.add(tablePanel);

        jTabbedPane1.addTab("Manage Users", manageUsersPanel);

        database.setLayout(new java.awt.GridBagLayout());

        resetDatabaseButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        resetDatabaseButton.setText("RESET DATABASE");
        resetDatabaseButton.setFocusable(false);
        resetDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDatabaseButtonActionPerformed(evt);
            }
        });
        database.add(resetDatabaseButton, new java.awt.GridBagConstraints());

        jTabbedPane1.addTab("Database", database);

        jPanel2.add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        add(jPanel2, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void jLabel1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel1MouseClicked
        mainFrame.buttonsPanel = null;
        mainFrame.buttonsPanel = new ButtonsPanel(mainFrame);
        mainFrame.add(mainFrame.buttonsPanel, java.awt.BorderLayout.WEST);
        mainFrame.switchPanel(new DashboardPanel(mainFrame), "Dashboard");
    }//GEN-LAST:event_jLabel1MouseClicked

    private void jLabel3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel3MouseClicked
        mainFrame.isDark = !mainFrame.isDark; // toggle true/false
        mainFrame.setTheme(mainFrame.isDark);
        setThemeIcon(mainFrame.isDark);
    }//GEN-LAST:event_jLabel3MouseClicked

    private void changeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeButtonActionPerformed
        changePassword();
    }//GEN-LAST:event_changeButtonActionPerformed

    private void changePasswordShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePasswordShowPasswordActionPerformed
        if (changePasswordShowPassword.isSelected()) {
            currentPasswordField.setEchoChar((char) 0);
            newPasswordField.setEchoChar((char) 0);
            confirmPasswordField.setEchoChar((char) 0);
        } else {
            currentPasswordField.setEchoChar('•');
            newPasswordField.setEchoChar('•');
            confirmPasswordField.setEchoChar('•');
        }
    }//GEN-LAST:event_changePasswordShowPasswordActionPerformed

    private void usersTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_usersTableMouseClicked
        addButton.setEnabled(false);
        removeButton.setEnabled(true);
        updateButton.setEnabled(true);
        clearSelectionButton.setEnabled(true);
        int row = usersTable.getSelectedRow();
        selectedUserId = Integer.parseInt(usersTable.getValueAt(row, 0).toString());
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT * FROM users_table WHERE user_id = ?");
            pstmt.setInt(1, selectedUserId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                usernameField.setText(rs.getString("username"));
                passwordField.setText(rs.getString("password"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Fetching User Failed!\n" + ex.getLocalizedMessage());
        }
    }//GEN-LAST:event_usersTableMouseClicked

    private void manageUsersShowPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageUsersShowPasswordActionPerformed
        if (manageUsersShowPassword.isSelected()) {
            passwordField.setEchoChar((char) 0);
        } else {
            passwordField.setEchoChar('•');
        }
    }//GEN-LAST:event_manageUsersShowPasswordActionPerformed

    private void clearSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearSelectionButtonActionPerformed
        addButton.setEnabled(true);
        removeButton.setEnabled(false);
        updateButton.setEnabled(false);
        clearSelectionButton.setEnabled(false);
        usersTable.clearSelection();
        usernameField.setText("");
        passwordField.setText("");
    }//GEN-LAST:event_clearSelectionButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        addUser();
    }//GEN-LAST:event_addButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        updateUser();
    }//GEN-LAST:event_updateButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        removeUser();
    }//GEN-LAST:event_removeButtonActionPerformed

    private void resetDatabaseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDatabaseButtonActionPerformed
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to RESET The DATABASE? This Cannot be Undone", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt;

                File photoFile = new File("photos/");
                deleteFolder(photoFile);
                File qrFile = new File("qrcodes/");
                deleteFolder(qrFile);

                System.out.println(photoFile.getName() + " and " + qrFile.getName() + " File Deleted");

                pstmt = mainFrame.connection.prepareStatement("DROP DATABASE employee_management_database");
                pstmt.executeUpdate();

                pstmt = mainFrame.connection.prepareStatement("CREATE DATABASE employee_management_database");
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "RESET Successful!");

                mainFrame.dispose();
                java.awt.EventQueue.invokeLater(() -> {
                    new MainFrame().setVisible(true);
                });
            } catch (SQLException ex) {
                Logger.getLogger(SettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Reset Failed!\n" + ex.getLocalizedMessage());
            }
        }
    }//GEN-LAST:event_resetDatabaseButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JLabel addUpdateLabel;
    private javax.swing.JPanel addUpdatePanel;
    private javax.swing.JLabel addUpdatePasswordLabel;
    private javax.swing.JLabel addUpdateUsernameLabel;
    private javax.swing.JButton changeButton;
    private javax.swing.JPanel changePasswordPanel;
    private javax.swing.JCheckBox changePasswordShowPassword;
    private javax.swing.JLabel changePasswordUsernameLabel;
    private javax.swing.JButton clearSelectionButton;
    private javax.swing.JPasswordField confirmPasswordField;
    private javax.swing.JLabel confirmPasswordLabel;
    private javax.swing.JPasswordField currentPasswordField;
    private javax.swing.JLabel currentPasswordLabel;
    private javax.swing.JPanel database;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel manageUsersPanel;
    private javax.swing.JCheckBox manageUsersShowPassword;
    private javax.swing.JPasswordField newPasswordField;
    private javax.swing.JLabel newPasswordLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JButton removeButton;
    private javax.swing.JButton resetDatabaseButton;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JButton updateButton;
    private javax.swing.JTextField usernameField;
    private javax.swing.JTable usersTable;
    // End of variables declaration//GEN-END:variables
}
