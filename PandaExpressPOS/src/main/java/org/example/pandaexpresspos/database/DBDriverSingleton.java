package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.util.List;
import java.util.UUID;

public class DBDriverSingleton {
    private static final DBCredentials dbCredentials = new DBCredentials();
    private static DBDriverSingleton instance;

    // Why a private constructor?
    // This is how we implement the *singleton* design pattern
    private DBDriverSingleton() {
    }

    public static DBDriverSingleton getInstance() {
        if (instance == null) {
            instance = new DBDriverSingleton();
        }
        return instance;
    }

    // Public methods:
    /*
    TODO: since the method signatures look very similar for all of the database
     tables we could perhaps make them generic
     */

    // Order
    public Order selectOrder(UUID orderId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boolean insertOrder(Order newOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boolean updateOrder(Order updatedOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> getOrderHistory(Integer mostRecent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> getOrderHistory(Integer startDate, Integer endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Employee
    public Order selectEmployee(UUID employeeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boolean insertEmployee(Employee newEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Boolean updateEmployee(Order updatedEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Employee> getEmployeeSnapshot() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // To be implemented: inventory, menu items

    // Private helpers:
    // Generic method that all public methods can call to execute SQL queries
    private static Boolean executeQuery(String... args) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
