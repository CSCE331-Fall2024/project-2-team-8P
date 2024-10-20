package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;
import org.example.pandaexpresspos.models.wrappers.InventoryItemWithQty;
import org.example.pandaexpresspos.models.wrappers.MenuItemToInventoryItem;
import org.example.pandaexpresspos.models.wrappers.MenuItemWithQty;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * Singleton class that directly interacts with the PostgreSQL database via JDBC calls and converts results into data models.
 * Because this class is a singleton, only one instance may exist throughout the entire application at any point in time.
 */
public class DBDriverSingleton {

    /**
     * Object that gives us current time information
     */
    public final Calendar calendar = Calendar.getInstance();

    private static DBDriverSingleton instance;

    private final TimeZone timeZone = TimeZone.getTimeZone("America/Chicago");

    // Why a private constructor?
    // This is how we implement the *singleton* design pattern
    private DBDriverSingleton() {
    }

    /**
     * Gets the current singleton instance of {@code DBDriverSingleton}. Creates a new instance if one doesn't exist.
     *
     * @return The singleton instance of {@code DBDriverSingleton}
     */
    public static DBDriverSingleton getInstance() {
        if (instance == null) {
            instance = new DBDriverSingleton();
            instance.calendar.setTimeZone(instance.timeZone);
        }
        return instance;
    }

    // Public methods:

    // Order

    /**
     * Selects an order from the database with the given orderId
     *
     * @param orderId the UUID of the order to select
     * @return the {@code Order} object representing the selected order
     */
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

    /**
     * Selects the {@code mostRecent} most recent orders from the database
     *
     * @param mostRecent the number of most recent orders to select
     * @return a {@code List<Order>} of most recent orders
     */
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

    /**
     * Selects the sum of order prices grouped by hour. This only returns hours of the day up until the current hour,
     * determined by Java's internal DateTime methods.
     *
     * @return a {@code List<Double>} of order price totals, grouped by hour. Index 0 corresponds to the 1st hour, etc.
     */
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

    /**
     * Selects the total number of orders grouped by hour. This only returns hours of the day up until the current hour,
     * determined by Java's internal DateTime methods.
     *
     * @return a {@code List<Double>} of orders placed, grouped by hour. Index 0 corresponds to the 1st hour, etc.
     */
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

    /**
     * Selects the sum of order prices grouped by hour. This returns all hours of the day (1-12).
     *
     * @return a {@code List<Double>} of order price totals, grouped by hour. Index 0 corresponds to the 1st hour, etc.
     */
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

    /**
     * Selects the total number of orders placed grouped by hour. This returns all hours of the day (1-12).
     *
     * @return a {@code List<Double>} of order placed, grouped by hour. Index 0 corresponds to the 1st hour, etc.
     */
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

    /**
     * Totals up the number of times each menu item was included in an order in a specified time period
     *
     * @param startMonth the start month
     * @param endMonth   the end month
     * @param startDay   the start day
     * @param endDay     the end day
     * @return a {@code Map<String, Integer>}, where the keys are menu item names, and the values are the number of times
     * the respective menu item was contained in an order
     */
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
                            endDay,
                            startMonth,
                            endMonth
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

    /**
     * Decreases the stock of inventory items associated with a given menu item
     *
     * @param menuItem    the menu item to decrease the inventory stock for
     * @param menuItemQty the number of times the menu item was included in an order. For example, if we have 5
     *                    "Orange Chicken" in an order, we need to decrease its associated inventory stock 5 times
     */
    public void decreaseMenuItemInventoryQuantity(MenuItem menuItem, int menuItemQty) {
        for (InventoryItem item : menuItem.inventoryItems.keySet()) {
            executeUpdate(String.format(QueryTemplate.decreaseInventoryItemQty,
                    menuItemQty,
                    item.inventoryItemId
            ));
        }
    }

    /**
     * Increases the stock of inventory items associated with a given menu item. This is primarily used to replenish
     * stock after canceling an order.
     *
     * @param menuItem    the menu item to increase the inventory stock for
     * @param menuItemQty the number of times the menu item was included in an order. For example, if we have 5
     *                    "Orange Chicken" in an order, we need to increase its associated inventory stock 5 times
     */
    public void increaseMenuItemInventoryQuantity(MenuItem menuItem, int menuItemQty) {
        for (InventoryItem item : menuItem.inventoryItems.keySet()) {
            executeUpdate(String.format(QueryTemplate.increaseInventoryItemQty,
                    menuItemQty,
                    item.inventoryItemId
            ));
        }
    }

    /**
     * Totals up the number of times each inventory item was used over the specified time period
     *
     * @param startMonth the start month
     * @param endMonth   the end month
     * @param startDay   the start day
     * @param endDay     the end day
     * @return a {@code Map<String, Integer>}, where the keys are inventory item names, and the values are the number of
     * times the respective inventory item was used
     */
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
                            endDay,
                            startMonth,
                            endMonth
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

