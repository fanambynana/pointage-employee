package org.example.calendar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.example.Enum.AppliedCategory;
import org.example.Enum.DayType;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@Data
public class WorkerCalendar {
    private Month month;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<WorkerDay> holidayDays = new ArrayList<>();
    private List<WorkerDay> days;

    public WorkerCalendar(Month month, List<WorkerDay> holidayDays, LocalDate weekStart, LocalDate weekEnd) {
        this.month = month;
        this.holidayDays = holidayDays;
        this.days = new ArrayList<>();
        setDays(weekStart, weekEnd);
    }
    public WorkerCalendar(Month month, LocalDate weekStart, LocalDate weekEnd) {
        this.month = month;
        this.days = new ArrayList<>();
        setDays(weekStart, weekEnd);
    }

    private void setDays(LocalDate weekStart, LocalDate weekEnd) {
        int hoursPerDay = 8;

        int daysInWeekCount = (int) (DAYS.between(weekStart, weekEnd));
        int weeksCount = daysInWeekCount / 7;

        for (int i = 0; i <= daysInWeekCount; i++) {
           WorkerDay nextDay = getWorkerDay(weekStart, i);
            days.add(nextDay);
        }
    }

    private WorkerDay getWorkerDay(LocalDate weekStart, int i) {
        LocalDate nextDayDate = weekStart.plusDays(i);
        WorkerDay nextDay = new WorkerDay();

        nextDay.setDate(nextDayDate);

        DayType dayType;
        if (isHolidayDay(nextDayDate)) {
            dayType = DayType.holiday;
        } else {
            dayType = DayType.normal;
        }
        nextDay.setDayType(dayType);

        nextDay.setDayOfWeek(DayOfWeek.from(nextDayDate));

        if (nextDay.getDayOfWeek().equals(DayOfWeek.SATURDAY) || nextDay.getDayOfWeek().equals(DayOfWeek.SUNDAY)) {
            nextDay.setAppliedCategory(AppliedCategory.guard);
        } else {
            nextDay.setAppliedCategory(AppliedCategory.all);
        }
        return nextDay;
    }

    public void addHolidayDays(WorkerDay day) {
        this.holidayDays.add(day);
    }

    public WorkerDay getFirstWeek() {
        return new LinkedList<>(days).getFirst();
    }
    public WorkerDay getLastWeek() {
        return new LinkedList<>(days).getLast();
    }
    public boolean isHolidayDay(LocalDate date) {
        return holidayDays
                .stream().map(WorkerDay::getDate).toList()
                .contains(date);
    }
}
