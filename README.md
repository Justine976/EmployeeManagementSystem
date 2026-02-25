# Employee Management System (JavaFX 25 + SQLite)

This project has been migrated to:

- **JavaFX 25** (target runtime: `zulu25.30.17-ca-fx-jdk25.0.1-win_x64`)
- **SQLite** via **`sqlite-jdbc-3.50.3.0.jar`**

## Required JAR

Place this file in `jars/`:

- `sqlite-jdbc-3.50.3.0.jar`

> The NetBeans project is already configured to use `jars\\sqlite-jdbc-3.50.3.0.jar`.

## Database

The app now uses a local SQLite file:

- `employee_management_database.db`

It is created automatically on first run.

## Entry Point

The configured main class is now:

- `MainApp`

`MainApp` starts a JavaFX `Application` and hosts the existing UI through `SwingNode` while running on JavaFX runtime.
