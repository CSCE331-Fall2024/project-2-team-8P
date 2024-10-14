package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    // The indices in the returned list correspond to hours of the day - 1
    // e.g., index 0 corresponds to hour 1
    public List<Double> selectXReport() {
        List<Double> xReport = null;
        try {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentDay = LocalDate.now().getDayOfMonth();

            // Workday starts at 10am and ends at 10pm
            int currentHour = (LocalDateTime.now().getHour() - 10) % 12 + 1;

            xReport = executeQuery(
                    String.format(QueryTemplate.selectOrderSumsByHour, currentMonth, currentDay, currentHour),
                    SQLToJavaMapper::orderSumMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return xReport;
    }

    // The indices in the returned list correspond to hours of the day - 1
    // e.g., index 0 corresponds to hour 1
    public List<Double> selectZReport() {
        List<Double> zReport = null;
        try {
            int currentMonth = LocalDate.now().getMonthValue();
            int currentDay = LocalDate.now().getDayOfMonth();
            final int totalWorkingHoursPerDay = 12;

            zReport = executeQuery(
                    String.format(QueryTemplate.selectOrderSumsByHour, currentMonth, currentDay, totalWorkingHoursPerDay),
                    SQLToJavaMapper::orderSumMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return zReport;
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

    public void deleteMenuItem(UUID menuItemId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    public HashMap<String,Number> reportSales(Integer startMonth, Integer endMonth, Integer startDay, Integer endDay) {
        //This function will return a hashmap of the sales of each menu item in the given time frame
        //Heads up this function call is a little slow because of the for loop iterating through all the menu items
        HashMap<String, Number> sales = new HashMap<String, Number>();
        try{
            List<MenuItem> menuItem = selectMenuItems();
            for (MenuItem item : menuItem) {
                executeQuery(String.format(QueryTemplate.salesOfMenuItem, item.menuItemId, startMonth, endMonth, startDay, endDay), rs -> {
                    try {
                        int count = rs.getInt(1);
                        sales.put(item.itemName, count);
                        return sales;
                    } catch (SQLException e) {
                        return sales;
                    }
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return sales;
    }

    public HashMap<String, Integer> productUsageReport(Integer startMonth, Integer endMonth, Integer startDay, Integer endDay) {
        //This function will return a hashmap of the usage of each inventory item in the given time frame
        //Heads up this function call is a little slow because of the for loop iterating through all the inventory items along with multiplying with another hash map

        HashMap<String, Integer> product = new HashMap<String, Integer>();
        HashMap<String, Number> sales = reportSales(startMonth, endMonth, startDay, endDay);
        try{
            List<InventoryItem> inventoryItem = selectInventoryItems();
            for (InventoryItem item : inventoryItem) {
                    executeQuery(String.format(QueryTemplate.associateInventoryItemToMenuItem), rs -> {
                        return product;
                    });
            }
            return product;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
        // Private helpers:
     public HashMap<String,String> inventoryToMenu() {
         //This function will return a hashmap of the inventory item to menu item
         //This function is used in the productUsageReport function
         HashMap<String, String> inventoryToMenu = new HashMap<String, String>();
         try {
             List<MenuItem> menuItem = selectMenuItems();
             for (MenuItem item : menuItem) {
                 executeQuery(String.format(QueryTemplate.associateInventoryItemToMenuItem), rs -> {
                     try {
                         String inventoryItemName = rs.getString("inventoryitem_name");
                         String menuItemName = rs.getString("menuitem_name");
                         inventoryToMenu.put(inventoryItemName, menuItemName);
                         return inventoryToMenu;
                     } catch (SQLException e) {
                         return inventoryToMenu;
                     }
                 });
             }
             return inventoryToMenu;

         } catch (SQLException e) {
             e.printStackTrace();
             return null;
         }
     }
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
