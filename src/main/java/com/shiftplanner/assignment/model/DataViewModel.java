package com.shiftplanner.assignment.model;

import java.util.ArrayList;
import java.util.List;

public class DataViewModel {
    private List<Shift> shifts = new ArrayList<Shift>();
    private List<Employee> employees = new ArrayList<Employee>();

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }
}

