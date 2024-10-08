package org.example.pandaexpresspos.database;

import static java.lang.System.out;

import javafx.scene.control.Menu;
import org.example.pandaexpresspos.models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class DBDriverSingleton {

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

    public void insertOrder(Order newOrder) {
        // Insert the order entry
        executeUpdate(String.format(QueryTemplate.insertOrder,
                newOrder.orderId,
                newOrder.cashierId,
                newOrder.month,
                newOrder.week,
                newOrder.day,
                newOrder.hour,
                newOrder.price
        ));

        // Handle inventory item connections:
        for (InventoryItem item : newOrder.inventoryItems.keySet()) {
            Integer quantity = newOrder.inventoryItems.get(item);

            // Insert entry into table "orderToInventoryItem"
            executeUpdate(String.format(QueryTemplate.insertOrderToInventoryItem,
                    newOrder.orderId,
                    item.inventoryItemId,
                    quantity
            ));

            // Update quantity of item in inventory table
            executeUpdate(String.format(QueryTemplate.updateInventoryItemQty,
                    quantity,
                    item.inventoryItemId
            ));
        }

        // Handle menu item connections:
        for (MenuItem item : newOrder.menuItems.keySet()) {
            Integer quantity = newOrder.menuItems.get(item);

            // Insert entry into table "orderToMenuItem"
            executeUpdate(String.format(QueryTemplate.insertOrderToMenuItem,
                    newOrder.orderId,
                    item.menuItemId,
                    quantity
            ));

            // Update quantity of item in menu item table
            executeUpdate(String.format(QueryTemplate.updateMenuItemQty,
                    quantity,
                    item.menuItemId
            ));
        }
    }

    public void updateOrder(Order updatedOrder) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteOrder(UUID orderId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Employee
    public Employee selectEmployee(UUID employeeId) throws SQLException {
        // 1. Accepts employeeId
        // 2. Executes a select SQL query
        // 3. Puts the information retrieved from the DB into an `Employee` object and returns it
        String query = String.format(QueryTemplate.selectEmployee, employeeId);
        List<Employee> employees = executeQuery(query, SQLToJavaMapper::employeeMapper);

        if (employees.isEmpty()) {
            throw new SQLException("Employee not found");
        }
        return employees.getFirst();
    }

    public List<Employee> selectEmployees() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertEmployee(Employee newEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateEmployee(Order updatedEmployee) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteEmployee(UUID employeeId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Inventory
    public InventoryItem selectInventoryItem(UUID inventoryItemId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<InventoryItem> selectInventoryItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertInventoryItem(InventoryItem newInventoryItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateInventoryItem(InventoryItem newInventoryItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteInventoryItem(UUID inventoryItemId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Menu items
    public MenuItem selectMenuItem(UUID menuItemId) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public List<MenuItem> selectMenuItems() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertMenuItem(MenuItem newMenuItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void updateMenuItem(MenuItem newMenuItem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteMenuItem(UUID menuItemId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // Private helpers:

    // TODO: it may be slow to reconnect every time we need to execute a query if we have multiple back-to-back
    // This is used for:
    // 1. Select
    private static <T> List<T> executeQuery(String query, Function<ResultSet, T> mapper) throws SQLException {
        List<T> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                DBCredentials.dbConnectionString,
                DBCredentials.username,
                DBCredentials.passwd);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                T item = mapper.apply(rs);
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
    private static void executeUpdate(String query) {
        try (Connection conn = DriverManager.getConnection(
                DBCredentials.dbConnectionString,
                DBCredentials.username,
                DBCredentials.passwd);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
        } catch (SQLException e) {
            // TODO (maybe): display stack trace graphically somewhere
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws SQLException {
        DBDriverSingleton instance = DBDriverSingleton.getInstance();

        // Test logic to place order
//        Order newOrder = new Order(
//                UUID.randomUUID(),
//                UUID.fromString("1bbd8bdf-defc-4d6d-8cb0-7b4a5989c7ba"),
//                8,
//                35,
//                20,
//                8,
//                50.0
//        );
//        newOrder.addInventoryItem(new InventoryItem(
//                UUID.fromString("824579a8-0a41-440e-b3dd-dd90cd961b03"),
//                0.1,
//                1000,
//                "Napkin"),
//                2);
//        newOrder.addMenuItem(new MenuItem(
//                UUID.fromString("59e65561-bb65-4cf6-8d9d-965f272f55c8"),
//                2.1,
//                835,
//                "Dr. Pepper"),
//                1
//        );
//
//        out.println("Order ID: " + newOrder.orderId);
//        instance.insertOrder(newOrder);

        Employee emp = instance.selectEmployee(UUID.fromString("01e98262-a7a7-41f1-b347-6df6081f4563"));
        System.out.println("Name: " + emp.name + "\nisManager: " + emp.isManager);
    }
}
