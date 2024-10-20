package org.example.pandaexpresspos.database;

/**
 * The QueryTemplate class contains SQL query templates used for interacting
 * with the database in the Panda Express POS system.
 *
 * <p>This class defines various SQL statements for operations related to
 * orders, employees, inventory items, and menu items. The queries include
 * selections, insertions, updates, and deletions. Each query is defined
 * as a constant string, allowing for easy modification and reuse throughout
 * the application.</p>
 */
class QueryTemplate {
    // Orders
    /** SQL query to select an order by orderId */
    public static final String selectOrder = """
            SELECT * FROM "order"
            WHERE orderId = '%s';
            """;

    /** SQL query to select a random order */
    public static final String selectRandomOrder = """
            SELECT * FROM "order"
            ORDER BY RANDOM()
            LIMIT 1;
            """;

    /** SQL query to select recent orders by month and limit */
    public static final String selectRecentOrders = """
            SELECT * FROM "order"
            WHERE month = %d
            ORDER BY month DESC, day DESC, hour DESC
            LIMIT %d;
            """;

    /** SQL query to select order sums by hour */
    public static final String selectOrderSumsByHour = """
            SELECT hour, SUM(price) FROM "order"
            WHERE month = %d AND day = %d AND hour <= %d
            GROUP BY hour
            ORDER BY hour ASC;
            """;

    /** SQL query to select order count by hour */
    public static final String selectOrderByHour = """
            SELECT hour, COUNT(*) FROM "order"
            WHERE month = %d AND day = %d AND hour <= %d
            GROUP BY hour
            ORDER BY hour ASC;
            """;

    /** SQL query to insert a new order */
    public static final String insertOrder = """
            INSERT INTO "order" (orderId, cashierId, month, week, day, hour, price)
            VALUES ('%s', '%s', %d, %d, %d, %d, %f);
            """;

    /** SQL query to update an existing order */
    public static final String updateOrder = """
            UPDATE "order"
            SET month = %d, week = '%s', day = %d, hour = %d, price = %f
            WHERE orderId = '%s';
            """;

    /** SQL query to insert an order to a menu item */
    public static final String insertOrderToMenuItem = """
            INSERT INTO orderToMenuItem (orderId, menuItemId, quantity)
            VALUES ('%s', '%s', %d);
            """;

    /** SQL query to insert an order to an inventory item */
    public static final String insertOrderToInventoryItem = """
            INSERT INTO orderToInventoryItem (orderId, inventoryItemId, quantity)
            VALUES ('%s', '%s', %d);
            """;

    // Employees
    /** SQL query to select an employee by employeeId */
    public static final String selectEmployee = """
            SELECT * FROM employee
            WHERE employeeId = '%s';
            """;

    /** SQL query to select an employee by name */
    public static final String selectEmployeeByName = """
            SELECT * FROM employee
            WHERE name = '%s';
            """;

    /** SQL query to select a random employee */
    public static final String selectRandomEmployee = """
            SELECT * FROM employee
            ORDER BY RANDOM()
            LIMIT 1;
            """;

    /** SQL query to select all employees */
    public static final String selectAllEmployees = """
            SELECT * FROM employee;
            """;

    /** SQL query to insert a new employee */
    public static final String insertEmployee = """
            INSERT INTO employee (employeeId, isManager, name)
            VALUES ('%s', %b, '%s');
            """;

    /** SQL query to update an existing employee */
    public static final String updateEmployee = """
            UPDATE employee
            SET isManager = %b, name = '%s'
            WHERE employeeId = '%s';
            """;

    // Inventory items
    /** SQL query to select an inventory item by inventoryItemId */
    public static final String selectInventoryItem = """
            SELECT * FROM inventoryItem
            WHERE inventoryItemId = '%s';
            """;

    /** SQL query to select all inventory items */
    public static final String selectAllInventoryItems = """
            SELECT * FROM inventoryItem;
            """;

    /** SQL query to select a random inventory item */
    public static final String selectRandomInventoryItem = """
            SELECT * FROM inventoryItem
            ORDER BY RANDOM()
            LIMIT 1;
            """;

    /** SQL query to insert a new inventory item */
    public static final String insertInventoryItem = """
            INSERT INTO inventoryItem (inventoryItemId, cost, availableStock, itemName)
            VALUES ('%s', %f, %d, '%s');
            """;

    /** SQL query to decrease the quantity of an inventory item */
    public static final String decreaseInventoryItemQty = """
            UPDATE inventoryItem
            SET availableStock = availableStock - %d
            WHERE inventoryItemId = '%s';
            """;

