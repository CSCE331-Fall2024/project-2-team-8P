package org.example.pandaexpresspos.models;

import java.util.UUID;

public class Employee {
    public UUID employeeID;
    public Boolean isManager;
    public String name;

    public Employee(UUID employeeID, Boolean isManager, String name) {
        this.employeeID = employeeID;
        this.isManager = isManager;
        this.name = name;
    }

    public Employee(Boolean isManager, String name) {
        this(UUID.randomUUID(), isManager, name);
    }


}
