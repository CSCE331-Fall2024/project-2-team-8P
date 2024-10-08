package org.example.pandaexpresspos.database;

class QueryTemplate {
    // Orders
    public static final String selectOrder = """
            SELECT * FROM "order"
            WHERE orderId = '%s';
            """;
    public static final String selectRecentOrders = """
            SELECT * FROM "order"
            ORDER BY month DESC, day DESC, hour DESC
            LIMIT %d;
            """;
    public static final String selectOrderSumsByWeekRange = """
            SELECT week, SUM(price) FROM "order"
            WHERE week between %d and %d
            GROUP BY week
            ORDER BY week DESC;
            """;
    public static final String insertOrder = """
            INSERT INTO "order" (orderId, cashierId, month, week, day, hour, price)
            VALUES ('%s', '%s', %d, %d, %d, %d, %f);
            """;
    public static final String insertOrderToMenuItem = """
            INSERT INTO orderToMenuItem (orderId, menuItemId, quantity)
            VALUES ('%s', '%s', %d);
            """;
    public static final String insertOrderToInventoryItem = """
            INSERT INTO orderToInventoryItem (orderId, inventoryItemId, quantity)
            VALUES ('%s', '%s', %d);
            """;

    // Employees
    public static final String selectEmployee = """
            SELECT * FROM employee
            WHERE employeeId = '%s';
            """;
    public static final String selectAllEmployees = """
            SELECT * FROM employee;
            """;
    public static final String insertEmployee = """
            INSERT INTO employee (employeeId, isManager, name)
            VALUES ('%s', %b, '%s');
            """;
    public static final String updateEmployee = """
            UPDATE employee
            SET isManager = %b, name='%s'
            WHERE employeeId = '%s';
            """;

    // Inventory items
    public static final String selectInventoryItem = """
            SELECT * FROM inventoryItem
            WHERE inventoryId = '%s';
            """;
    public static final String selectAllInventoryItems = """
            SELECT * FROM inventoryItem;
            """;
    public static final String insertInventoryItem = """
            INSERT INTO inventoryItem (inventoryItemId, cost, availableStock, itemName)
            VALUES ('%s', %f, %d, '%s');
            """;
    public static final String updateInventoryItemQty = """
            UPDATE inventoryItem
            SET availableStock = availableStock - %d
            WHERE inventoryItemId = '%s';
            """;
    public static final String updateInventoryItem = """
            UPDATE inventoryItem
            SET price = %f, availableStock = %d, itemName = '%s'
            WHERE inventoryItemId = '%s';
            """;

    // Menu items
    public static final String selectMenuItem = """
            SELECT * FROM menuItem
            WHERE menuItemId = '%s';
            """;
    public static final String selectAllMenuItems = """
            SELECT * FROM menuItem;
            """;
    public static final String insertMenuItem = """
            INSERT INTO menuItem (menuItemId, price, availableStock, itemName)
            VALUES ('%s', %f, %d, '%s');
            """;
    public static final String updateMenuItemQty = """
            UPDATE menuItem
            SET availableStock = availableStock - %d
            WHERE menuItemId = '%s';
            """;
    public static final String updateMenuItem = """
            UPDATE menuItem
            SET price = %f, availableStock = %d, itemName = '%s'
            WHERE menuItemId = '%s';
            """;
}