    /** SQL query to increase the quantity of an inventory item */
    public static final String increaseInventoryItemQty = """
            UPDATE inventoryItem
            SET availableStock = availableStock + %d
            WHERE inventoryItemId = '%s';
            """;

    /** SQL query to update an existing inventory item */
    public static final String updateInventoryItem = """
            UPDATE inventoryItem
            SET cost = %f, availableStock = %d, itemName = '%s'
            WHERE inventoryItemId = '%s';
            """;

    // Menu items
    /** SQL query to select a menu item by menuItemId */
    public static final String selectMenuItem = """
            SELECT * FROM menuItem
            WHERE menuItemId = '%s';
            """;

    /** SQL query to select all menu items */
    public static final String selectAllMenuItems = """
            SELECT * FROM menuItem;
            """;

    /** SQL query to select a random menu item */
    public static final String selectRandomMenuItem = """
            SELECT * FROM menuItem
            ORDER BY RANDOM()
            LIMIT 1;
            """;

    /** SQL query to select inventory items used by a menu item */
    public static final String selectMenuItemInventoryItem = """
            SELECT
                i.inventoryItemId,
                i.cost,
                i.availableStock,
                i.itemName,
                mi.quantity AS itemsUsed
            FROM menuItem m
            JOIN menuItemToInventoryItem mi ON m.menuItemId = mi.menuItemId
            JOIN inventoryItem i ON i.inventoryItemId = mi.inventoryItemId
            WHERE m.menuItemId = '%s';
            """;

    /** SQL query to select all menu items and their corresponding inventory items */
    public static final String selectAllMenuItemInventoryItem = """
            SELECT
                i.inventoryItemId,
                i.cost AS inventoryItemCost,
                i.availableStock AS inventoryItemStock,
                i.itemName AS inventoryItemName,
                m.menuItemId,
                m.price as menuItemPrice,
                m.itemName AS menuItemName
            FROM menuItem m
            JOIN menuItemToInventoryItem mi ON m.menuItemId = mi.menuItemId
            JOIN inventoryItem i ON i.inventoryItemId = mi.inventoryItemId;
            """;

    /** SQL query to insert a new menu item */
    public static final String insertMenuItem = """
            INSERT INTO menuItem (menuItemId, price, itemName)
            VALUES ('%s', %f, '%s');
            """;

    /** SQL query to update an existing menu item */
    public static final String updateMenuItem = """
            UPDATE menuItem
            SET price = %f, itemName = '%s'
            WHERE menuItemId = '%s';
            """;

    /** SQL query to select menu item sales by time period */
    public static final String selectMenuItemSalesByTimePeriod = """
            SELECT m.menuItemId, m.price, m.itemName, count(*)
            FROM "order" o
            JOIN orderToMenuItem otm ON o.orderId = otm.orderId
            JOIN menuItem m ON otm.menuItemId = m.menuItemId
            WHERE o.month BETWEEN %d AND %d
            AND o.day BETWEEN %d AND %d
            GROUP BY m.menuItemId, m.price, m.itemName;
            """;

    /** SQL query to insert a relationship between a menu item and inventory item */
    public static final String insertMenuItemToInventoryItem = """
            INSERT INTO menuItemToInventoryItem (menuItemId, inventoryItemId, quantity)
            VALUES ('%s', '%s' , %d);
            """;

    /** SQL query to delete a relationship between a menu item and inventory item */
    public static final String deleteMenuItemToInventoryItem = """
            DELETE FROM menuItemToInventoryItem
            WHERE menuItemId = '%s';
            """;

    /** SQL query to select inventory usage by time period */
    public static final String selectInventoryUseByTimePeriod = """
            SELECT
                i.inventoryItemId,
                i.cost,
                i.availableStock,
                i.itemName,
                count(*) AS itemsUsed
            FROM "order" o
            JOIN orderToMenuItem otm ON o.orderId = otm.orderId
            JOIN menuItem m ON otm.menuItemId = m.menuItemId
            JOIN menuItemToInventoryItem mti ON m.menuItemId = mti.menuItemId
            JOIN inventoryItem i ON mti.inventoryItemId = i.inventoryItemId
            WHERE o.month BETWEEN %d AND %d
            AND o.day BETWEEN %d AND %d
            GROUP BY
                i.inventoryItemId,
                i.cost,
                i.availableStock,
                i.itemName;
            """;
}
