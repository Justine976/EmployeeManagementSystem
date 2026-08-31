![Project Screenshot](src/resources/Screenshot%202026-08-31%20104109.png)

# Employee Management System

A desktop **Employee Management System** built with **Java Swing** and **SQLite**, developed in **NetBeans**.
It manages employee records with photo upload, QR-code generation, QR-scan based time in/out, attendance tracking, and payroll/payslip generation.

## Features

- **Login** — admin login with "remember me" support (`admin` / `admin` by default)
- **Dashboard** — quick overview of employee and attendance stats
- **Add / Update / View employees** — with photo upload; a **QR code** is auto-generated for every employee on successful add
- **Time In / Time Out** — scans the employee's QR code with a webcam to record attendance
- **Attendance List** — per-date attendance records per employee
- **Generate Payslip** — computes attendance-based payroll (SSS, PhilHealth, Pag-IBIG, withholding tax) and prints payslips
- **Settings** — dark/light theme toggle (FlatLaf), data reset, sample data regeneration
- **Default sample data** — 10 sample employees are seeded automatically on first launch, each with an auto-generated QR code

## Tech Stack

| Component      | Technology |
|----------------|------------|
| Language       | Java (source/target 24) |
| UI             | Java Swing (NetBeans GUI Builder) + FlatLaf themes |
| Database       | SQLite (WAL mode) via `sqlite-jdbc` |
| QR generation  | ZXing (`core` + `javase`) |
| Webcam / QR scanning | `webcam-capture` (+ `bridj`) |
| Logging        | SLF4J |

All third-party JARs are vendored in the [`jars/`](jars) folder — no dependency manager needed.

## Getting Started

### Requirements

- **JDK 24 or newer** (project is configured for `javac.source=24`)
- **Apache NetBeans** (recommended — the project is a NetBeans Ant project) *or* Ant on the CLI
- A webcam (only needed for Time In/Out QR scanning)

### Run in NetBeans

1. Clone the repository:
   ```bash
   git clone https://github.com/Justine976/EmployeeManagementSystem.git
   ```
2. Open the project in NetBeans (**File → Open Project**).
3. Press **F6** (Run Project). The main class is `ui.frame.MainFrame`.

### Build from the CLI

```bash
ant clean jar
java --enable-native-access=ALL-UNNAMED -jar dist/EmployeeManagementSystem.jar
```

## Default Login

| Username | Password |
|----------|----------|
| `admin`  | `admin`  |

## Project Structure

```
src/
├── ui/
│   ├── frame/            # Top-level windows
│   │   ├── MainFrame          # Main app window, DB bootstrap, theme switching
│   │   └── TimeInOutFrame     # Standalone time in/out window (alternative UI)
│   └── panel/            # Screens swapped inside MainFrame's card layout
│       ├── LoginPanel, ButtonsPanel, DashboardPanel
│       ├── AddEmployeePanel, UpdatePanel
│       ├── ViewEmployeesPanel, ViewDetailsPanel
│       ├── AttendanceListPanel, GeneratePayslipPanel
│       ├── SettingsPanel, TimeInOutPanel
├── util/
│   ├── PayrollCalculator      # Payroll computation (taxes, deductions)
│   └── QrCodeGenerator        # Shared QR code image generation (qrcodes/<id>.png)
├── database/
│   └── generateAttendanceSample  # Seeds sample attendance rows into the DB
└── resources/            # Icons (light & dark variants)
```

Each Swing class has a matching `.form` file for the NetBeans GUI Builder — keep them together.

## Database

- File: `employee_management.db` in the project root — **auto-created on first launch** (schema + default admin + sample employees).
- QR codes are managed automatically: a QR is generated when a new employee is added, and on startup any employee without a `qrcodes/<id>.png` file (including the sample employees) gets one generated.
- Journal mode is **WAL**, and the app sets `busy_timeout = 10s`, so brief concurrent access won't fail with `SQLITE_BUSY`.
- The DB and its `-wal`/`-shm` sidecar files are git-ignored — don't commit them.

## Runtime-generated folders

| Folder     | Purpose                                              |
|------------|------------------------------------------------------|
| `photos/`  | Uploaded employee photos (`emp1.jpg`, …)             |
| `qrcodes/` | Generated QR codes (`<employee_id>.png`)             |
| `build/`, `dist/` | NetBeans build outputs (git-ignored)          |

## Notes

- Only one instance of the app should run at a time (they share the same SQLite file; WAL + busy timeout make brief overlaps safe, but not simultaneous writes from two instances).
- The SQLite JDBC driver uses native code; the run configuration already passes `--enable-native-access=ALL-UNNAMED`.
