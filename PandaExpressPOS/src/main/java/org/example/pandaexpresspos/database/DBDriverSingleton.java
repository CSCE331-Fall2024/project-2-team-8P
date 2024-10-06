package org.example.pandaexpresspos.database;

import static java.lang.System.out;

import org.example.pandaexpresspos.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DBDriverSingleton {

    @FunctionalInterface
    protected interface ResultSetMapper<T> {
        T map(ResultSet resultSet) throws SQLException;
    }

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

    public List<Order> selectOrders(Integer mostRecent) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<Order> selectOrders(Integer startDate, Integer endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer insertOrder(Order newOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer updateOrder(Order updatedOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer deleteOrder(UUID orderId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // TODO: implement functionality to place an order


    // Employee
    public Employee selectEmployee(UUID employeeId) throws SQLException {
        // 1. Accepts employeeId
        // 2. Executes a select SQL query
        // 3. Puts the information retrieved from the DB into an `Employee` object and returns it
        String queryTemplate = """
                SELECT * FROM employee
                WHERE employeeid = '%s';
                """;

        ResultSetMapper<Employee> employeeMapper = rs -> new Employee(
                UUID.fromString(rs.getString("employeeid")),
                rs.getBoolean("ismanager"),
                rs.getString("name")
        );

        List<Employee> employees = executeQuery(
                queryTemplate,
                employeeMapper,
                employeeId.toString());

        if (employees.isEmpty()) {
            throw new SQLException("Employee not found");
        }
        return employees.getFirst();
    }

    public List<Employee> selectEmployees() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer insertEmployee(Employee newEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer updateEmployee(Order updatedEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer deleteEmployee(UUID employeeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Inventory
    public InventoryItem selectInventoryItem(UUID inventoryItemId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<InventoryItem> selectInventoryItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer insertInventoryItem(InventoryItem newInventoryItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer updateInventoryItem(InventoryItem newInventoryItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer deleteInventoryItem(UUID inventoryItemId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Menu items
    public MenuItem selectMenuItem(UUID menuItemId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<MenuItem> selectMenuItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer insertMenuItem(MenuItem newMenuItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer updateMenuItem(MenuItem newMenuItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Integer deleteMenuItem(UUID menuItemId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Private helpers:

    // This is used for:
    // 1. Select
    private static <T> List<T> executeQuery(String template, ResultSetMapper<T> mapper, Object... args) {
        List<T> results = new ArrayList<>();
        String query = String.format(template, args);

        try (Connection conn = DriverManager.getConnection(
                DBCredentials.dbConnectionString,
                DBCredentials.username,
                DBCredentials.passwd);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                T item = mapper.map(rs);
                results.add(item);
            }

            rs.close();
        } catch (SQLException e) {
            // TODO (maybe): display stack trace graphically somewhere
            e.printStackTrace();
        }

        return results;
    }

    // This is used for:
    // 1. Insert
    // 2. Update
    // 3. Delete
    private static Integer executeUpdate(String template, Object... args) {
        int rowsUpdated = 0;
        String query = String.format(template, args);

        try (Connection conn = DriverManager.getConnection(
                DBCredentials.dbConnectionString,
                DBCredentials.username,
                DBCredentials.passwd);
             Statement stmt = conn.createStatement()) {

            rowsUpdated = stmt.executeUpdate(query);
        } catch (SQLException e) {
            // TODO (maybe): display stack trace graphically somewhere
            e.printStackTrace();
        }

        return rowsUpdated;
    }


    public static void main(String[] args) throws SQLException {
        out.println("Soham sucks!!!!");
        DBDriverSingleton instance = DBDriverSingleton.getInstance();
        Employee employee =
                instance.selectEmployee(UUID.fromString("01e98262-a7a7-41f1-b347-6df6081f4563"));

        out.println("Name: " + employee.name + "\nisManager: " + employee.isManager);
    }
}
