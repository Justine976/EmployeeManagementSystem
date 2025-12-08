import java.time.LocalDate;
import java.time.DayOfWeek;

public class GenerateAttendance {

    public static void main(String[] args) {

        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 31);

        StringBuilder sb = new StringBuilder();
        sb.append("INSERT IGNORE INTO attendance_table ")
          .append("(employee_id, date, time_in, time_out, total_hours, status) VALUES ");

        boolean first = true;

        for (int empId = 1000; empId < 1010; empId++) {

            LocalDate date = start;

            while (!date.isAfter(end)) {

                // Skip Saturday and Sunday
                if (date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                    date.getDayOfWeek() != DayOfWeek.SUNDAY) {

                    if (!first) sb.append(",");
                    first = false;

                    sb.append("(")
                      .append(empId).append(",")
                      .append("'").append(date).append("',")
                      .append("'09:00:00',")
                      .append("'18:00:00',")
                      .append("9,")
                      .append("'Present'")
                      .append(")");
                }

                date = date.plusDays(1);
            }
        }

        sb.append(";");

        System.out.println(sb.toString());
    }
}
