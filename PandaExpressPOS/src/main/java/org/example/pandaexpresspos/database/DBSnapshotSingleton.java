package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;

import java.util.*;

public class DBSnapshotSingleton {
    private static DBSnapshotSingleton instance;

    /*
    Why these keys? Rather than indexing on an ID, which is not human-readable,
    I want to index on something that's intuitive
     */
    // Key: name of menu item
    private final Map<String, MenuItem> menuSnapshot = new TreeMap<>();
    // Key: name of inventory item
    private final Map<String, InventoryItem> inventorySnapshot = new TreeMap<>();
    // Key: name of employee
    private final Map<String, Employee> employeeSnapshot = new TreeMap<>();
    // Key: orderId (there's no other way to uniquely ID an order)
    private final Map<UUID, Order> orderSnapshot = new TreeMap<>();


    private DBSnapshotSingleton() {
    }

    // Public methods:
    public static DBSnapshotSingleton getInstance() {
        if (instance == null) {
            instance = new DBSnapshotSingleton();
        }
        return instance;
    }

    // Refreshes values stored in all snapshot variables
    // Note: should only be called when necessary (lazily) to reduce latency
    public void refreshAllSnapshots() {
        refreshOrderSnapshot();
        refreshEmployeeSnapshot();
        refreshMenuSnapshot();
        refreshInventorySnapshot();
    }

    public void refreshOrderSnapshot() {
        int numOrdersToRetrieve = 50;
        List<Order> orders = DBDriverSingleton.getInstance()
                .selectOrders(numOrdersToRetrieve);
        orderSnapshot.clear();
        for (Order order : orders) {
            orderSnapshot.put(order.orderId, order);
        }
    }a

    public void refreshEmployeeSnapshot() {
        List<Employee> employees = DBDriverSingleton.getInstance()
                .selectEmployees();
        employeeSnapshot.clear();
        for (Employee employee : employees) {
            employeeSnapshot.put(employee.name, employee);
        }
    }

    public void refreshInventorySnapshot() {
        List<InventoryItem> inventoryItems = DBDriverSingleton.getInstance()
                .selectInventoryItems();
        inventorySnapshot.clear();
        for (InventoryItem item : inventoryItems) {
            inventorySnapshot.put(item.itemName, item);
        }
    }

    public void refreshMenuSnapshot() {
        List<MenuItem> menuItems = DBDriverSingleton.getInstance()
                .selectMenuItems();
        menuSnapshot.clear();
        for (MenuItem menuItem : menuItems) {
            menuSnapshot.put(menuItem.itemName, menuItem);
        }
    }

    public Map<String, MenuItem> getMenuSnapshot() {
        return menuSnapshot;
    }

    public Map<String, InventoryItem> getInventorySnapshot() {
        return inventorySnapshot;
    }

    public Map<String, Employee> getEmployeeSnapshot() {
        return employeeSnapshot;
    }

    public Map<UUID, Order> getOrderSnapshot() {
        return orderSnapshot;
    }
}
