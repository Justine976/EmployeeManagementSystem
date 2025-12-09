
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import java.awt.CardLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
/**
 *
 * @author Admin
 */
public class MainFrame extends javax.swing.JFrame {

    Connection connection;

    CardLayout cardLayout;

    ButtonsPanel buttonsPanel;

    Preferences prefs = Preferences.userRoot().node("EmployeeManagementSystem.Preferences");
    boolean isDark = prefs.getBoolean("darkMode", false);

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        initComponents();
        setTheme(isDark);
        Connect();

        cardLayout = (CardLayout) (mainPanel.getLayout());

        LoginPanel loginPanel = new LoginPanel(this);
        switchPanel(loginPanel, "Login");

    }

    public void setTheme(boolean isDark) {
        try {
            if (isDark) {
                UIManager.setLookAndFeel(new FlatMacDarkLaf());
            } else {
                UIManager.setLookAndFeel(new FlatMacLightLaf());
            }
            String iconName = isDark ? "employeeManagementIcon-for-dark.png" : "employeeManagementIcon-for-light.png";
            setIconImage(new ImageIcon(getClass().getResource("/resources/" + iconName)).getImage());

            prefs.putBoolean("darkMode", isDark);

            UIManager.put("OptionPane.messageFont", new Font("Segoe UI", Font.PLAIN, 15));
            javax.swing.SwingUtilities.updateComponentTreeUI(this);

            this.revalidate();
            this.repaint();
        } catch (UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
    }

    public void switchPanel(javax.swing.JPanel newPanel, String name) {
        mainPanel.removeAll();
        mainPanel.add(newPanel, name);
        cardLayout.show(mainPanel, name);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void Connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost/employee_management_database", "root", "");
            Statement stmt = connection.createStatement();

            // -------------------- USERS TABLE --------------------
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users_table ("
                    + "user_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "admin BOOLEAN NOT NULL, "
                    + "username VARCHAR(50) UNIQUE NOT NULL, "
                    + "password VARCHAR(255) NOT NULL)";
            stmt.execute(createUsersTable);

            // Default admin account
            stmt.executeUpdate(
                    "INSERT IGNORE INTO users_table (admin, username, password) VALUES "
                    + "(1, 'admin', 'admin') "
            );

            // -------------------- EMPLOYEES TABLE --------------------
            String createEmployeesTable = "CREATE TABLE IF NOT EXISTS employees_table ("
                    + "employee_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "photo_path VARCHAR(255) NOT NULL, "
                    + "full_name VARCHAR(200) NOT NULL, "
                    + "birth_date DATE NOT NULL, "
                    + "gender ENUM('Male','Female','Other') NOT NULL, "
                    + "address TEXT NOT NULL, "
                    + "contact_number VARCHAR(20) NOT NULL, "
                    + "email_address VARCHAR(200) UNIQUE NOT NULL, "
                    + "position VARCHAR(100) NOT NULL, "
                    + "department VARCHAR(100) NOT NULL, "
                    + "salary DECIMAL(10,2) NOT NULL, "
                    + "hired_date DATE NOT NULL)";
            stmt.execute(createEmployeesTable);

            // Start employee_id at 1000 for cleaner IDs
            stmt.executeUpdate("ALTER TABLE employees_table AUTO_INCREMENT = 1000;");

            // -------------------- INSERT SAMPLE EMPLOYEES --------------------
            String insertEmployees = "INSERT IGNORE INTO employees_table "
                    + "(photo_path, full_name, birth_date, gender, address, contact_number, email_address, position, department, salary, hired_date) VALUES "
                    + "('photos/emp1.jpg', 'Juan Dela Cruz', '1995-04-12', 'Male', 'Manila City', '09171234567', 'juan.cruz@example.com', 'Software Engineer', 'IT Department', 35000.00, '2022-03-10'), "
                    + "('photos/emp2.jpg', 'Maria Santos', '1998-07-21', 'Female', 'Quezon City', '09281234567', 'maria.santos@example.com', 'HR Officer', 'Human Resources', 30000.00, '2021-11-05'), "
                    + "('photos/emp3.jpg', 'Mark Reyes', '1992-01-18', 'Male', 'Pasig City', '09181231234', 'mark.reyes@example.com', 'Accountant', 'Finance', 32000.00, '2020-06-15'), "
                    + "('photos/emp4.jpg', 'Angela Cruz', '1996-10-04', 'Female', 'Cebu City', '09351231231', 'angela.cruz@.com', 'Graphic Designer', 'Marketing', 28000.00, '2023-01-12'), "
                    + "('photos/emp5.jpg', 'John Bautista', '1993-03-09', 'Male', 'Davao City', '09491234567', 'john.bautista@example.com', 'IT Support', 'IT Department', 26000.00, '2021-05-20'), "
                    + "('photos/emp6.jpg', 'Catherine Lim', '1997-12-11', 'Female', 'Makati City', '09291231231', 'catherine.lim@example.com', 'Sales Associate', 'Sales', 25000.00, '2022-10-01'), "
                    + "('photos/emp7.jpg', 'Joseph Tan', '1990-02-27', 'Male', 'Taguig City', '09191231212', 'joseph.tan@example.com', 'Project Manager', 'Operations', 45000.00, '2019-04-08'), "
                    + "('photos/emp8.jpg', 'Elaine Garcia', '1999-05-30', 'Female', 'Las Piñas City', '09301231231', 'elaine.garcia@example.com', 'Receptionist', 'Front Desk', 20000.00, '2023-08-03'), "
                    + "('photos/emp9.jpg', 'Patrick Villanueva', '1994-09-23', 'Male', 'Caloocan City', '09181231234', 'patrick.villanueva@example.com', 'Network Technician', 'IT Department', 27000.00, '2021-09-10'), "
                    + "('photos/emp10.jpg', 'Liza Ramos', '1991-06-25', 'Female', 'Baguio City', '09271231231', 'liza.ramos@example.com', 'Administrative Assistant', 'Admin', 23000.00, '2020-02-17');";
            stmt.executeUpdate(insertEmployees);

            // -------------------- ATTENDANCE TABLE --------------------
            String createAttendanceTable = "CREATE TABLE IF NOT EXISTS attendance_table ("
                    + "attendance_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "employee_id INT NOT NULL, "
                    + "date DATE NOT NULL, "
                    + "time_in TIME, "
                    + "time_out TIME, "
                    + "total_hours DECIMAL(5,2), "
                    + "status VARCHAR(20) DEFAULT 'Present', "
                    + "FOREIGN KEY (employee_id) REFERENCES employees_table(employee_id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createAttendanceTable);

            // -------------------- PAYROLL TABLE --------------------
            String createPayrollTable = "CREATE TABLE IF NOT EXISTS payroll_table ("
                    + "payroll_id INT AUTO_INCREMENT PRIMARY KEY, "
                    + "employee_id INT NOT NULL, "
                    + "total_working_days INT NOT NULL, "
                    + "absent_days INT NOT NULL, "
                    + "daily_rate DECIMAL(10,2) NOT NULL, "
                    + "absence_deduction DECIMAL(10,2) NOT NULL, "
                    + "net_pay DECIMAL(10,2) NOT NULL, "
                    + "pay_period VARCHAR(50) NOT NULL, "
                    + "pay_date DATE DEFAULT CURRENT_DATE, "
                    + "FOREIGN KEY (employee_id) REFERENCES employees_table(employee_id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)";
            stmt.execute(createPayrollTable);

        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database Connecting Failed!\n" + ex.getLocalizedMessage());
            System.exit(0);
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

        mainPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Employee Management System");
        setMinimumSize(new java.awt.Dimension(1000, 750));
        setPreferredSize(new java.awt.Dimension(1200, 800));

        mainPanel.setLayout(new java.awt.CardLayout());
        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}
