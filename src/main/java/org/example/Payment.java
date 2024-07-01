package org.example;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.numbers.fraction.Fraction;
import org.example.Enum.WorkMoment;
import org.example.calendar.CheckInDay;
import org.example.calendar.WorkerCalendar;

import java.time.Month;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public class Payment {
    private Month name;
    private int totalHours;
    private WorkMoment shift;
    private String empName;
    private String empMatNum;
    private double totalAmount;
    private double netSalary;

    @ToString.Exclude
    private List<CheckInDay> checkInDays;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Fraction totalAmountFrac;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Fraction netSalaryFrac;

    public Payment(List<CheckInDay> checkInDays) {
        this.name = getCalendar(checkInDays).getMonth();
        this.checkInDays = checkInDays;
        this.empName = new LinkedList<>(checkInDays).getFirst().getEmpName();
        this.empMatNum = new LinkedList<>(checkInDays).getFirst().getEmpMatNum();
        setTotalAmount(checkInDays);
        setTotalHours(checkInDays);
        setShift(checkInDays);
        this.netSalaryFrac = this.totalAmountFrac.multiply(Fraction.from(0.8));
        this.netSalary = netSalaryFrac.doubleValue();
    }

    private WorkerCalendar getCalendar(List<CheckInDay> checkInDays) {
        return new LinkedList<>(checkInDays).getFirst().getCalendar();
    }
    private void setTotalAmount(List<CheckInDay> checkInDays) {
        Optional<Fraction> result = checkInDays.stream().map(CheckInDay::getAmountTodayFrac).reduce(Fraction::add);
        this.totalAmountFrac = result.orElse(Fraction.ONE);
        this.totalAmount = totalAmountFrac.doubleValue();
    }

    public void addCheckInDay(CheckInDay checkInDay) {
        this.checkInDays.add(checkInDay);
    }
    private void setTotalHours(List<CheckInDay> checkInDays) {
        this.totalHours = checkInDays.stream().mapToInt(CheckInDay::getHoursToday).sum();
    }
    private void setShift(List<CheckInDay> checkInDays) {
        this.shift = new LinkedList<>(checkInDays).getFirst().isNight() ? WorkMoment.night : WorkMoment.day;
    }
}
