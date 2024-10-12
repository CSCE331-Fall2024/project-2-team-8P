package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    // Order
    public Order selectOrder(UUID orderId) {
        Order order = null;
        try {
            List<Order> orders = executeQuery(
                    String.format(QueryTemplate.selectOrder, orderId),
                    SQLToJavaMapper::orderMapper
            );
            if (orders.isEmpty()) {
                throw new SQLException("Order not found");
            }
            order = orders.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    public List<Order> selectOrders(Integer mostRecent) {
        List<Order> orders = null;
        try {
            int currentMonth = LocalDate.now().getMonthValue();
            orders = executeQuery(
                    String.format(QueryTemplate.selectRecentOrders, currentMonth, mostRecent),
                    SQLToJavaMapper::orderMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Map<Integer, Double> selectOrders(Integer startDate, Integer endDate) {
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
            executeUpdate(String.format(QueryTemplate.decreaseInventoryItemQty,
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
            executeUpdate(String.format(QueryTemplate.decreaseMenuItemQty,
                    quantity,
                    item.menuItemId
            ));
        }
    }

    public void updateOrder(Order updatedOrder) {
        executeUpdate(String.format(QueryTemplate.updateOrder,
                updatedOrder.month,
                updatedOrder.week,
                updatedOrder.day,
                updatedOrder.hour,
                updatedOrder.price,
                updatedOrder.orderId
        ));
    }

    // Employee
    public Employee selectEmployee(UUID employeeId) {
        // 1. Accepts employeeId
        // 2. Executes a select SQL query
        // 3. Puts the information retrieved from the DB into an `Employee` object and returns it
        Employee employee = null;
        try {
            List<Employee> employees = executeQuery(
                    String.format(QueryTemplate.selectEmployee, employeeId),
                    SQLToJavaMapper::employeeMapper
            );
            if (employees.isEmpty()) {
                throw new SQLException("Employee not found");
            }
            employee = employees.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public Employee selectEmployee(String name) {
        Employee employee = null;
        try {
            List<Employee> employees = executeQuery(
                    String.format(QueryTemplate.selectEmployeeByName, name),
                    SQLToJavaMapper::employeeMapper
            );
            if (employees.isEmpty()) {
                throw new SQLException("Employee not found");
            }
            employee = employees.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    public List<Employee> selectEmployees() {
        List<Employee> employees = null;
        try {
            employees = executeQuery(
                    String.format(QueryTemplate.selectAllEmployees),
                    SQLToJavaMapper::employeeMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employees;
    }

    public void insertEmployee(Employee newEmployee) {
        executeUpdate(String.format(QueryTemplate.insertEmployee,
                newEmployee.employeeId,
                newEmployee.isManager,
                newEmployee.name
        ));
    }

    public void updateEmployee(Employee updatedEmployee) {
        executeUpdate(String.format(QueryTemplate.updateEmployee,
                updatedEmployee.isManager,
                updatedEmployee.name,
                updatedEmployee.employeeId
        ));
    }

    // Inventory
    public InventoryItem selectInventoryItem(UUID inventoryItemId) {
        InventoryItem item = null;
        try {
            List<InventoryItem> items = executeQuery(
                    String.format(QueryTemplate.selectInventoryItem, inventoryItemId),
                    SQLToJavaMapper::inventoryItemMapper
            );

            if (items.isEmpty()) {
                throw new SQLException("Inventory Item not found");
            }
            item = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<InventoryItem> selectInventoryItems() {
        List<InventoryItem> items = null;
        try {
            items = executeQuery(
                    String.format(QueryTemplate.selectAllInventoryItems),
                    SQLToJavaMapper::inventoryItemMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void insertInventoryItem(InventoryItem newInventoryItem) {
        executeUpdate(String.format(QueryTemplate.insertInventoryItem,
                newInventoryItem.inventoryItemId,
                newInventoryItem.cost,
                newInventoryItem.availableStock,
                newInventoryItem.itemName
        ));
    }

    public void updateInventoryItem(InventoryItem updatedInventoryItem) {
        executeUpdate(String.format(QueryTemplate.updateInventoryItem,
                updatedInventoryItem.cost,
                updatedInventoryItem.availableStock,
                updatedInventoryItem.itemName,
                updatedInventoryItem.inventoryItemId
        ));
    }

    // Menu items
    public MenuItem selectMenuItem(UUID menuItemId) {
        MenuItem item = null;
        try {
            List<MenuItem> items = executeQuery(
                    String.format(QueryTemplate.selectMenuItem, menuItemId),
                    SQLToJavaMapper::menuItemMapper
            );

            if (items.isEmpty()) {
                throw new SQLException("Menu Item not found");
            }
            item = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return item;
    }

    public List<MenuItem> selectMenuItems() {
        List<MenuItem> items = null;
        try {
            items = executeQuery(
                    String.format(QueryTemplate.selectAllMenuItems),
                    SQLToJavaMapper::menuItemMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void insertMenuItem(MenuItem newMenuItem) {
        executeUpdate(String.format(QueryTemplate.insertMenuItem,
                newMenuItem.menuItemId,
                newMenuItem.price,
                newMenuItem.availableStock,
                newMenuItem.itemName
        ));
    }

    public void updateMenuItem(MenuItem updatedMenuItem) {
        executeUpdate(String.format(QueryTemplate.updateMenuItem,
                updatedMenuItem.price,
                updatedMenuItem.availableStock,
                updatedMenuItem.itemName,
                updatedMenuItem.menuItemId
        ));
    }

    // Private helpers:

    // TODO: it may be slow to reconnect every time we need to execute a query if we have multiple back-to-back
    // This is used for:
    // 1. Select
    private static <T> List<T> executeQuery(String query, Function<ResultSet, T> mapper) throws SQLException { //This function is used to execute a query such as selecting
        List<T> results = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(
                DBCredentials.dbConnectionString,
                DBCredentials.username,
                DBCredentials.passwd);
             Statement stmt = conn.createStatement()) {

            ResultSet rs = stmt.executeQuery(query);

            if (!rs.isBeforeFirst()) {
                throw new SQLException("Query returned empty result");
            }

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
    private static void executeUpdate(String query) { //This function is used to update a query such as inserting
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
}
