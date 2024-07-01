package org.example.calendar;

import lombok.*;
import org.apache.commons.numbers.fraction.Fraction;
import org.example.Employee;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static java.time.temporal.ChronoUnit.HOURS;

@Data
@NoArgsConstructor
public class CheckInDay {
    private String empName;
    private String empMatNum;
    private DayOfWeek dayOfWeek;
    private int hoursToday;
    private double amountToday;
    private LocalDateTime start;
    private LocalDateTime end;
    private boolean isOverTime;
    private boolean haveABreak;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Fraction amountTodayFrac;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Employee employee;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WorkerCalendar calendar;

    public CheckInDay(Employee employee, WorkerCalendar calendar, LocalDateTime start, LocalDateTime end, boolean isOverTime, boolean haveABreak) {
        this.employee = employee;
        this.empName = employee.getFirstname();
        this.empMatNum = employee.getMatriculationNumber();
        this.dayOfWeek = DayOfWeek.from(start);
        this.calendar = calendar;
        this.start = start;
        this.end = end;
        this.haveABreak = haveABreak;
        this.isOverTime = isOverTime;

        setAmountToday(start, end, isOverTime, haveABreak);
    }

    private void setAmountToday(LocalDateTime start, LocalDateTime end, boolean isOverTime, boolean haveABreak) {
        double totalTimeRate = 0;

        boolean isNight = start.isAfter(LocalDateTime.parse(LocalDate.from(start) + "T16:30"));
        boolean isNormalDay = start.isBefore(LocalDateTime.parse(LocalDate.from(start) + "T08:30"));
        boolean isSunday = DayOfWeek.from(start) == DayOfWeek.SUNDAY;
        boolean isHoliday = calendar.isHolidayDay(LocalDate.from(start));

        int workDuration = (int) HOURS.between(start, end);

        if (isHoliday) {
            totalTimeRate = calculateTotalHolidayTimeRate(workDuration, haveABreak, isOverTime, isNight, isSunday);
        } else if (isSunday) {
            totalTimeRate = calculateTotalSundayTimeRate(workDuration, haveABreak, isOverTime, isNight);
        } else if (isNight) {
            totalTimeRate = calculateTotalNightTimeRate(workDuration, isOverTime);
        } else if (isOverTime) {
            totalTimeRate = calculateOverTimeRate(workDuration, haveABreak);
        } else if (isNormalDay) {
            totalTimeRate = 0;
        }
        this.hoursToday = workDuration;
        Fraction salaryPerHours = employee.getSalaryPerHours();

        this.amountTodayFrac = totalTimeRate != 0 ? salaryPerHours.multiply(workDuration).multiply(Fraction.from(totalTimeRate)) : salaryPerHours.multiply(workDuration);
        this.amountToday = amountTodayFrac.doubleValue();
    }

    private double calculateTotalHolidayTimeRate(int workDuration, boolean haveABreak, boolean isOverTime, boolean isNight, boolean isSunday) {
        double holidayPrimeRate = 1.5;
        if (isSunday) {
            return calculateTotalSundayTimeRate(workDuration, haveABreak, isOverTime, isNight) * holidayPrimeRate;
        }
        if (isNight) {
            return calculateTotalNightTimeRate(workDuration, isOverTime) * holidayPrimeRate;
        }
        if (isOverTime) {
            return calculateOverTimeRate(workDuration, haveABreak) * holidayPrimeRate;
        }
        return holidayPrimeRate;
    }

    private double calculateTotalSundayTimeRate(int workDuration, boolean haveABreak, boolean isOverTime, boolean isNight) {
        double sundayPrimeRate = 1.4;
        if (isNight) {
            return calculateTotalNightTimeRate(workDuration, isOverTime) * sundayPrimeRate;
        }
        if (isOverTime) {
            return calculateOverTimeRate(workDuration, haveABreak) * sundayPrimeRate;
        }
        return sundayPrimeRate;
    }

    private double calculateTotalNightTimeRate(int workDuration, boolean isOverTime) {
        double nightPrimeRate = 1.3;
        return isOverTime ? calculateOverTimeRate(workDuration, false) * nightPrimeRate : nightPrimeRate;
    }

    private double calculateOverTimeRate(int totalOverTime, boolean haveABreak) {
        double overTimeEightRate = 1.3;
        double overTimeTwelveRate = 1.5;

        totalOverTime = applyBreak(totalOverTime, haveABreak);

        int remainingTime = totalOverTime - 8;
        double totalRate;
        if (remainingTime > 0)  {
            totalRate = 8 * overTimeEightRate + remainingTime * overTimeTwelveRate;
        } else {
            totalRate = totalOverTime * overTimeEightRate;
        }
        return totalRate;
    }
    private int applyBreak(int workDuration, boolean haveABreak) {
        return haveABreak ? workDuration - 1 : workDuration;
    }
    public boolean isNight() {
        return this.start.isAfter(LocalDate.from(start).atTime(LocalTime.parse("16:30")));
    }
}
