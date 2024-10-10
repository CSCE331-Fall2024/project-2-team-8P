package org.example.pandaexpresspos.models;

import java.util.UUID;

public class Employee {
    public UUID employeeId;
    public Boolean isManager;
    public String name;

    public Employee(UUID employeeID, Boolean isManager, String name) {
        this.employeeId = employeeID;
        this.isManager = isManager;
        this.name = name;
    }

    public Employee(Boolean isManager, String name) {
        this(UUID.randomUUID(), isManager, name);
    }


}
