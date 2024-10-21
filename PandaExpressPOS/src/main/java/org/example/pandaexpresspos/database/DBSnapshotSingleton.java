package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.*;
import org.example.pandaexpresspos.models.wrappers.MenuItemToInventoryItem;

import java.util.*;

/**
 * The DBSnapshotSingleton class is a singleton that maintains snapshots
 * of various database entities such as MenuItems, InventoryItems,
 * Employees, and Orders. This class provides an efficient way to manage
 * and access these entities without constantly querying the database.
 *
 * <p>The snapshots are stored in Maps for quick access, using human-readable
 * keys for easier understanding. The snapshots can be refreshed
 * lazily to minimize latency.</p>
 *
 * @author Kevin Zhang
 */
public class DBSnapshotSingleton {
    private static DBSnapshotSingleton instance;

    // Key: name of menu item
    private final Map<String, MenuItem> menuSnapshot = new TreeMap<>();
    // Key: name of inventory item
    private final Map<String, InventoryItem> inventorySnapshot = new TreeMap<>();
    // Key: name of employee
    private final Map<String, Employee> employeeSnapshot = new TreeMap<>();
    // Key: orderId (there's no other way to uniquely ID an order)
    private final Map<UUID, Order> orderSnapshot = new TreeMap<>();

    /**
     * Private constructor to prevent instantiation.
     */
    private DBSnapshotSingleton() {
    }

    /**
     * Gets the single instance of DBSnapshotSingleton.
     *
     * @return the singleton instance of DBSnapshotSingleton
     */
    public static DBSnapshotSingleton getInstance() {
        if (instance == null) {
            instance = new DBSnapshotSingleton();
        }
        return instance;
    }

    /**
     * Refreshes values stored in all snapshot variables.
     *
     * <p>This method should only be called when necessary (lazily)
     * to reduce latency.</p>
     */
    public void refreshAllSnapshots() {
        refreshOrderSnapshot();
        refreshEmployeeSnapshot();
        refreshMenuSnapshot();
        refreshInventorySnapshot();
    }

    /**
     * Refreshes the order snapshot with the latest orders from the database.
     */
    public void refreshOrderSnapshot() {
        int numOrdersToRetrieve = 50;
        List<Order> orders = DBDriverSingleton.getInstance()
                .selectOrders(numOrdersToRetrieve);
        orderSnapshot.clear();
        for (Order order : orders) {
            orderSnapshot.put(order.orderId, order);
        }
    }

    /**
     * Refreshes the employee snapshot with the latest employees from the database.
     */
    public void refreshEmployeeSnapshot() {
        List<Employee> employees = DBDriverSingleton.getInstance()
                .selectEmployees();
        employeeSnapshot.clear();
        for (Employee employee : employees) {
            employeeSnapshot.put(employee.name, employee);
        }
    }

    /**
     * Refreshes the inventory snapshot with the latest inventory items
     * from the database.
     */
    public void refreshInventorySnapshot() {
        List<InventoryItem> inventoryItems = DBDriverSingleton.getInstance()
                .selectInventoryItems();
        inventorySnapshot.clear();
        for (InventoryItem item : inventoryItems) {
            inventorySnapshot.put(item.itemName, item);
        }
    }

    /**
     * Refreshes the menu snapshot with the latest menu items and their
     * associated inventory items from the database.
     *
     * <p>This function populates the 'inventoryItems' map within each
     * MenuItem.</p>
     */
    public void refreshMenuSnapshot() {
        List<MenuItemToInventoryItem> menuItemToInventoryItems = DBDriverSingleton.getInstance()
                .selectMenuItemToInventoryItems();

        // Refresh the inventory snapshot to ensure up-to-date data
        refreshInventorySnapshot();

        menuSnapshot.clear();
        for (MenuItemToInventoryItem item : menuItemToInventoryItems) {
            // Get the associated inventory item from the inventory snapshot
            InventoryItem associatedInventoryItem = inventorySnapshot.get(item.inventoryItem.itemName);

            if (menuSnapshot.containsKey(item.menuItem.itemName)) {
                MenuItem existingMenuItem = menuSnapshot.get(item.menuItem.itemName);
                existingMenuItem.addOrUpdateInventoryItem(associatedInventoryItem, 1);
            } else {
                MenuItem newMenuItem = item.menuItem;
                newMenuItem.addOrUpdateInventoryItem(associatedInventoryItem, 1);
                menuSnapshot.put(item.menuItem.itemName, newMenuItem);
            }
        }
    }

    /**
     * Gets the current snapshot of menu items.
     *
     * @return a Map of menu items indexed by their names
     */
    public Map<String, MenuItem> getMenuSnapshot() {
        return menuSnapshot;
    }

    /**
     * Gets the current snapshot of inventory items.
     *
     * @return a Map of inventory items indexed by their names
     */
    public Map<String, InventoryItem> getInventorySnapshot() {
        return inventorySnapshot;
    }

    /**
     * Gets the current snapshot of employees.
     *
     * @return a Map of employees indexed by their names
     */
    public Map<String, Employee> getEmployeeSnapshot() {
        return employeeSnapshot;
    }

    /**
     * Gets the current snapshot of orders.
     *
     * @return a Map of orders indexed by their unique order IDs
     */
    public Map<UUID, Order> getOrderSnapshot() {
        return orderSnapshot;
    }
}
