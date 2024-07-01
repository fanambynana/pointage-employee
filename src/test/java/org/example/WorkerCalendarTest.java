package org.example;

//import lombok.extern.slf4j.Slf4j;
import org.example.Enum.AppliedCategory;
import org.example.Enum.DayType;
import org.example.Enum.WorkMoment;
import org.example.calendar.CheckInDay;
import org.example.calendar.WorkerCalendar;
import org.example.calendar.WorkerDay;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

//@Slf4j
public class WorkerCalendarTest {
    static List<WorkerDay> holidayDays = List.of(
            //new WorkerDay(LocalDate.parse("2024-06-17"), DayType.holiday, AppliedCategory.all),
            //new WorkerDay(LocalDate.parse("2024-06-25"), DayType.holiday, AppliedCategory.all),
            new WorkerDay(LocalDate.parse("2024-06-26"), DayType.holiday, AppliedCategory.all)
    );
    static Category regularCategory = new Category(
            AppliedCategory.regular,
            8,
            100_000d
    );
    static Category guardCategoryShiftDay = new Category(
            AppliedCategory.guard,
            70,
            100_000d
    );
    static Category guardCategoryShiftNight = new Category(
            AppliedCategory.guard,
            98,
            100_000d
    );
    static Employee employeeRakoto = new Employee(
            "AZERTYUIOP", "Rakoto",
            "EMP24001",
            LocalDate.parse("2001-03-16"),
            LocalDate.parse("2024-05-01"),
            LocalDate.parse("2026-05-01"),
            110_000d,
            guardCategoryShiftDay
    );
    static Employee employeeRabe = new Employee(
            "AZERTYUIOP", "Rabe",
            "EMP24001",
            LocalDate.parse("2001-05-26"),
            LocalDate.parse("2024-05-01"),
            LocalDate.parse("2026-05-01"),
            110_000d,
            guardCategoryShiftNight
    );
    static LocalDate weekStart = LocalDate.parse("2024-05-27");
    static LocalDate weekEnd = LocalDate.parse("2024-07-07");
    static WorkerCalendar calendar = new WorkerCalendar(Month.JUNE, holidayDays, weekStart, weekEnd);

    @Test
    void createCalendar27MayTo07JulyOk() {
        List<WorkerDay> days = calendar.getDays();

        assertNotNull(days);
        assertEquals(weekStart, new LinkedList<>(days).getFirst().getDate());
        assertEquals(weekEnd, new LinkedList<>(days).getLast().getDate());

        //log.info(calendar.toString());

        System.out.println(calendar);
    }
    @Test
    void employeeRakotoWorkOneDayOk() {
        CheckInDay checkInDay = employeeRakoto.work(LocalDateTime.parse("2024-06-01T08:00"),
                LocalDateTime.parse("2024-06-01T17:00"),
                calendar,
                false, false);
        assertNotEquals(0d, checkInDay.getAmountToday());

        //log.info(checkInDay.toString());

        System.out.println(checkInDay);

        //assertEquals(employeeRakoto.getSalaryPerHours() * 8, checkInDay.getAmountToday());
        //assertTrue(employeeRakoto.getPaymentDays().contains(paymentDay));
    }
    @Test
    void employeeRakotoWorkDailyOk() {
        Payment payment = employeeRakoto.workDaily(calendar, LocalTime.parse("07:00"), LocalTime.parse("17:00"));
        assertFalse(payment.getCheckInDays().isEmpty());

        assertEquals(641428.5714285715, payment.getTotalAmount());
        assertEquals(513142.85714285716, payment.getNetSalary());

        //log.info(payment.toString());

        System.out.println(payment);
    }
    @Test
    void employeeRabeWorkNightlyOk() {
        Payment payment = employeeRabe.workDaily(calendar, LocalTime.parse("17:00"), LocalTime.parse("07:00"));
        assertFalse(payment.getCheckInDays().isEmpty());
        //assertEquals(1_923_486, (int) checkInCalendar.getTotalAmount());

        assertEquals(833857.1428571428, payment.getTotalAmount());
        assertEquals(667085.7142857143, payment.getNetSalary());

        //log.info(payment.toString());

        System.out.println(payment);
    }
    @Test
    void RakotoWork_420Hs_Shift_Day_InJune() {
        Payment payment = employeeRakoto.workDaily(calendar, LocalTime.parse("07:00"), LocalTime.parse("17:00"));
        assertFalse(payment.getCheckInDays().isEmpty());
        assertEquals(420,  payment.getTotalHours());
        assertEquals(WorkMoment.day, payment.getShift());

        //log.info(String.valueOf(payment.getTotalHours()));

        System.out.println(payment.getTotalHours());
    }
    @Test
    void RabeWork_588Hs_Shift_Night_InJune() {
        Payment payment = employeeRabe.workDaily(calendar, LocalTime.parse("17:00"), LocalTime.parse("07:00"));
        assertFalse(payment.getCheckInDays().isEmpty());
        assertEquals(588,  payment.getTotalHours());
        assertEquals(WorkMoment.night, payment.getShift());

        //log.info(String.valueOf(payment.getTotalHours()));

        System.out.println(payment.getTotalHours());
    }
}
