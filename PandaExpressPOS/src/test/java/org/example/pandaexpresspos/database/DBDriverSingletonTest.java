package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.System.err;
import static java.lang.System.out;

// NOTE: UUIDs are hardcoded here, which is bad practice, since that means these test cases
// are coupled to a specific version of the DB; however, I'm going to leave it hardcoded
// for now since I don't anticipate us clearing the database
class DBDriverSingletonTest {
    private static DBDriverSingleton driver;

    @BeforeAll
    static void setUp() {
        driver = DBDriverSingleton.getInstance();
    }

    @Test
    void selectOrder() {
        Order order = driver.selectOrder(
                UUID.fromString("39de3ee8-d39d-4dd2-b67c-2aa0a6236166")
        );
        printOrder(order);
    }

    @Test
    void selectOrders() {
        List<Order> orders = driver.selectOrders(50);
        printItems(orders);
    }

    @Test
    void selectXReport() {
        List<Double> xReport = driver.selectXReport();
        printOrderSumsByHour(xReport);
    }

    @Test
    void selectZReport() {
        List<Double> zReport = driver.selectZReport();
        printOrderSumsByHour(zReport);
    }

    @Test
    void insertOrder() {
        InventoryItem napkin = driver.selectInventoryItem(
                UUID.fromString("20fb88db-d71d-405c-ba98-1919c1e7d74e")
        );
        MenuItem drPepper = driver.selectMenuItem(
                UUID.fromString("db104ecc-18f3-4048-9d73-13b601d424ab")
        );

        out.println("DB state before placing order:");
        out.println("Napkin qty: " + napkin.availableStock);
        out.println("Dr Pepper qty: " + drPepper.availableStock);

        Order newOrder = new Order(
                UUID.randomUUID(),
                UUID.fromString("99761186-b538-41ec-b9ba-109866af0653"),
                8,
                35,
                20,
                8,
                50.0
        );
        newOrder.addOrUpdateInventoryItem(napkin, 2);
        newOrder.addOrUpdateMenuItem(drPepper, 1);

        driver.insertOrder(newOrder);

        napkin = driver.selectInventoryItem(
                UUID.fromString("20fb88db-d71d-405c-ba98-1919c1e7d74e")
        );
        drPepper = driver.selectMenuItem(
                UUID.fromString("db104ecc-18f3-4048-9d73-13b601d424ab")
        );

        printSeparator();
        out.println("DB state after placing order:");
        out.println("Napkin qty: " + napkin.availableStock);
        out.println("Dr Pepper qty: " + drPepper.availableStock);

        printSeparator();
        newOrder = driver.selectOrder(newOrder.orderId);
        printOrder(newOrder);
    }

    @Test
    void selectEmployee() {
        Employee employee = driver.selectEmployee(
                UUID.fromString("99761186-b538-41ec-b9ba-109866af0653")
        );
        printEmployee(employee);
    }

    @Test
    void selectEmployees() {
        List<Employee> employees = driver.selectEmployees();
        printItems(employees);
    }

    @Test
    void insertEmployee() {
        Employee newEmployee = new Employee(
                false,
                "Soham Nagawanshi"
        );
        driver.insertEmployee(newEmployee);

        newEmployee = driver.selectEmployee(newEmployee.employeeId);
        printEmployee(newEmployee);
    }

    @Test
    void updateEmployee() {
        Employee employee = driver.selectEmployee(
                UUID.fromString("99761186-b538-41ec-b9ba-109866af0653")
        );
        out.println("Employee before updating:");
        printEmployee(employee);
        String originalName = employee.name;

        // Update the employee
        employee.name = "RYAN KHA";
        driver.updateEmployee(employee);
        printSeparator();
        out.println("Employee after updating:");
        printEmployee(employee);

        // Restore the employee's name so this test can be reused
        employee.name = originalName;
        driver.updateEmployee(employee);
    }

    @Test
    void selectInventoryItem() {
        InventoryItem inventoryItem = driver.selectInventoryItem(
                UUID.fromString("c4a1734e-45c2-4bb2-9d62-b9db6d6c1dc3")
        );
        printInventoryItem(inventoryItem);
    }

    @Test
    void selectInventoryItems() {
        List<InventoryItem> inventoryItems = driver.selectInventoryItems();
        printItems(inventoryItems);
    }

    @Test
    void insertInventoryItem() {
        InventoryItem newItem = new InventoryItem(
                70.00,
                210,
                "Water"
        );
        driver.insertInventoryItem(newItem);

        newItem = driver.selectInventoryItem(newItem.inventoryItemId);
        printInventoryItem(newItem);
    }

