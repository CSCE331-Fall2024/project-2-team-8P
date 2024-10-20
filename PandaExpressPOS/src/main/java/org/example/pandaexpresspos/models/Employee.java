package org.example.pandaexpresspos.models;

import java.util.UUID;

/**
 * The Employee class represents an employee in the Panda Express POS system.
 * It stores information about the employee, including their ID, whether they are a manager, and their name.
 */
public class Employee {
    /** Unique identifier for the employee */
    public UUID employeeId;

    /** Boolean indicating if the employee is a manager */
    public Boolean isManager;

    /** The name of the employee */
    public String name;

    /**
     * Constructor to create an Employee with a specified ID.
     *
     * @param employeeID the unique ID of the employee
     * @param isManager whether the employee is a manager
     * @param name the name of the employee
     */
    public Employee(UUID employeeID, Boolean isManager, String name) {
        this.employeeId = employeeID;
        this.isManager = isManager;
        this.name = name;
    }

    /**
     * Constructor to create an Employee with an automatically generated ID.
     *
     * @param isManager whether the employee is a manager
     * @param name the name of the employee
     */
    public Employee(Boolean isManager, String name) {
        this(UUID.randomUUID(), isManager, name);
    }
}
