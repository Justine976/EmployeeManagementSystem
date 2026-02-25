# Employee Management System (JavaFX 25 + SQLite)

This repository is configured to run on:

- **JavaFX 25** (target runtime: `zulu25.30.17-ca-fx-jdk25.0.1-win_x64`)
- **SQLite** using **`sqlite-jdbc-3.50.3.0.jar`**

## Runtime and Build

- Java source/target level is **25**.
- Main class is **`MainApp`**, which launches a JavaFX `Application`.
- Existing screens are hosted inside JavaFX through `SwingNode` so the app runs on JavaFX runtime while preserving the current UI behavior.

## Required JAR

Place this file in `jars/`:

- `sqlite-jdbc-3.50.3.0.jar`

The NetBeans project is configured to include `jars\\sqlite-jdbc-3.50.3.0.jar`.

## Database

- SQLite file: `employee_management_database.db`
- Schema and seed data are created automatically at startup by `DatabaseManager`.
- Reset operations now clear and reseed SQLite tables (instead of unsupported MySQL `DROP/CREATE DATABASE` statements).