    @Test
    void updateInventoryItem() {
        InventoryItem fortuneCookie = driver.selectInventoryItem(
                UUID.fromString("3c29dea1-3d1a-4b76-96f5-5d6c5ffbbe7b")
        );
        out.println("Inventory item before updating:");
        printInventoryItem(fortuneCookie);
        Integer originalQty = fortuneCookie.availableStock;

        // Update the inventory item
        fortuneCookie.availableStock = 4200;
        driver.updateInventoryItem(fortuneCookie);
        printSeparator();
        out.println("Inventory item after updating:");
        printInventoryItem(fortuneCookie);

        // Restore the inventory item's status so this test can be reused
        fortuneCookie.availableStock = originalQty;
        driver.updateInventoryItem(fortuneCookie);
    }

    @Test
    void selectMenuItem() {
        MenuItem item = driver.selectMenuItem(
                UUID.fromString("120cbe37-d4a6-4f9e-bf51-893cd1dfd647")
        );
        printMenuItem(item);
    }

    @Test
    void selectMenuItems() {
        List<MenuItem> items = driver.selectMenuItems();
        printItems(items);
    }

    @Test
    void insertMenuItem() {
        MenuItem newItem = new MenuItem(
                500.00,
                400,
                "Fish Soup"
        );
        driver.insertMenuItem(newItem);

        newItem = driver.selectMenuItem(newItem.menuItemId);
        printMenuItem(newItem);
    }

    @Test
    void updateMenuItem() {
        MenuItem beijingBeef = driver.selectMenuItem(
                UUID.fromString("1293aa61-f866-4146-bbd7-26eac4752cfe")
        );
        out.println("Menu item before updating:");
        printMenuItem(beijingBeef);
        Integer originalQty = beijingBeef.availableStock;

        // Update the menu item
        beijingBeef.availableStock = 4200;
        driver.updateMenuItem(beijingBeef);
        printSeparator();
        out.println("Menu item after updating:");
        printMenuItem(beijingBeef);

        // Restore the menu item's status so this test can be reused
        beijingBeef.availableStock = originalQty;
        driver.updateMenuItem(beijingBeef);
    }

    @Test
    void SalesReport() {
        Map<String, Integer> salesReport = driver.selectSalesReport(
                1,
                2,
                1,
                2
        );
        printMap(salesReport);
    }
    @Test
    void selectMenuItemToInventoryItemFunctionTest(){
        List<InventoryItem> test;
                test=driver.selectMenuItemToInventoryItem("db104ecc-18f3-4048-9d73-13b601d424ab");
        printItems(test);



    }
    // Helpers
    private <T> void printItems(List<T> items) {
        for (T item : items) {
            switch (item) {
                case Order order -> printOrder(order);
                case Employee employee -> printEmployee(employee);
                case InventoryItem inventoryItem -> printInventoryItem(inventoryItem);
                case MenuItem menuItem -> printMenuItem(menuItem);
                case null, default -> err.println("ERROR: item is not of the expected type");
            }
            printSeparator();
        }
        out.println("Items selected: " + items.size());
    }

    private <K, V> void printMap(Map<K, V> map) {
        for (K key : map.keySet()) {
            out.println(key + ": " + map.get(key));
        }
    }

    private void printOrderSumsByHour(List<Double> orderSums) {
        for (int i = 0; i < orderSums.size(); i++) {
            out.println("Hour " + (i + 1) + " order total: " + orderSums.get(i));
        }
    }

    private void printOrder(Order order) {
        out.println(String.format("""
                        orderId: '%s'
                        cashierId: '%s'
                        month: %d
                        week: %d
                        day: %d
                        hour: %d
                        price: %f
                        """,
                order.orderId,
                order.cashierId,
                order.month,
                order.week,
                order.day,
                order.hour,
                order.price
        ));
    }

    private void printMenuItem(MenuItem menuItem) {
        out.printf("""
                        menuItemId: '%s'
                        price: %f
                        availableStock: %d
                        itemName: '%s'
                        """,
                menuItem.menuItemId,
                menuItem.price,
                menuItem.availableStock,
                menuItem.itemName
        );
    }

    private void printInventoryItem(InventoryItem inventoryItem) {
        out.printf("""
                        inventoryItemId: '%s'
                        cost: %f
                        availableStock: %d
                        itemName: '%s'
                        """,
                inventoryItem.inventoryItemId,
                inventoryItem.cost,
                inventoryItem.availableStock,
                inventoryItem.itemName
        );
    }

    private void printEmployee(Employee employee) {
        out.printf("""
                        employeeId: '%s'
                        isManager: %b
                        name: '%s'
                        """,
                employee.employeeId,
                employee.isManager,
                employee.name
        );
    }

    private void printSeparator() {
        out.println("-".repeat(100));
    }
}