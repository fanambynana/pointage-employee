package org.example;

import lombok.*;
import org.apache.commons.numbers.fraction.Fraction;
import org.example.Enum.AppliedCategory;
import org.example.calendar.CheckInDay;
import org.example.calendar.WorkerCalendar;
import org.example.calendar.WorkerDay;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class Employee {
    private String name;
    private String firstname;
    private String matriculationNumber;
    private LocalDate birthDate;
    private LocalDate hiringDate;
    private LocalDate contractEndDate;
    private double salaryAmount;
    private Category category;

    @Setter(value = AccessLevel.NONE)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Payment payment;

    public Employee(String name, String firstname, String matriculationNumber, LocalDate birthDate, LocalDate hiringDate, LocalDate contractEndDate, double salaryAmount, Category category) {
        this.name = name;
        this.firstname = firstname;
        this.matriculationNumber = matriculationNumber;
        this.birthDate = birthDate;
        this.hiringDate = hiringDate;
        this.contractEndDate = contractEndDate;
        this.salaryAmount = salaryAmount;
        this.category = category;
    }

    public Fraction getSalaryPerHours() {
        double salaryPerWeek = category.getWeeklySalary();
        int hoursPerWeek = category.getHoursPerWeek();

        return Fraction.of((int) salaryPerWeek, hoursPerWeek); // ( (hoursPerWeek - hoursPerDay) + (hoursPerDay * sundayPrimeTime));
    }
    private void addCheckInDay(CheckInDay checkInDay) {
        this.payment.addCheckInDay(checkInDay);
    }
    public Payment workDaily(WorkerCalendar calendar, LocalTime startHours, LocalTime endHours) {
        List<CheckInDay> checkInDayList = new ArrayList<>();

        for (WorkerDay day: calendar.getDays()) {
            boolean isAllowedToBreak = !category.getName().equals(AppliedCategory.guard);
            LocalDate today = day.getDate();
            LocalDateTime endDatetime = startHours.isAfter(endHours) ? today.plusDays(1).atTime(endHours) : today.atTime(endHours);
            CheckInDay checkInDay = new CheckInDay(this, calendar,
                    today.atTime(startHours),
                    endDatetime,
                    false,
                    isAllowedToBreak
            );
            checkInDayList.add(checkInDay);
        }
        this.payment = new Payment(checkInDayList);
        return this.payment;
    }
    public CheckInDay work(LocalDateTime start, LocalDateTime end, WorkerCalendar calendar, boolean isOverTime, boolean haveABreak) {
        CheckInDay checkInDay = new CheckInDay(this, calendar, start, end, isOverTime, haveABreak);
        addCheckInDay(checkInDay);
        return checkInDay;
    }
}
