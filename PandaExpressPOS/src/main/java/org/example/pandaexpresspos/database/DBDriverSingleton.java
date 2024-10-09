package org.example.pandaexpresspos.database;

import static java.lang.System.out;

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

    // Order
    public Order selectOrder(UUID orderId) {
        Order order = null;
        try {
            List<Order> orders = executeQuery(
                    String.format(QueryTemplate.selectEmployee, orderId),
                    SQLToJavaMapper::orderMapper
            );

            if (orders.isEmpty()) {
                throw new SQLException("Employee not found");
            }
            order = orders.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
    }

    public List<Order> selectOrders(Integer mostRecent) {
        Order order = null;
        try {
            List<Order> orders = executeQuery(
                    String.format(QueryTemplate.selectRecentOrders, orderId),
                    SQLToJavaMapper::orderMapper
            );

            if (orders.isEmpty()) {
                throw new SQLException("Employee not found");
            }
            order = orders.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return order;
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

    public List<Employee> selectEmployees() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void insertEmployee(Employee newEmployee)
    {
        executeUpdate(String.format(QueryTemplate.insertEmployee,
                newEmployee.employeeID,
                newEmployee.isManager,
                newEmployee.name
        ));
    }

    public void updateEmployee(Employee updatedEmployee) {
        executeUpdate(String.format(QueryTemplate.updateEmployee,
                updatedEmployee.isManager,
                updatedEmployee.name,
                updatedEmployee.employeeID
        ));
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





//         Test logic to place order
            MenuItem newMenu = new MenuItem(
//                    UUID.randomUUID(),
                    UUID.fromString("a18045d1-1e46-4104-8b6c-c8f65eb3bf27"),
                    420.69,
                    420,
                    "Lean"

            );

        System.out.println("UUID: " + newMenu.menuItemId +"\nCost: " + newMenu.price + "\nStock: " + newMenu.availableStock + "\nItem Name:" + newMenu.itemName);
        instance.updateMenuItem(newMenu);
//          InventoryItem newItem = new InventoryItem(
////                  UUID.randomUUID(),
//                  UUID.fromString("0e2ab524-0593-4648-86e7-90a9ea102e2f"),
//                  69.69,
//                  690,
//                  "Codeine"
//          );
//        System.out.println("UUID: " + newItem.inventoryItemId +"\nCost: " + newItem.cost + "\nStock: " + newItem.availableStock + "\nItem Name:" + newItem.itemName);
//        instance.updateInventoryItem(newItem);

//        Employee newemployee = new Employee(
//                UUID.randomUUID(),
//                UUID.fromString("aa3b94bb-4dc2-4d4b-b20b-c23646a1237e"),
//                true,
//                "Ryandumb"
//        );
//        instance.updateEmployee(newemployee);
//        instance.insertEmployee(newemployee);
//
//        System.out.println("UUID: " + newemployee.employeeID +"\nName: " + newemployee.name + "\nisManager: " + newemployee.isManager);

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

//
//        Employee emp = instance.selectEmployee(UUID.fromString("01e98262-a7a7-41f1-b347-6df6081f4563"));
//        System.out.println("Name: " + emp.name + "\nisManager: " + emp.isManager);
    }
}
