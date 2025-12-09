
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class AttendanceListPanel extends javax.swing.JPanel {

    LocalDate today = LocalDate.now();
    MainFrame mainFrame;

    /**
     * Creates new form Attendance
     */
    public AttendanceListPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        initComponents();
        fetch();
        
        javax.swing.SwingUtilities.invokeLater(() -> searchField.requestFocusInWindow());

        DocumentListener genListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchAction();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchAction();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchAction();
            }
        };

        searchField = (JTextField) searchComboBox.getEditor().getEditorComponent();
        searchField.getDocument().addDocumentListener(genListener);
        
    }

    private void searchDateAction() {
        String text = dateComboBox.getSelectedItem().toString().trim();

        try {
            String sql = "SELECT * FROM employees_table "
                    + "LEFT JOIN attendance_table ON employees_table.employee_id = attendance_table.employee_id "
                    + "WHERE attendance_table.date LIKE ? "
                    + "ORDER BY attendance_table.date DESC";
            PreparedStatement pstmt = mainFrame.connection.prepareStatement(sql);
            pstmt.setString(1, "%" + text + "%");
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) attendanceTable.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                v2.add(rs.getString("employee_id"));
                v2.add(rs.getString("full_name"));
                v2.add(rs.getString("date"));
                v2.add(rs.getString("time_in"));
                v2.add(rs.getString("time_out"));
                dtm.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceListPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Searching Failed!\n" + ex.getLocalizedMessage());
        }
    }

    private void searchAction() {
        String text = searchField.getText().trim();

        try {
            String sql = "SELECT * FROM employees_table "
                    + "LEFT JOIN attendance_table ON employees_table.employee_id = attendance_table.employee_id "
                    + "WHERE employees_table.employee_id LIKE ? "
                    + "OR employees_table.full_name LIKE ? "
                    + "OR attendance_table.date LIKE ? "
                    + "ORDER BY attendance_table.date DESC";
            PreparedStatement pstmt = mainFrame.connection.prepareStatement(sql);
            pstmt.setString(1, "%" + text + "%");
            pstmt.setString(2, "%" + text + "%");
            pstmt.setString(3, "%" + text + "%");
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) attendanceTable.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                v2.add(rs.getString("employee_id"));
                v2.add(rs.getString("full_name"));
                v2.add(rs.getString("date"));
                v2.add(rs.getString("time_in"));
                v2.add(rs.getString("time_out"));
                dtm.addRow(v2);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceListPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Searching Failed!\n" + ex.getLocalizedMessage());
        }
    }

    public void fetch() {
        mainFrame.Connect();
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT employees_table.employee_id, employees_table.full_name, attendance_table.date, attendance_table.time_in, attendance_table.time_out "
                    + "FROM employees_table LEFT JOIN attendance_table ON employees_table.employee_id = attendance_table.employee_id ORDER BY attendance_id DESC");
            ResultSet rs = pstmt.executeQuery();

            DefaultTableModel dtm = (DefaultTableModel) attendanceTable.getModel();
            dtm.setRowCount(0);
            while (rs.next()) {
                Vector v2 = new Vector();
                v2.add(rs.getString("employee_id"));
                v2.add(rs.getString("full_name"));
                v2.add(rs.getString("date"));
                v2.add(rs.getString("time_in"));
                v2.add(rs.getString("time_out"));
                dtm.addRow(v2);
            }
            loadComboBox();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceListPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Fetching Failed!\n" + ex.getLocalizedMessage());
        }
    }

    public void loadComboBox() {
        try {
            PreparedStatement pstmt = mainFrame.connection.prepareStatement("SELECT DISTINCT date FROM attendance_table ORDER BY date DESC");
            ResultSet rs = pstmt.executeQuery();
            searchComboBox.removeAllItems();
            dateComboBox.removeAllItems();
            while (rs.next()) {
                searchComboBox.addItem(rs.getString(1));
                dateComboBox.addItem(rs.getString(1));
            }
            searchComboBox.setSelectedIndex(-1);
            dateComboBox.setSelectedItem(today.toString());
            dateComboBox.addItem("");
        } catch (SQLException ex) {
            Logger.getLogger(ViewEmployeesPanel.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "LoadComboBox Failed!\n" + ex.getMessage());
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

        searchField = new javax.swing.JTextField();
        centerPanel = new javax.swing.JPanel();
        searchPanel = new javax.swing.JPanel();
        searchLabel = new javax.swing.JLabel();
        searchComboBox = new javax.swing.JComboBox<>();
        dateComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        attendanceTable = new javax.swing.JTable();

        searchField.setBackground(new java.awt.Color(255, 255, 255));
        searchField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchField.setForeground(new java.awt.Color(0, 0, 0));
        searchField.setPreferredSize(new java.awt.Dimension(400, 30));

        setMinimumSize(new java.awt.Dimension(700, 400));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setMinimumSize(new java.awt.Dimension(700, 500));
        centerPanel.setOpaque(false);
        centerPanel.setPreferredSize(new java.awt.Dimension(1200, 800));
        centerPanel.setLayout(new java.awt.BorderLayout());

        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new java.awt.Dimension(100, 50));
        searchPanel.setLayout(new java.awt.GridBagLayout());

        searchLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        searchLabel.setText("Search:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        searchPanel.add(searchLabel, gridBagConstraints);

        searchComboBox.setEditable(true);
        searchComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        searchComboBox.setPreferredSize(new java.awt.Dimension(400, 30));
        searchPanel.add(searchComboBox, new java.awt.GridBagConstraints());

        dateComboBox.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        dateComboBox.setMinimumSize(new java.awt.Dimension(120, 30));
        dateComboBox.setPreferredSize(new java.awt.Dimension(120, 30));
        dateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dateComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        searchPanel.add(dateComboBox, gridBagConstraints);

        centerPanel.add(searchPanel, java.awt.BorderLayout.PAGE_START);

        attendanceTable.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        attendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Employee ID", "Full Name", "Date", "Time In", "Time Out"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        attendanceTable.setFocusable(false);
        attendanceTable.setRowHeight(25);
        attendanceTable.setShowGrid(true);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(attendanceTable);

        centerPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        add(centerPanel, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void dateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dateComboBoxActionPerformed
        searchDateAction();
    }//GEN-LAST:event_dateComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable attendanceTable;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox<String> dateComboBox;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox<String> searchComboBox;
    private javax.swing.JTextField searchField;
    private javax.swing.JLabel searchLabel;
    private javax.swing.JPanel searchPanel;
    // End of variables declaration//GEN-END:variables
}
