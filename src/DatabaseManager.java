import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:employee_management_database.db";

    private DatabaseManager() {
    }

    public static Connection connectAndInitialize() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        Connection connection = DriverManager.getConnection(DB_URL);
        initializeSchema(connection);
        return connection;
    }

    public static void initializeSchema(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON");

            stmt.execute("CREATE TABLE IF NOT EXISTS users_table ("
                    + "user_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "admin INTEGER NOT NULL CHECK (admin IN (0,1)), "
                    + "username TEXT UNIQUE NOT NULL, "
                    + "password TEXT NOT NULL)");

            stmt.executeUpdate("INSERT OR IGNORE INTO users_table (user_id, admin, username, password) VALUES "
                    + "(1, 1, 'admin', 'admin')");

            stmt.execute("CREATE TABLE IF NOT EXISTS employees_table ("
                    + "employee_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "photo_path TEXT NOT NULL, "
                    + "full_name TEXT NOT NULL, "
                    + "birth_date TEXT NOT NULL, "
                    + "gender TEXT NOT NULL CHECK (gender IN ('Male','Female','Other')), "
                    + "address TEXT NOT NULL, "
                    + "contact_number TEXT NOT NULL, "
                    + "email_address TEXT UNIQUE NOT NULL, "
                    + "position TEXT NOT NULL, "
                    + "department TEXT NOT NULL, "
                    + "salary REAL NOT NULL, "
                    + "hired_date TEXT NOT NULL)");

            stmt.execute("CREATE TABLE IF NOT EXISTS attendance_table ("
                    + "attendance_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "employee_id INTEGER NOT NULL, "
                    + "date TEXT NOT NULL, "
                    + "time_in TEXT, "
                    + "time_out TEXT, "
                    + "total_hours REAL, "
                    + "status TEXT DEFAULT 'Present', "
                    + "FOREIGN KEY (employee_id) REFERENCES employees_table(employee_id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)");

            stmt.execute("CREATE TABLE IF NOT EXISTS payroll_table ("
                    + "payroll_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "employee_id INTEGER NOT NULL, "
                    + "total_working_days INTEGER NOT NULL, "
                    + "absent_days INTEGER NOT NULL, "
                    + "daily_rate REAL NOT NULL, "
                    + "absence_deduction REAL NOT NULL, "
                    + "net_pay REAL NOT NULL, "
                    + "pay_period TEXT NOT NULL, "
                    + "pay_date TEXT DEFAULT CURRENT_DATE, "
                    + "FOREIGN KEY (employee_id) REFERENCES employees_table(employee_id) "
                    + "ON DELETE CASCADE ON UPDATE CASCADE)");

            seedEmployees(connection);
        }
    }

    public static void resetDatabaseData(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = OFF");
            stmt.executeUpdate("DELETE FROM payroll_table");
            stmt.executeUpdate("DELETE FROM attendance_table");
            stmt.executeUpdate("DELETE FROM employees_table");
            stmt.executeUpdate("DELETE FROM users_table");
            stmt.executeUpdate("DELETE FROM sqlite_sequence WHERE name IN ('users_table','employees_table','attendance_table','payroll_table')");
            stmt.execute("PRAGMA foreign_keys = ON");
        }

        initializeSchema(connection);
    }

    private static void seedEmployees(Connection connection) throws SQLException {
        String[][] employees = {
            {"1000", "photos/emp1.jpg", "Juan Dela Cruz", "1995-04-12", "Male", "Manila City", "09171234567", "juan.cruz@example.com", "Software Engineer", "IT Department", "35000.00", "2022-03-10"},
            {"1001", "photos/emp2.jpg", "Maria Santos", "1998-07-21", "Female", "Quezon City", "09281234567", "maria.santos@example.com", "HR Officer", "Human Resources", "30000.00", "2021-11-05"},
            {"1002", "photos/emp3.jpg", "Mark Reyes", "1992-01-18", "Male", "Pasig City", "09181231234", "mark.reyes@example.com", "Accountant", "Finance", "32000.00", "2020-06-15"},
            {"1003", "photos/emp4.jpg", "Angela Cruz", "1996-10-04", "Female", "Cebu City", "09391234567", "angela.cruz@example.com", "Marketing Specialist", "Marketing", "28000.00", "2023-01-12"},
            {"1004", "photos/emp5.jpg", "John Bautista", "1993-03-09", "Male", "Davao City", "09491234567", "john.bautista@example.com", "IT Support", "IT Department", "26000.00", "2021-05-20"},
            {"1005", "photos/emp6.jpg", "Catherine Lim", "1997-12-11", "Female", "Makati City", "09291231231", "catherine.lim@example.com", "Sales Associate", "Sales", "25000.00", "2022-10-01"},
            {"1006", "photos/emp7.jpg", "Joseph Tan", "1990-02-27", "Male", "Taguig City", "09191231212", "joseph.tan@example.com", "Project Manager", "Operations", "45000.00", "2019-04-08"},
            {"1007", "photos/emp8.jpg", "Elaine Garcia", "1999-05-30", "Female", "Las Piñas City", "09301231231", "elaine.garcia@example.com", "Receptionist", "Front Desk", "20000.00", "2023-08-03"},
            {"1008", "photos/emp9.jpg", "Patrick Villanueva", "1994-09-23", "Male", "Caloocan City", "09181231234", "patrick.villanueva@example.com", "Network Technician", "IT Department", "27000.00", "2021-09-10"},
            {"1009", "photos/emp10.jpg", "Liza Ramos", "1991-06-25", "Female", "Baguio City", "09271231231", "liza.ramos@example.com", "Administrative Assistant", "Admin", "23000.00", "2020-02-17"}
        };

        try (PreparedStatement pstmt = connection.prepareStatement(
                "INSERT OR IGNORE INTO employees_table (employee_id, photo_path, full_name, birth_date, gender, address, contact_number, email_address, position, department, salary, hired_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (String[] employee : employees) {
                pstmt.setInt(1, Integer.parseInt(employee[0]));
                for (int i = 1; i <= 10; i++) {
                    pstmt.setString(i + 1, employee[i]);
                }
                pstmt.setString(12, employee[11]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}