    /**
     * Places an order
     *
     * @param newOrder the order to place
     */
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

    /**
     * Selects an employee
     *
     * @param employeeId the UUID of the employee to select
     * @return an {@code Employee} object representing the selected employee
     */
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

    /**
     * Selects an employee
     *
     * @param name the name of the employee to select
     * @return an {@code Employee} object representing the selected employee
     */
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

    /**
     * Selects all the employees
     *
     * @return a {@code List<Employee>} of all employees
     */
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

    /**
     * Adds a new employee
     *
     * @param newEmployee the employee to add
     */
    public void insertEmployee(Employee newEmployee) {
        executeUpdate(String.format(QueryTemplate.insertEmployee,
                newEmployee.employeeId,
                newEmployee.isManager,
                newEmployee.name
        ));
    }

    /**
     * Updates an employee's information
     *
     * @param updatedEmployee an {@code Employee} object containing an employee's updated information
     */
    public void updateEmployee(Employee updatedEmployee) {
        executeUpdate(String.format(QueryTemplate.updateEmployee,
                updatedEmployee.isManager,
                updatedEmployee.name,
                updatedEmployee.employeeId
        ));
    }

    // Inventory

    /**
     * Selects an inventory item
     *
     * @param inventoryItemId the UUID of the inventory item to select
     * @return an {@code InventoryItem} object representing the selected inventory item
     */
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

    /**
     * Selects all inventory items
     *
     * @return a {@code List<InventoryItem>} of all inventory items
     */
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

    /**
     * Adds a new inventory item
     *
     * @param newInventoryItem the inventory item to add
     */
    public void insertInventoryItem(InventoryItem newInventoryItem) {
        executeUpdate(String.format(QueryTemplate.insertInventoryItem,
                newInventoryItem.inventoryItemId,
                newInventoryItem.cost,
                newInventoryItem.availableStock,
                newInventoryItem.itemName
        ));
    }

    /**
     * Updates an existing inventory item
     *
     * @param updatedInventoryItem an {@code InventoryItem} object containing an inventory item's updated information
     */
    public void updateInventoryItem(InventoryItem updatedInventoryItem) {
        executeUpdate(String.format(QueryTemplate.updateInventoryItem,
                updatedInventoryItem.cost,
                updatedInventoryItem.availableStock,
                updatedInventoryItem.itemName,
                updatedInventoryItem.inventoryItemId
        ));
    }

    // Menu items

    /**
     * Selects a menu item
     *
     * @param menuItemId the UUID of the menu item to select
     * @return a {@code MenuItem} object represented the selected menu item
     */
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

    /**
     * Selects all menu items
     *
     * @return a {@code List<MenuItem>} of all menu items
     */
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

    /**
     * Adds a new menu item
     *
     * @param newMenuItem the menu item to add
     */
    public void insertMenuItem(MenuItem newMenuItem) {
        executeUpdate(String.format(QueryTemplate.insertMenuItem,
                newMenuItem.menuItemId,
                newMenuItem.price,
                newMenuItem.itemName
        ));
    }

    /**
     * Updates an existing menu item
     *
     * @param updatedMenuItem a {@code MenuItem} object reprsenting the menu item to update
     */
    public void updateMenuItem(MenuItem updatedMenuItem) {
        executeUpdate(String.format(QueryTemplate.updateMenuItem,
                updatedMenuItem.price,
                updatedMenuItem.itemName,
                updatedMenuItem.menuItemId
        ));
    }

    /**
     * Associates an inventory item with a menu item
     *
     * @param menuItemId the menu item to associate
     * @param inventoryItemId the inventory item to associate
     */
    public void insertMenuItemToInventoryItem(UUID menuItemId, UUID inventoryItemId) {
        executeUpdate(String.format(QueryTemplate.insertMenuItemToInventoryItem,
                menuItemId,
                inventoryItemId,
                1
        ));
    }

    /**
     * Selects all inventory items associated with a given menu item
     *
     * @param menuItemId the UUID of the menu item to select inventory items for
     * @return a {@code List<InventoryItem>} of inventory items associated with the given menu item
     */
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

    /**
     * Removes all inventory items associated with a given menu item
     *
     * @param menuItemId the UUID of the menu item to remove connections for
     */
    public void deleteMenuItemToInventoryItem(UUID menuItemId) {
        executeUpdate(String.format(QueryTemplate.deleteMenuItemToInventoryItem,
                menuItemId
        ));
    }

    // Package private methods:

    /**
     * Selects all associations between menu items and inventory items. This corresponds to entries from the
     * table obtained by joining the <span style="font-weight: bold">MenuItem</span>,
     * <span style="font-weight: bold">MenuItemToInventoryItem</span>, and
     * <span style="font-weight: bold">InventoryItem</span> tables.
     *
     * @return a {@code List<MenuItemToInventoryItem>} of such connections
     */
    List<MenuItemToInventoryItem> selectMenuItemToInventoryItems() {
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

    // Test helpers
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
