package com.shiftplanner.assignment.model;

import java.time.LocalDateTime;

public class Shift {
    private LocalDateTime start;
    private LocalDateTime end;
    private Double hourlyEarnings;
    private Employee assignedEmployee;
    private Boolean isAssigned;

    public Boolean getAssigned() {
        return isAssigned;
    }

    public void setAssigned(Boolean assigned) {
        isAssigned = assigned;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public Double getHourlyEarnings() {
        return hourlyEarnings;
    }

    public void setHourlyEarnings(Double hourlyEarnings) {
        this.hourlyEarnings = hourlyEarnings;
    }

    public Employee getAssignedEmployee() {
        return assignedEmployee;
    }

    public void setAssignedEmployee(Employee assignedEmployee) {
        this.assignedEmployee = assignedEmployee;
    }


}
