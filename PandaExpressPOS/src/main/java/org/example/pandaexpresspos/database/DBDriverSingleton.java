package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;
import org.example.pandaexpresspos.models.wrappers.InventoryItemWithQty;
import org.example.pandaexpresspos.models.wrappers.MenuItemToInventoryItem;
import org.example.pandaexpresspos.models.wrappers.MenuItemWithQty;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

public class DBDriverSingleton {

    private static DBDriverSingleton instance;

    public final Calendar calendar = Calendar.getInstance();
    public final TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");


    // Why a private constructor?
    // This is how we implement the *singleton* design pattern
    private DBDriverSingleton() {
    }

    public static DBDriverSingleton getInstance() {
        if (instance == null) {
            instance = new DBDriverSingleton();
            instance.calendar.setTimeZone(instance.timeZone);
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
            // Add one because January = 0 in calendar
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
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
    public List<Double> selectSalesByHour() {
        List<Double> salesByHour = null;
        try {

            // Add one because January = 0 in calendar
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Get the current hour in 12h format
            int currentHour = calendar.get(Calendar.HOUR);

            // 12 PM  is represented as 0
            currentHour = currentHour == 0 ? 12 : currentHour;

            // Workday starts at 10am and ends at 10pm
            currentHour = (currentHour - 10) % 12 + 1;

            // Return the positive modulus rather than negative
            if (currentHour < 0)
                currentHour += 12;

            salesByHour = executeQuery(
                    String.format(QueryTemplate.selectOrderSumsByHour, currentMonth, currentDay, currentHour),
                    SQLToJavaMapper::orderSumMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salesByHour;
    }

    public List<Double> selectOrdersByHour() {
        List<Double> ordersByHour = null;
        try {

            // Add one because January = 0 in calendar
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            // Get the current hour in 12h format
            int currentHour = calendar.get(Calendar.HOUR);

            // 12 PM  is represented as 0
            currentHour = currentHour == 0 ? 12 : currentHour;

            // Workday starts at 10am and ends at 10pm
            currentHour = (currentHour - 10) % 12 + 1;

            // Return the positive modulus rather than negative
            if (currentHour < 0)
                currentHour += 12;

            ordersByHour = executeQuery(
                    String.format(QueryTemplate.selectOrderByHour, currentMonth, currentDay, currentHour),
                    SQLToJavaMapper::orderTotalMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordersByHour;
    }

    // The indices in the returned list correspond to hours of the day - 1
    // e.g., index 0 corresponds to hour 1
    public List<Double> selectSalesByHourForDay() {
        List<Double> selectOrderByHourForDay = null;
        try {

            // Add one because January = 0 in calendar
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            final int totalWorkingHoursPerDay = 12;

            selectOrderByHourForDay = executeQuery(
                    String.format(QueryTemplate.selectOrderSumsByHour, currentMonth, currentDay, totalWorkingHoursPerDay),
                    SQLToJavaMapper::orderSumMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectOrderByHourForDay;
    }

    public List<Double> selectOrdersByHourForDay() {
        List<Double> selectOrderByHourForDay = null;
        try {
            // Add one because January = 0 in calendar
            int currentMonth = calendar.get(Calendar.MONTH) + 1;
            int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
            final int totalWorkingHoursPerDay = 12;

            selectOrderByHourForDay = executeQuery(
                    String.format(QueryTemplate.selectOrderByHour, currentMonth, currentDay, totalWorkingHoursPerDay),
                    SQLToJavaMapper::orderTotalMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return selectOrderByHourForDay;
    }

    public Map<String, Integer> selectSalesReport(
            Integer startMonth,
            Integer endMonth,
            Integer startDay,
            Integer endDay
    ) {
        // Let's use `TreeMap` here so the items are ordered alphabetically in the UI
        Map<String, Integer> sales = new TreeMap<>();
        try {
            List<MenuItemWithQty> itemsWithQty = executeQuery(
                    String.format(QueryTemplate.selectMenuItemSalesByTimePeriod,
                            startMonth,
                            endMonth,
                            startDay,
                            endDay
                    ),
                    SQLToJavaMapper::menuItemWithQtyMapper
            );
            for (MenuItemWithQty item : itemsWithQty) {
                sales.put(item.menuItem.itemName, item.quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sales;
    }

    public void decreaseMenuItemInventoryQuantity(MenuItem menuItem, int menuItemQty) {
        for (InventoryItem item : menuItem.inventoryItems.keySet()) {
            executeUpdate(String.format(QueryTemplate.decreaseInventoryItemQty,
                    menuItemQty,
                    item.inventoryItemId
            ));
        }
    }

    public void increaseMenuItemInventoryQuantity(MenuItem menuItem, int menuItemQty) {
        for (InventoryItem item : menuItem.inventoryItems.keySet()) {
            executeUpdate(String.format(QueryTemplate.increaseInventoryItemQty,
                    menuItemQty,
                    item.inventoryItemId
            ));
        }
    }

    public Map<String, Integer> selectProductUsage(
            Integer startMonth,
            Integer endMonth,
            Integer startDay,
            Integer endDay
    ) {
        Map<String, Integer> productUsage = new TreeMap<>();
        try {
            List<InventoryItemWithQty> menuItemToInventoryItems = executeQuery(
                    String.format(QueryTemplate.selectInventoryUseByTimePeriod,
                            startMonth,
                            endMonth,
                            startDay,
                            endDay
                    ),
                    SQLToJavaMapper::inventoryItemWithQtyMapper
            );
            for (InventoryItemWithQty item : menuItemToInventoryItems) {
                productUsage.put(item.inventoryItem.itemName, item.quantity);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productUsage;
    }

    public void insertOrder(Order newOrder) {
        // Update quantities of base inventory items (napkin, utensils, fortune cookie):
        for (InventoryItem item : newOrder.inventoryItems.keySet()) {
            executeUpdate(String.format(QueryTemplate.decreaseInventoryItemQty,
                    newOrder.inventoryItems.get(item),
                    item.inventoryItemId
            ));
        }

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

        // Handle menu item connections:
        for (MenuItem item : newOrder.menuItems.keySet()) {
            Integer menuItemQty = newOrder.menuItems.get(item);

            // Insert entry into table "orderToMenuItem"
            executeUpdate(String.format(QueryTemplate.insertOrderToMenuItem,
                    newOrder.orderId,
                    item.menuItemId,
                    menuItemQty
            ));
        }
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

    public List<MenuItemToInventoryItem> selectMenuItemToInventoryItems() {
        List<MenuItemToInventoryItem> items = null;
        try {
            items = executeQuery(
                    QueryTemplate.selectAllMenuItemInventoryItem,
                    SQLToJavaMapper::menuItemToInventoryItemMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<InventoryItemWithQty> selectMenuItemInventoryItems(MenuItem menuItem) {
        List<InventoryItemWithQty> items = null;
        try {
            items = executeQuery(
                    String.format(QueryTemplate.selectMenuItemInventoryItem, menuItem.menuItemId),
                    SQLToJavaMapper::inventoryItemWithQtyMapper
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
                newMenuItem.itemName
        ));
    }

    public void updateMenuItem(MenuItem updatedMenuItem) {
        executeUpdate(String.format(QueryTemplate.updateMenuItem,
                updatedMenuItem.price,
                updatedMenuItem.itemName,
                updatedMenuItem.menuItemId
        ));
    }

    public void insertMenuItemToInventoryItem(UUID menuItemId, UUID inventoryItemId) {
        executeUpdate(String.format(QueryTemplate.insertMenuItemToInventoryItem,
                menuItemId,
                inventoryItemId,
                1
        ));
    }

    public List<InventoryItem> selectMenuItemInventoryItems(UUID menuItemId) {
        List<InventoryItem> items = null;
        try {
            items = executeQuery(
                    String.format(QueryTemplate.selectMenuItemInventoryItem, menuItemId),
                    SQLToJavaMapper::inventoryItemMapper
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    public void deleteMenuItemToInventoryItem(UUID menuItemId) {
        executeUpdate(String.format(QueryTemplate.deleteMenuItemToInventoryItem,
                menuItemId
        ));
    }

    // Test helpers (package-private):
    Order selectRandomOrder() {
        Order order = null;
        try {
            List<Order> items = executeQuery(
                    QueryTemplate.selectRandomOrder,
                    SQLToJavaMapper::orderMapper
            );
            if (items.isEmpty()) {
                throw new SQLException("Order not found");
            }
            order = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return order;
    }

    Employee selectRandomEmployee() {
        Employee employee = null;
        try {
            List<Employee> items = executeQuery(
                    QueryTemplate.selectRandomEmployee,
                    SQLToJavaMapper::employeeMapper
            );
            if (items.isEmpty()) {
                throw new SQLException("Employee not found");
            }
            employee = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return employee;
    }

    MenuItem selectRandomMenuItem() {
        MenuItem menuItem = null;
        try {
            List<MenuItem> items = executeQuery(
                    QueryTemplate.selectRandomMenuItem,
                    SQLToJavaMapper::menuItemMapper
            );
            if (items.isEmpty()) {
                throw new SQLException("MenuItem not found");
            }
            menuItem = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuItem;
    }

    InventoryItem selectRandomInventoryItem() {
        InventoryItem inventoryItem = null;
        try {
            List<InventoryItem> items = executeQuery(
                    QueryTemplate.selectRandomInventoryItem,
                    SQLToJavaMapper::inventoryItemMapper
            );
            if (items.isEmpty()) {
                throw new SQLException("InventoryItem not found");
            }
            inventoryItem = items.getFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return inventoryItem;
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
}
