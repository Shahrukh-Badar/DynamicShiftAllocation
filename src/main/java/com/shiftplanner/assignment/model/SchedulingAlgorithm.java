package com.shiftplanner.assignment.model;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SchedulingAlgorithm {
    // To get more accuracy in term of matching desired or actual earning, increase the shift and employee count.
    public static int numberOfEmployee = 10; //1000
    public static int numberOfShifts = 40; //40000
    public static boolean debugMessageOn = false;


    public static void process() {

        if (numberOfEmployee == 0 || numberOfShifts == 0)
            return;
        if (numberOfEmployee > 100000 || numberOfShifts > 100000)
            return;

        //To Generate Data
        DataViewModel dataViewModelResult = generateData();
        List<Employee> employees = dataViewModelResult.getEmployees();
        List<Shift> shifts = dataViewModelResult.getShifts();

        int noOfEmployees = numberOfEmployee, noOfShifts = numberOfShifts;
        AtomicInteger employeeIterator = new AtomicInteger();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        // Iterate through all shifts
        shifts.forEach((shift) -> {

            // Employee circular iterator.
            Employee currentEmployee = employees.get(employeeIterator.get());
            Double currentEmployeeDesiredEarning = currentEmployee.getDesiredEarnings();

            // Get already assigned shifts for current Employee to
            // find overlapping and recent total earning from shifts.
            Predicate<Shift> employeePredicate = u -> (u.getAssignedEmployee() != null && u.getAssignedEmployee().getName() == currentEmployee.getName());
            List<Shift> employeeShifts = shifts.stream()
                    .peek(c -> {
                        if (c.getAssignedEmployee() == null) {
                        }
                    }).filter(employeePredicate).collect(Collectors.toList());

            // Check if the shift is already assigned or not, we dont need this condition as we are iterating item by item
            if (!shift.getAssigned()) {
                // For first time, because there is no overlapping in first assignment.
                if (employeeShifts.size() == 0) {
                    if (shift.getHourlyEarnings() <= currentEmployeeDesiredEarning) {
                        // Assign shift if shift earning is less than or equal to desired earning.
                        shift.setAssignedEmployee(currentEmployee);
                        shift.setAssigned(true);
                        //To see log, please enable global variable debugMessageOn to true.
                        debugMessage(currentEmployee.getName() + ", First Shift Assigned: " + shift.getStart().format(formatter) + "  " + shift.getEnd().format(formatter));
                    }
                } else {
                    // Sum the total of already assigned shifts.
                    double totalEarnings = employeeShifts.stream().mapToDouble(f -> f.getHourlyEarnings()).sum();
                    AtomicReference<Boolean> isOverlapping = new AtomicReference<>(false);

                    //Iterate through already assigned shifts in case of overlapping, if overlapping found the current shift will be skip.
                    employeeShifts.forEach((employeeShift) ->
                    {
                        if (shift.getStart().isAfter(employeeShift.getStart()) && shift.getStart().isBefore(employeeShift.getEnd())) {
                            isOverlapping.set(true);
                            // To check the overlapping shifts, please enable global variable debugMessageOn to true.
                            debugMessage(currentEmployee.getName() + ", Shift " + shift.getStart().format(formatter) + "  " + shift.getEnd().format(formatter) + " is overlapping " +
                                    "existing shift " + employeeShift.getStart().format(formatter) + "  " + employeeShift.getEnd().format(formatter));
                        }
                    });
                    // Assigned to employee if not overlapping w.r.t employee desired earning.
                    if (!isOverlapping.get()) {
                        if ((totalEarnings + shift.getHourlyEarnings()) <= currentEmployeeDesiredEarning) {
                            shift.setAssignedEmployee(currentEmployee);
                            shift.setAssigned(true);
                            // To check the assign shifts, please enable global variable debugMessageOn to true.
                            debugMessage(currentEmployee.getName() + ", Shift Assigned: " + shift.getStart().format(formatter) + "  " + shift.getEnd().format(formatter));
                        }
                    }
                }
            }
            // For circular iteration on employees
            employeeIterator.incrementAndGet();
            if (employeeIterator.get() == noOfEmployees)
                employeeIterator.set(0);
        });


        // Printing output
        employees.forEach((employee) -> {
            Predicate<Shift> employeePredicate = u -> (u.getAssignedEmployee() != null && u.getAssignedEmployee().getName() == employee.getName());
            List<Shift> employeeShifts = shifts.stream()
                    .peek(c -> {
                        if (c.getAssignedEmployee() == null) {
                        }
                    }).filter(employeePredicate).collect(Collectors.toList());

            double totalEarnings = employeeShifts.stream().mapToDouble(f -> f.getHourlyEarnings()).sum();
            System.out.println(employee.getName() + ", desired earning: " + decimalFormat.format(employee.getDesiredEarnings()) + " and actual earning: " + decimalFormat.format(totalEarnings));
        });

        System.out.println(System.getProperty("line.separator"));

        // Printing output
        shifts.forEach((shift) -> {
            if (shift.getAssignedEmployee() != null)
                System.out.println("Shift [" + shift.getStart().format(formatter) + "  " + shift.getEnd().format(formatter) + "] has been assigned to " + shift.getAssignedEmployee().getName());
        });
    }

    // Generate Data of Shifts and Employees
    public static DataViewModel generateData() {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        DataViewModel dataViewModelResult = new DataViewModel();
        int noOfEmployees = numberOfEmployee, noOfShifts = numberOfShifts;
        List<Employee> employees = new ArrayList<Employee>();
        Employee employee;
        double randomNum = 0;
        // Start and end date according to requirement
        LocalDateTime shiftStartDateTime = LocalDateTime.of(2020, Month.AUGUST, 1, 0, 0, 0),
                shiftEndDateTime = LocalDateTime.of(2020, Month.AUGUST, 5, 0, 0, 0);
        List<LocalDateTime> shiftDuration;
        List<Shift> shifts = new ArrayList<Shift>();
        Shift shift;

        // Populating employees data
        for (int i = 0; i < noOfEmployees; i++) {
            employee = new Employee();
            employee.setName("Employee".concat(" ").concat(Integer.toString(i + 1)));
            randomNum = ThreadLocalRandom.current().nextDouble(30, 91);
            employee.setDesiredEarnings(Double.parseDouble(decimalFormat.format(randomNum)));
            employees.add(employee);
        }

        // Populating shifts data
        for (int i = 0; i < noOfShifts; i++) {
            shift = new Shift();
            shiftDuration = generateShiftDuration(shiftStartDateTime, shiftEndDateTime);
            randomNum = ThreadLocalRandom.current().nextDouble(1, 11);
            shift.setStart(shiftDuration.get(0));
            shift.setEnd(shiftDuration.get((1)));
            shift.setHourlyEarnings(Double.parseDouble(decimalFormat.format(randomNum)));
            shift.setAssigned(false);
            shifts.add(shift);
        }

        dataViewModelResult.setEmployees(employees);
        dataViewModelResult.setShifts(shifts);
        return dataViewModelResult;
    }

    // Used to generate random shift of two hours duration.
    public static List<LocalDateTime> generateShiftDuration(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<LocalDateTime> shiftDuration = new ArrayList<LocalDateTime>();
        LocalDateTime shiftStartDateTime, shiftEndDateTime;
        int startYear = startDateTime.getYear(), endYear = endDateTime.getYear(),
                startMonth = startDateTime.getMonth().getValue(), endMonth = endDateTime.getMonth().getValue(),
                startDay = startDateTime.getDayOfMonth(), endDay = endDateTime.getDayOfMonth(),
                randomDay = 0, randomHour = 0, randomMinute = 0, randomYear = 0, randomMonth = 0;

        randomYear = ThreadLocalRandom.current().nextInt(startYear, endYear + 1);
        randomMonth = ThreadLocalRandom.current().nextInt(startMonth, endMonth + 1);
        randomDay = ThreadLocalRandom.current().nextInt(startDay, endDay + 1);
        randomHour = ThreadLocalRandom.current().nextInt(0, 23);
        randomMinute = ThreadLocalRandom.current().nextInt(0, 59);
        randomMinute = round(randomMinute) != 60 ? round(randomMinute) : round(randomMinute) - 10;

        shiftStartDateTime = LocalDateTime.of(randomYear, randomMonth, randomDay, randomHour, randomMinute, 0);
        shiftEndDateTime = shiftStartDateTime.plusHours(2);

        shiftDuration.add(shiftStartDateTime);
        shiftDuration.add(shiftEndDateTime);

        return shiftDuration;
    }

    /// Taken from internet to Round number to next tens
    public static int round(int n) {
        // Smaller multiple
        int a = (n / 10) * 10;
        // Larger multiple
        int b = a + 10;
        // Return of closest of two
        return (n - a > b - n) ? b : a;
    }

    public static void debugMessage(String message) {
        if (debugMessageOn)
            System.out.println(message);
    }


}
