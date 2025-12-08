
import java.awt.HeadlessException;
import java.awt.Image;
import java.io.File;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class ViewEmployeesPanel extends javax.swing.JPanel {

    MainFrame mainFrame;

    int row, id;

    /**
     * Creates new form ViewEmployeesPanel
     */
    public ViewEmployeesPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        fetch();

        javax.swing.SwingUtilities.invokeLater(() -> searchField.requestFocusInWindow());

        searchField = (JTextField) searchComboBox.getEditor().getEditorComponent();
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                searchAction();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                searchAction();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                searchAction();
            }

            private void searchAction() {
                String text = searchField.getText().trim();

                try {
                    String sql = "SELECT * FROM employees_table WHERE employee_id LIKE ? OR full_name LIKE ? OR department LIKE ? ORDER BY employee_id DESC";
                    PreparedStatement pstmt = mainFrame.connection.prepareStatement(sql);
                    pstmt.setString(1, "%" + text + "%");
                    pstmt.setString(2, "%" + text + "%");
                    pstmt.setString(3, "%" + text + "%");
                    ResultSet rs = pstmt.executeQuery();

                    DefaultTableModel dtm = (DefaultTableModel) employeesTable.getModel();
                    dtm.setRowCount(0);
                    while (rs.next()) {
                        Vector v2 = new Vector();
                        v2.add(rs.getString("employee_id"));
                        v2.add(rs.getString("full_name"));
                        v2.add(rs.getString("department"));
                        dtm.addRow(v2);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Searching Failed!\n" + ex.getLocalizedMessage());
                }
            }
        });

        centerPanel.remove(profilePanel);
    }

    public void fetch() {
        mainFrame.Connect();
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT employee_id, full_name, department FROM employees_table ORDER BY employee_id DESC");
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) employeesTable.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                v2.add(rs.getString("employee_id"));
                v2.add(rs.getString("full_name"));
                v2.add(rs.getString("department"));
                dtm.addRow(v2);
            }
            loadComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Fetching Failed!\n" + ex.getLocalizedMessage());
        }
    }

    public void loadComboBox() {
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT DISTINCT department FROM employees_table ORDER BY position ASC");
            ResultSet rs = pstmt.executeQuery();
            searchComboBox.removeAllItems();
            while (rs.next()) {
                searchComboBox.addItem(rs.getString("department"));
            }
            searchComboBox.setSelectedIndex(-1);
        } catch (SQLException ex) {
            Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "LoadComboBox Failed!\n" + ex.getMessage());
        }
    }

    public void tableClicked() {
        row = employeesTable.getSelectedRow();
        id = Integer.parseInt(employeesTable.getValueAt(row, 0).toString());
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT * FROM employees_table WHERE employee_id = ?");
            pstmt.setInt(1, id);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                centerPanel.add(profilePanel, java.awt.BorderLayout.WEST);
                File selectedFile = new File(rs.getString("photo_path"));
                String photoPath = selectedFile.getAbsolutePath();

                photoLabel.setIcon(new ImageIcon(new ImageIcon(photoPath).getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)));
                photoLabel.setText("");

                employeeIdLabel.setText(rs.getString("employee_id"));
                nameLabel.setText(rs.getString("full_name"));
            }

            this.revalidate();
            this.repaint();
        } catch (Exception ex) {
            Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Fetching Employee Failed!\n" + ex.getLocalizedMessage());
        }
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

        popupMenu = new javax.swing.JPopupMenu();
        removeMenuItem = new javax.swing.JMenuItem();
        updateMenuItem = new javax.swing.JMenuItem();
        viewDetailsMenuItem = new javax.swing.JMenuItem();
        generatePayslipMenuItem = new javax.swing.JMenuItem();
        searchField = new javax.swing.JTextField();
        centerPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchComboBox = new javax.swing.JComboBox<>();
        tableScrollPane = new javax.swing.JScrollPane();
        employeesTable = new javax.swing.JTable();
        profilePanel = new javax.swing.JPanel();
        photoLabel = new javax.swing.JLabel();
        employeeIdLabel = new javax.swing.JLabel();
        nameLabel = new javax.swing.JLabel();
        updateButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        clearButton = new javax.swing.JButton();

        removeMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        removeMenuItem.setText("Remove");
        removeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(removeMenuItem);

        updateMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        updateMenuItem.setText("Update");
        updateMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(updateMenuItem);

        viewDetailsMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        viewDetailsMenuItem.setText("View Details");
        viewDetailsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewDetailsMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(viewDetailsMenuItem);

        generatePayslipMenuItem.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        generatePayslipMenuItem.setText("Generate Payslip");
        generatePayslipMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatePayslipMenuItemActionPerformed(evt);
            }
        });
        popupMenu.add(generatePayslipMenuItem);

        popupMenu.getAccessibleContext().setAccessibleParent(employeesTable);

        searchField.setBackground(new java.awt.Color(255, 255, 255));
        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchField.setForeground(new java.awt.Color(0, 0, 0));
        searchField.setMinimumSize(new java.awt.Dimension(150, 30));
        searchField.setPreferredSize(new java.awt.Dimension(400, 30));

        setPreferredSize(new java.awt.Dimension(1000, 500));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setMinimumSize(new java.awt.Dimension(800, 600));
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        centerPanel.setLayout(new java.awt.BorderLayout());

        searchPanel.setMinimumSize(new java.awt.Dimension(500, 50));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new java.awt.Dimension(500, 50));
        searchPanel.setLayout(new java.awt.GridBagLayout());

        searchLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        searchLabel.setText("Search:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        searchPanel.add(searchLabel, gridBagConstraints);

        searchComboBox.setEditable(true);
        searchComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchComboBox.setMinimumSize(new java.awt.Dimension(400, 30));
        searchComboBox.setPreferredSize(new java.awt.Dimension(400, 30));
        searchPanel.add(searchComboBox, new java.awt.GridBagConstraints());

        centerPanel.add(searchPanel, java.awt.BorderLayout.NORTH);

        tableScrollPane.setMinimumSize(new java.awt.Dimension(800, 400));
        tableScrollPane.setPreferredSize(new java.awt.Dimension(900, 500));

        employeesTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        employeesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Employee ID", "Name", "Department"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        employeesTable.setToolTipText("Click or Right-Click to Show Menu");
        employeesTable.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        employeesTable.setFocusable(false);
        employeesTable.setRowHeight(25);
        employeesTable.setShowGrid(true);
        employeesTable.getTableHeader().setReorderingAllowed(false);
        employeesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                employeesTableMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                employeesTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                employeesTableMouseReleased(evt);
            }
        });
        tableScrollPane.setViewportView(employeesTable);

        centerPanel.add(tableScrollPane, java.awt.BorderLayout.CENTER);

        profilePanel.setPreferredSize(new java.awt.Dimension(230, 10));
        profilePanel.setLayout(new java.awt.GridBagLayout());

        photoLabel.setBackground(new java.awt.Color(255, 255, 255));
        photoLabel.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        photoLabel.setForeground(new java.awt.Color(0, 0, 0));
        photoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        photoLabel.setText("PHOTO");
        photoLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(153, 153, 153)));
        photoLabel.setMinimumSize(new java.awt.Dimension(100, 100));
        photoLabel.setOpaque(true);
        photoLabel.setPreferredSize(new java.awt.Dimension(150, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        profilePanel.add(photoLabel, gridBagConstraints);

        employeeIdLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        employeeIdLabel.setText("Employee ID");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        profilePanel.add(employeeIdLabel, gridBagConstraints);

        nameLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        nameLabel.setText("NAME");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 30, 0);
        profilePanel.add(nameLabel, gridBagConstraints);

        updateButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        updateButton.setText("UPDATE");
        updateButton.setPreferredSize(new java.awt.Dimension(100, 30));
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        profilePanel.add(updateButton, gridBagConstraints);

        viewButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        viewButton.setText("VIEW");
        viewButton.setToolTipText("View Details");
        viewButton.setPreferredSize(new java.awt.Dimension(100, 30));
        viewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        profilePanel.add(viewButton, gridBagConstraints);

        removeButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        removeButton.setText("REMOVE");
        removeButton.setPreferredSize(new java.awt.Dimension(100, 30));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        profilePanel.add(removeButton, gridBagConstraints);

        clearButton.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        clearButton.setText("CLEAR");
        clearButton.setToolTipText("Clear Selection");
        clearButton.setPreferredSize(new java.awt.Dimension(100, 30));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        profilePanel.add(clearButton, gridBagConstraints);

        centerPanel.add(profilePanel, java.awt.BorderLayout.WEST);

        add(centerPanel, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void employeesTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeesTableMouseClicked
        tableClicked();
    }//GEN-LAST:event_employeesTableMouseClicked

    private void removeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMenuItemActionPerformed
        if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to Remove this Employee?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT photo_path FROM employees_table WHERE employee_id = ?");
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    File photoFile = new File(rs.getString("photo_path"));
                    File qrFile = new File("qrcodes/" + id + ".png");
                    if (photoFile.delete() && qrFile.delete()) {
                        System.out.println(photoFile.getName() + " and " + qrFile.getName() + " File Deleted");
                    }

                    pstmt = mainFrame.connection.prepareStatement("DELETE FROM employees_table WHERE employee_id = ?");
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();

                    fetch();

                    JOptionPane.showMessageDialog(this, "Employee Removed!");

                }
            } catch (HeadlessException | SQLException ex) {
                Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Removing Employee Failed! \n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_removeMenuItemActionPerformed

    private void updateMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateMenuItemActionPerformed

        mainFrame.remove(mainFrame.buttonsPanel);
        mainFrame.switchPanel(new UpdatePanel(mainFrame, id), "Update Panel");

    }//GEN-LAST:event_updateMenuItemActionPerformed

    private void viewDetailsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewDetailsMenuItemActionPerformed

        mainFrame.remove(mainFrame.buttonsPanel);
        mainFrame.switchPanel(new ViewDetailsPanel(mainFrame, id), "View Details Panel");

    }//GEN-LAST:event_viewDetailsMenuItemActionPerformed

    private void generatePayslipMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePayslipMenuItemActionPerformed

        mainFrame.remove(mainFrame.buttonsPanel);
        mainFrame.switchPanel(new GeneratePayslipPanel(mainFrame, id), "Generate Payroll Panel");

    }//GEN-LAST:event_generatePayslipMenuItemActionPerformed

    private void employeesTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeesTableMousePressed
        row = employeesTable.rowAtPoint(evt.getPoint());
        id = Integer.parseInt(employeesTable.getValueAt(row, 0).toString());
        employeesTable.setRowSelectionInterval(row, row);

        if (evt.isPopupTrigger()) {
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        tableClicked();
    }//GEN-LAST:event_employeesTableMousePressed

    private void employeesTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_employeesTableMouseReleased
        row = employeesTable.rowAtPoint(evt.getPoint());
        id = Integer.parseInt(employeesTable.getValueAt(row, 0).toString());
        employeesTable.setRowSelectionInterval(row, row);

        if (evt.isPopupTrigger()) {
            popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
        tableClicked();
    }//GEN-LAST:event_employeesTableMouseReleased

    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        employeesTable.clearSelection();
        centerPanel.remove(profilePanel);

        this.revalidate();
        this.repaint();
    }//GEN-LAST:event_clearButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (JOptionPane.showConfirmDialog(this,
                "Are you sure you want to Remove this Employee?", "Confirmation",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT photo_path FROM employees_table WHERE employee_id = ?");
                pstmt.setInt(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    File photoFile = new File(rs.getString("photo_path"));
                    File qrFile = new File("qrcodes/" + id + ".png");
                    if (photoFile.delete() && qrFile.delete()) {
                        System.out.println(photoFile.getName() + " and " + qrFile.getName() + " File Deleted");
                    } else {
                        System.out.println(photoFile.getName() + " and " + qrFile.getName() + " File Delete Failed, please check the folders");
                        JOptionPane.showMessageDialog(this, "File Delete Failed, please check the folders" + photoFile.getName() + " and " + qrFile.getName());
                    }

                    pstmt = mainFrame.connection.prepareStatement("DELETE FROM employees_table WHERE employee_id = ?");
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();

                    fetch();

                    centerPanel.remove(profilePanel);

                    this.revalidate();
                    this.repaint();

                    JOptionPane.showMessageDialog(this, "Employee Removed!");

                }
            } catch (HeadlessException | SQLException ex) {
                Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(this, "Removing Employee Failed! \n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed

        mainFrame.remove(mainFrame.buttonsPanel);
        mainFrame.switchPanel(new UpdatePanel(mainFrame, id), "Update Panel");

    }//GEN-LAST:event_updateButtonActionPerformed

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_viewButtonActionPerformed

        mainFrame.remove(mainFrame.buttonsPanel);
        mainFrame.switchPanel(new ViewDetailsPanel(mainFrame, id), "View Details Panel");

    }//GEN-LAST:event_viewButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JLabel employeeIdLabel;
    private javax.swing.JTable employeesTable;
    private javax.swing.JMenuItem generatePayslipMenuItem;
    private javax.swing.JLabel nameLabel;
    private javax.swing.JLabel photoLabel;
    private javax.swing.JPopupMenu popupMenu;
    private javax.swing.JPanel profilePanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JMenuItem removeMenuItem;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JPanel searchPanel;
    private javax.swing.JScrollPane tableScrollPane;
    private javax.swing.JButton updateButton;
    private javax.swing.JMenuItem updateMenuItem;
    private javax.swing.JButton viewButton;
    private javax.swing.JMenuItem viewDetailsMenuItem;
    // End of variables declaration//GEN-END:variables
}
