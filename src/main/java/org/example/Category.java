package org.example;

import lombok.Data;
import org.example.Enum.AppliedCategory;

@Data
public class Category {
    private final AppliedCategory name;
    private final int hoursPerWeek;
    private final double weeklySalary;
    private final double indemnityAmount;

    public Category(AppliedCategory name, int hoursPerWeek, double weeklySalary) {
        this.name = name;
        this.hoursPerWeek = hoursPerWeek;
        this.weeklySalary = weeklySalary;
        this.indemnityAmount = 0d;
    }

    public Category(AppliedCategory name, int hoursPerWeek, double weeklySalary, double indemnityAmount) {
        this.name = name;
        this.hoursPerWeek = hoursPerWeek;
        this.weeklySalary = weeklySalary;
        this.indemnityAmount = indemnityAmount;
    }
}
