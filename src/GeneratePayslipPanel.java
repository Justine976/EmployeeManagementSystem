
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import javax.swing.JOptionPane;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
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
public class GeneratePayslipPanel extends javax.swing.JPanel {

    // Get today's date
    LocalDate today = LocalDate.now();

    // Get the first and last day of the current month
    YearMonth yearMonth = YearMonth.from(today);
    LocalDate startOfMonth = yearMonth.atDay(1);
    LocalDate endOfMonth = yearMonth.atEndOfMonth();

    MainFrame mainFrame;

    int id;

    /**
     * Creates new form GeneratePayrollPanel
     */
    public GeneratePayslipPanel(MainFrame mainFrame, int id) {
        this.mainFrame = mainFrame;
        this.id = id;
        initComponents();

        totalWorkDaysField.setText("26");
        startDateField.setText(startOfMonth.toString());
        endDateField.setText(endOfMonth.toString());

        Generate();
        
        DocumentListener genListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                Generate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                Generate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                Generate();
            }
        };

        // attach to all fields
        totalWorkDaysField.getDocument().addDocumentListener(genListener);
        startDateField.getDocument().addDocumentListener(genListener);
        endDateField.getDocument().addDocumentListener(genListener);
        preparedByField.getDocument().addDocumentListener(genListener);

    }

    private static double computeWithholdingTaxMonthly(double taxableIncome) {
        // Based on 2025 TRAIN withholding tax table (monthly income)
        // 0 – 20,833: 0%
        // 20,833.01 – 33,333: 15% of excess over 20,833
        // 33,333.01 – 66,666: 2,500 + 20% of excess over 33,333
        // 66,666.01 – 166,666: 10,833.33 + 25% of excess over 66,666
        // 166,666.01 – 666,666: 40,833.33 + 30% of excess over 166,666
        // above 666,666: 200,833.33 + 35% of excess over 666,666

        double tax = 0.0;
        if (taxableIncome <= 20833) {
            tax = 0;
        } else if (taxableIncome <= 33333) {
            tax = (taxableIncome - 20833) * 0.15;
        } else if (taxableIncome <= 66666) {
            tax = 2500 + (taxableIncome - 33333) * 0.20;
        } else if (taxableIncome <= 166666) {
            tax = 10833.33 + (taxableIncome - 66666) * 0.25;
        } else if (taxableIncome <= 666666) {
            tax = 40833.33 + (taxableIncome - 166666) * 0.30;
        } else {
            tax = 200833.33 + (taxableIncome - 666666) * 0.35;
        }
        return tax;
    }

    public void Generate() {
        try {
            String startDate = startDateField.getText();
            String endDate = endDateField.getText();

            // Define payroll period
//            String payPeriod = today.getYear() + "-" + today.getMonth() + "-" + startOfMonth.getDayOfMonth() + "-" + endOfMonth.getDayOfMonth();
            // Select employee
            String empQuery = "SELECT full_name, position, salary FROM employees_table";
            PreparedStatement pstmt = mainFrame.connection.prepareStatement(empQuery);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String employeeName = rs.getString("full_name");
                String position = rs.getString("position");
                double monthlySalary = rs.getDouble("salary");

                int totalWorkingDays = 0;
                if (!totalWorkDaysField.getText().isEmpty()) {
                    totalWorkingDays = Integer.parseInt(totalWorkDaysField.getText());
                }

                // Count total present and absent days from attendance
                String countPresentQuery = "SELECT COUNT(*) AS total_present FROM attendance_table "
                        + "WHERE employee_id=? AND date BETWEEN ? AND ? AND status='Present'";
                pstmt = mainFrame.connection.prepareStatement(countPresentQuery);
                pstmt.setInt(1, id);
                pstmt.setString(2, startDate);
                pstmt.setString(3, endDate);
                rs = pstmt.executeQuery();

                int totalPresent = 0;
                if (rs.next()) {
                    totalPresent = rs.getInt("total_present");
                }

                int absentDays = totalWorkingDays - totalPresent;
                if (absentDays < 0) {
                    absentDays = 0;
                }

                // 1. Get total work hours from attendance table
                String sql = "SELECT SUM(total_hours) AS total_hours_worked "
                        + "FROM attendance_table "
                        + "WHERE employee_id = ? AND date >= ? AND date <= ?";
                pstmt = mainFrame.connection.prepareStatement(sql);
                pstmt.setInt(1, id);
                pstmt.setDate(2, Date.valueOf(startDate));
                pstmt.setDate(3, Date.valueOf(endDate));
                rs = pstmt.executeQuery();
                double totalHoursWorked = 0;
                if (rs.next()) {
                    totalHoursWorked = rs.getDouble("total_hours_worked");
                }

                // 2. Calculate hourly rate
                int standardMonthlyHours = totalWorkingDays * 8; // 26 days x 8 hours/day
                double hourlyRate = monthlySalary / standardMonthlyHours;

                // 3. Calculate gross pay based on actual hours
                double grossPay = hourlyRate * totalHoursWorked;

                // 4. Compute deductions (using same rules as monthly, prorate for hours)
                double sss = (monthlySalary * 0.05) * (totalHoursWorked / standardMonthlyHours);
                double philHealth = (monthlySalary * 0.025) * (totalHoursWorked / standardMonthlyHours);
                double pagibig = Math.min(monthlySalary, 10000) * 0.02 * (totalHoursWorked / standardMonthlyHours);

                // Taxable income
                double taxableIncome = grossPay - (sss + philHealth + pagibig);
                double tax = computeWithholdingTaxMonthly(taxableIncome); // simplified monthly tax

                double totalDeductions = sss + philHealth + pagibig + tax;
                double netPay = grossPay - totalDeductions;

                if (!(totalHoursWorked > standardMonthlyHours)) {
                    String payslip
                            = "----------------------------------------\n"
                            + "                PAYSLIP\n"
                            + "----------------------------------------\n"
                            + "EMPLOYEE INFO\n"
                            + "----------------------------------------\n"
                            + "Employee Name      : " + employeeName + "\n"
                            + "Employee ID        : " + id + "\n"
                            + "Position           : " + position + "\n"
                            + "Monthly Salary     : " + String.format("%.2f", monthlySalary) + "\n"
                            + "Total Present      : " + totalPresent + "\n"
                            + "Total Absent       : " + absentDays + "\n"
                            + "Total Hours Worked : " + String.format("%.2f", totalHoursWorked) + "\n"
                            + "Standard Work Days : " + totalWorkingDays + "\n"
                            + "Standard Hours     : " + standardMonthlyHours + "\n"
                            + "Hourly Rate        : " + String.format("%.2f", hourlyRate) + "\n"
                            + "----------------------------------------\n"
                            + "EARNINGS\n"
                            + "----------------------------------------\n"
                            + "Gross Pay          : " + String.format("%.2f", grossPay) + "\n"
                            + "----------------------------------------\n"
                            + "DEDUCTIONS\n"
                            + "----------------------------------------\n"
                            + "SSS                : " + String.format("%.2f", sss) + "\n"
                            + "PhilHealth         : " + String.format("%.2f", philHealth) + "\n"
                            + "Pag-IBIG           : " + String.format("%.2f", pagibig) + "\n"
                            + "Taxable Income     : " + String.format("%.2f", taxableIncome) + "\n"
                            + "Tax                : " + String.format("%.2f", tax) + "\n"
                            + "Total Deduction    : " + String.format("%.2f", totalDeductions) + "\n"
                            + "----------------------------------------\n"
                            + "NET PAY            : " + String.format("%.2f", netPay) + "\n"
                            + "----------------------------------------\n"
                            + "Date               : " + today + "\n"
                            + "Prepared by        : " + preparedByField.getText() + "\n";
                    receiptArea.setText(payslip);

                    // Insert payroll record
//                String insertPayroll = "INSERT INTO payroll_table (employee_id, total_working_days, absent_days, daily_rate, absence_deduction, net_pay, pay_period) "
//                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";
//                pstmt = mainFrame.connection.prepareStatement(insertPayroll);
//                pstmt.setInt(1, employeeId);
//                pstmt.setInt(2, totalWorkingDays);
//                pstmt.setInt(3, absentDays);
//                pstmt.setDouble(4, dailyRate);
//                pstmt.setDouble(5, absenceDeduction);
//                pstmt.setDouble(6, netPay);
//                pstmt.setString(7, payPeriod);
//                pstmt.executeUpdate();
                }
            }
        } catch (HeadlessException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error generating payslip: " + ex.getMessage());
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

        bagPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        receiptArea = new javax.swing.JTextArea();
        moreInfoPanel = new javax.swing.JPanel();
        totalWorkDaysField = new javax.swing.JTextField();
        totalWorkDaysLabel = new javax.swing.JLabel();
        startDateLabel = new javax.swing.JLabel();
        startDateField = new javax.swing.JTextField();
        endDateLabel = new javax.swing.JLabel();
        endDateField = new javax.swing.JTextField();
        preparedByLabel = new javax.swing.JLabel();
        preparedByField = new javax.swing.JTextField();
        buttonsPanel = new javax.swing.JPanel();
        backButton = new javax.swing.JButton();
        printButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        bagPanel.setMinimumSize(new java.awt.Dimension(800, 600));
        bagPanel.setOpaque(false);
        bagPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        bagPanel.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setBorder(null);
        jScrollPane1.setMinimumSize(new java.awt.Dimension(350, 300));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(350, 300));

        receiptArea.setEditable(false);
        receiptArea.setBackground(new java.awt.Color(255, 255, 255));
        receiptArea.setColumns(40);
        receiptArea.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        receiptArea.setForeground(new java.awt.Color(0, 0, 0));
        receiptArea.setRows(10);
        receiptArea.setMinimumSize(new java.awt.Dimension(332, 204));
        jScrollPane1.setViewportView(receiptArea);

        bagPanel.add(jScrollPane1, java.awt.BorderLayout.WEST);

        moreInfoPanel.setOpaque(false);
        moreInfoPanel.setLayout(new java.awt.GridBagLayout());

        totalWorkDaysField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        totalWorkDaysField.setMinimumSize(new java.awt.Dimension(250, 35));
        totalWorkDaysField.setPreferredSize(new java.awt.Dimension(250, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        moreInfoPanel.add(totalWorkDaysField, gridBagConstraints);

        totalWorkDaysLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        totalWorkDaysLabel.setText("Total Work Days:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        moreInfoPanel.add(totalWorkDaysLabel, gridBagConstraints);

        startDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        startDateLabel.setText("Start Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
        moreInfoPanel.add(startDateLabel, gridBagConstraints);

        startDateField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        startDateField.setMinimumSize(new java.awt.Dimension(250, 35));
        startDateField.setPreferredSize(new java.awt.Dimension(250, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
        moreInfoPanel.add(startDateField, gridBagConstraints);

        endDateLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        endDateLabel.setText("End Date:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 30, 0);
        moreInfoPanel.add(endDateLabel, gridBagConstraints);

        endDateField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        endDateField.setMinimumSize(new java.awt.Dimension(250, 35));
        endDateField.setPreferredSize(new java.awt.Dimension(250, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 30, 0);
        moreInfoPanel.add(endDateField, gridBagConstraints);

        preparedByLabel.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        preparedByLabel.setText("Prepared By:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        moreInfoPanel.add(preparedByLabel, gridBagConstraints);

        preparedByField.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        preparedByField.setMinimumSize(new java.awt.Dimension(250, 35));
        preparedByField.setPreferredSize(new java.awt.Dimension(250, 35));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        moreInfoPanel.add(preparedByField, gridBagConstraints);

        bagPanel.add(moreInfoPanel, java.awt.BorderLayout.CENTER);

        buttonsPanel.setOpaque(false);
        buttonsPanel.setPreferredSize(new java.awt.Dimension(200, 60));
        buttonsPanel.setLayout(new java.awt.GridBagLayout());

        backButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        backButton.setText("BACK");
        backButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        backButton.setDefaultCapable(false);
        backButton.setFocusable(false);
        backButton.setPreferredSize(new java.awt.Dimension(100, 40));
        backButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        buttonsPanel.add(backButton, gridBagConstraints);

        printButton.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        printButton.setText("PRINT");
        printButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        printButton.setDefaultCapable(false);
        printButton.setFocusable(false);
        printButton.setPreferredSize(new java.awt.Dimension(100, 40));
        printButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
        buttonsPanel.add(printButton, gridBagConstraints);

        bagPanel.add(buttonsPanel, java.awt.BorderLayout.PAGE_END);

        add(bagPanel, new java.awt.GridBagConstraints());
    }// </editor-fold>//GEN-END:initComponents

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
        mainFrame.add(mainFrame.buttonsPanel, java.awt.BorderLayout.WEST);
        mainFrame.switchPanel(new ViewEmployeesPanel(mainFrame), "ViewEmployees");
    }//GEN-LAST:event_backButtonActionPerformed

    private void printButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printButtonActionPerformed
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Print");

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }

            // Convert Graphics to Graphics2D
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            receiptArea.printAll(g2);
            return Printable.PAGE_EXISTS;
        });

        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
                JOptionPane.showMessageDialog(this, "Printing Done!");
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing Failed!\n" + ex.getMessage());
            }
        }
    }//GEN-LAST:event_printButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JPanel bagPanel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JTextField endDateField;
    private javax.swing.JLabel endDateLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel moreInfoPanel;
    private javax.swing.JTextField preparedByField;
    private javax.swing.JLabel preparedByLabel;
    private javax.swing.JButton printButton;
    private javax.swing.JTextArea receiptArea;
    private javax.swing.JTextField startDateField;
    private javax.swing.JLabel startDateLabel;
    private javax.swing.JTextField totalWorkDaysField;
    private javax.swing.JLabel totalWorkDaysLabel;
    // End of variables declaration//GEN-END:variables
}
