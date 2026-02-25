
import java.sql.*;
import java.time.LocalDate;

public class PayrollCalculator {

    public static void computePayroll(
            Connection conn,
            int employeeId,
            LocalDate periodStart,
            LocalDate periodEnd
    ) throws SQLException {
        String empQuery = "SELECT full_name, position, salary FROM employees_table WHERE employee_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(empQuery);
        pstmt.setInt(1, employeeId);
        ResultSet rs = pstmt.executeQuery();
        double monthlySalary = 0;
        String employeeName = "";
        String position = "";
        if (rs.next()) {
            employeeName = rs.getString("full_name");
            position = rs.getString("position");
            monthlySalary = rs.getDouble("salary");
        }
        // 1. Get total work hours from attendance table
        String sql = "SELECT SUM(total_hours) AS total_hours_worked "
                + "FROM attendance_table "
                + "WHERE employee_id = ? AND date >= ? AND date <= ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, employeeId);
        ps.setDate(2, Date.valueOf(periodStart));
        ps.setDate(3, Date.valueOf(periodEnd));
        rs = ps.executeQuery();
        double totalHoursWorked = 0;
        if (rs.next()) {
            totalHoursWorked = rs.getDouble("total_hours_worked");
        }
        rs.close();
        ps.close();

        // 2. Calculate hourly rate
        int standardMonthlyHours = 26 * 8; // 26 days x 8 hours/day
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

        if (totalHoursWorked > standardMonthlyHours) {
            System.out.println("totalHoursWorked is greater than standardMonthlyHours, please check the attendance table");
        } else {
            String payslip 
                    = "--------------------------------------------------\n"
                    + "                     PAYSLIP\n"
                    + "--------------------------------------------------\n"
                    + "Employee Name      : " + employeeName + "\n"
                    + "Employee ID        : " + employeeId + "\n"
                    + "Position           : " + position + "\n"
                    + "Monthly Salary     : " + String.format("%.2f", monthlySalary) + "\n"
                    + "Total Hours Worked : " + String.format("%.2f", totalHoursWorked) + "\n"
                    + "Standard Hours     : " + standardMonthlyHours + "\n"
                    + "Hourly Rate        : " + String.format("%.2f", hourlyRate) + "\n"
                    + "--------------------------------------------------\n"
                    + "EARNINGS\n"
                    + "--------------------------------------------------\n"
                    + "Gross Pay          : " + String.format("%.2f", grossPay) + "\n"
                    + "--------------------------------------------------\n"
                    + "DEDUCTIONS\n"
                    + "--------------------------------------------------\n"
                    + "SSS                : " + String.format("%.2f", sss) + "\n"
                    + "PhilHealth         : " + String.format("%.2f", philHealth) + "\n"
                    + "Pag-IBIG           : " + String.format("%.2f", pagibig) + "\n"
                    + "Taxable Income     : " + String.format("%.2f", taxableIncome) + "\n"
                    + "Tax                : " + String.format("%.2f", tax) + "\n"
                    + "Total Deduction    : " + String.format("%.2f", totalDeductions) + "\n"
                    + "--------------------------------------------------\n"
                    + "NET PAY            : " + String.format("%.2f", netPay) + "\n"
                    + "--------------------------------------------------";
            System.out.println(payslip);

        }

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

    // Example usage
    public static void main(String[] args) throws Exception {
        // Setup DB connection
        Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/employee_management_database", "root", "");

        computePayroll(
                conn, 1005,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 31)
        );
        conn.close();
    }
}
/*

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PayslipGUI {
    public static void main(String[] args) {
        // Sample data (replace with actual variables)
        String employeeName = "John Doe";
        String employeeId = "12345";
        String position = "Software Engineer";
        double monthlySalary = 50000.00;
        double totalHoursWorked = 160.00;
        double standardMonthlyHours = 160.00;
        double hourlyRate = 312.50;
        double grossPay = 50000.00;
        double sss = 1125.00;
        double philHealth = 750.00;
        double pagibig = 500.00;
        double taxableIncome = 45625.00;
        double tax = 9125.00;
        double totalDeductions = 11500.00;
        double netPay = 38500.00;

        // Build the payslip string (same as original, but using StringBuilder for efficiency)
        StringBuilder payslipBuilder = new StringBuilder();
        payslipBuilder.append("--------------------------------------------------\n")
                      .append("                     PAYSLIP\n")
                      .append("--------------------------------------------------\n")
                      .append("Employee Name      : ").append(employeeName).append("\n")
                      .append("Employee ID        : ").append(employeeId).append("\n")
                      .append("Position           : ").append(position).append("\n")
                      .append("Monthly Salary     : ").append(String.format("%.2f", monthlySalary)).append("\n")
                      .append("Total Hours Worked : ").append(String.format("%.2f", totalHoursWorked)).append("\n")
                      .append("Standard Hours     : ").append(standardMonthlyHours).append("\n")
                      .append("Hourly Rate        : ").append(String.format("%.2f", hourlyRate)).append("\n")
                      .append("--------------------------------------------------\n")
                      .append("EARNINGS\n")
                      .append("--------------------------------------------------\n")
                      .append("Gross Pay          : ").append(String.format("%.2f", grossPay)).append("\n")
                      .append("--------------------------------------------------\n")
                      .append("DEDUCTIONS\n")
                      .append("--------------------------------------------------\n")
                      .append("SSS                : ").append(String.format("%.2f", sss)).append("\n")
                      .append("PhilHealth         : ").append(String.format("%.2f", philHealth)).append("\n")
                      .append("Pag-IBIG           : ").append(String.format("%.2f", pagibig)).append("\n")
                      .append("Taxable Income     : ").append(String.format("%.2f", taxableIncome)).append("\n")
                      .append("Tax                : ").append(String.format("%.2f", tax)).append("\n")
                      .append("Total Deduction    : ").append(String.format("%.2f", totalDeductions)).append("\n")
                      .append("--------------------------------------------------\n")
                      .append("NET PAY            : ").append(String.format("%.2f", netPay)).append("\n")
                      .append("--------------------------------------------------");

        String payslip = payslipBuilder.toString();

        // Create GUI components
        JFrame frame = new JFrame("Payslip Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 600);

        JTextArea textArea = new JTextArea(payslip);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Monospaced font for alignment
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton printButton = new JButton("Print Payslip");
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printPayslip(textArea);
            }
        });

        JPanel panel = new JPanel();
        panel.add(printButton);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private static void printPayslip(JTextArea textArea) {
        PrinterJob job = PrinterJob.getPrinterJob();
        if (job.printDialog()) {
            try {
                job.setPrintable(textArea.getPrintable(null, null));
                job.print();
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(null, "Printing failed: " + e.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


*/