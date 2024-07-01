package org.example.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.Enum.AppliedCategory;
import org.example.Enum.DayType;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class WorkerDay {
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private DayType dayType;
    private AppliedCategory appliedCategory;

    public WorkerDay(LocalDate date, DayType dayType, AppliedCategory appliedCategory) {
        this.date = date;
        this.appliedCategory = appliedCategory;
        this.dayOfWeek = DayOfWeek.from(date);
        this.dayType = dayType;
    }
    public WorkerDay(LocalDate date, AppliedCategory appliedCategory) {
        this.date = date;
        this.appliedCategory = appliedCategory;
        this.dayOfWeek = DayOfWeek.from(date);
        this.dayType = DayType.normal;
    }
}
