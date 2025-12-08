import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AttendanceGenerator {
    public static void main(String[] args) {
        Random random = new Random();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

        // Employee IDs: 1000 to 1009
        List<Integer> employeeIds = new ArrayList<>();
        for (int i = 1000; i <= 1009; i++) {
            employeeIds.add(i);
        }

        // Dates from 2025-12-01 to 2025-12-31
        List<LocalDate> allDates = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2025, 12, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            allDates.add(date);
        }

        StringBuilder insertAttendance = new StringBuilder();
        insertAttendance.append("INSERT IGNORE INTO attendance_table\" \n+ \"(employee_id, date, time_in, time_out, total_hours, status) VALUES\" \n");

        boolean first = true;
        for (int empId : employeeIds) {
            // Shuffle dates and pick 23-26 random dates
            Collections.shuffle(allDates, random);
            int numDays = 20 + random.nextInt(7); // 20 to 26
            List<LocalDate> selectedDates = allDates.subList(0, numDays);

            for (LocalDate date : selectedDates) {
                // Generate random time_in between 08:00:00 and 09:00:00, 8-9am
                int hourIn = 8 + random.nextInt(2);
                int minIn = random.nextInt(60);
                LocalTime timeIn = LocalTime.of(hourIn, minIn, 0);

                // Generate random time_out between 16:00:00 and 17:00:00, 5-6pm
                int hourOut = 16 + random.nextInt(2);
                int minOut = random.nextInt(60);
                LocalTime timeOut = LocalTime.of(hourOut, minOut, 0);

                // Calculate total_hours (assuming no overnight shifts)
                double totalHours = (timeOut.toSecondOfDay() - timeIn.toSecondOfDay()) / 3600.0;
                if (totalHours < 0) totalHours += 24; // In case time_out is next day, but unlikely here

                String status = "Present";

                if (!first) {
                    insertAttendance.append(", \"\n");
                }
                first = false;

                insertAttendance.append("+ \"(")
                    .append(empId).append(", ")
                    .append("'").append(date.format(dateFormatter)).append("', ")
                    .append("'").append(timeIn.format(timeFormatter)).append("', ")
                    .append("'").append(timeOut.format(timeFormatter)).append("', ")
                    .append(String.format("%.2f", totalHours)).append(", ")
                    .append("'").append(status).append("')");
            }
        }


        System.out.println("String insertAttendance = \"" + insertAttendance.toString() + "\";");
        System.out.println("stmt.execute(insertAttendance);");
    }
}
