package org.example.pandaexpresspos.database;

class QueryTemplate {
    // Orders
    public static final String selectOrder = """
            SELECT * FROM "order"
            WHERE orderId = '%s';
            """;
    public static final String selectRecentOrders = """
            SELECT * FROM "order"
            WHERE month = %d
            ORDER BY month DESC, day DESC, hour DESC
            LIMIT %d;
            """;
    public static final String selectOrderSumsByHour = """
            SELECT hour, SUM(price) FROM "order"
            WHERE month = %d AND day = %d AND hour <= %d
            GROUP BY hour
            ORDER BY hour ASC;
            """;
    public static final String insertOrder = """
            INSERT INTO "order" (orderId, cashierId, month, week, day, hour, price)
            VALUES ('%s', '%s', %d, %d, %d, %d, %f);
            """;
    public static final String updateOrder = """
            UPDATE "order"
            SET month = %d, week = '%s', day = %d, hour = %d, price = %f
            WHERE orderId = '%s';
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
    public static final String selectEmployeeByName = """
            SELECT * FROM employee
            WHERE name = '%s';
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
            SET isManager = %b, name = '%s'
            WHERE employeeId = '%s';
            """;

    // Inventory items
    public static final String selectInventoryItem = """
            SELECT * FROM inventoryItem
            WHERE inventoryItemId = '%s';
            """;
    public static final String selectAllInventoryItems = """
            SELECT * FROM inventoryItem;
            """;
    public static final String insertInventoryItem = """
            INSERT INTO inventoryItem (inventoryItemId, cost, availableStock, itemName)
            VALUES ('%s', %f, %d, '%s');
            """;
    public static final String decreaseInventoryItemQty = """
            UPDATE inventoryItem
            SET availableStock = availableStock - %d
            WHERE inventoryItemId = '%s';
            """;
    public static final String increaseInventoryItemQty = """
            UPDATE inventoryItem
            SET availableStock = availableStock + %d
            WHERE inventoryItemId = '%s';
            """;
    public static final String updateInventoryItem = """
            UPDATE inventoryItem
            SET cost = %f, availableStock = %d, itemName = '%s'
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
    public static final String selectMenuItemInventoryItems = """
            SELECT i.inventoryItemId, i.cost, i.availableStock, i.itemName, mi.quantity
            FROM menuItem m
            JOIN menuItemToInventoryItem mi
            ON m.menuItemId = mi.menuItemId
            JOIN inventoryItem i
            ON i.inventoryItemId = mi.inventoryItemId
            WHERE m.itemName = '%s';
            """;
    public static final String insertMenuItem = """
            INSERT INTO menuItem (menuItemId, price, availableStock, itemName)
            VALUES ('%s', %f, %d, '%s');
            """;
    public static final String decreaseMenuItemQty = """
            UPDATE menuItem
            SET availableStock = availableStock - %d
            WHERE menuItemId = '%s';
            """;
    public static final String updateMenuItem = """
            UPDATE menuItem
            SET price = %f, availableStock = %d, itemName = '%s'
            WHERE menuItemId = '%s';
            """;
    public static final String selectMenuItemSalesByTimePeriod = """
            SELECT m.menuItemId, m.price, m.availableStock, m.itemName, count(*)
            FROM "order" o
            JOIN orderToMenuItem otm ON o.orderId = otm.orderId
            JOIN menuItem m ON otm.menuItemId = m.menuItemId
            WHERE o.month BETWEEN %d AND %d
            AND o.day BETWEEN %d AND %d
            GROUP BY m.menuItemId, m.price, m.availableStock, m.itemName;
            """;
    public static final String insertMenuItemToInventoryItem = """
            INSERT INTO menuItemToInventoryItem (menuItemId, inventoryItemId, quantity)
            VALUES ('%s', '%s' , %d);
            """;
    public static final String inventoryItemAssociatedWithMenuItem = """
            SELECT
            i.inventoryItemId,
            i.cost,
            i.availableStock,
            i.itemName
            FROM
            menuitem m
            JOIN menuItemToInventoryItem mti ON m.menuItemId = mti.menuItemId
            JOIN inventoryItem i ON i.inventoryItemId = mti.inventoryItemId
            WHERE m.menuItemId = '%s';
            """;
    public static final String deleteMenuItemToInventoryItem = """
            DELETE FROM menuItemToInventoryItem
            WHERE menuItemId = '%s';
            """;
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
    public static final String selectInventoryItemandStock = """
                         SELECT 
                ii.inventoryitemid,
                ii.cost,
                ii.availablestock,
                ii.itemname
            FROM 
                inventoryitem ii
            JOIN 
                menuitemtoinventoryitem itm ON ii.inventoryitemid = itm.inventoryitemid
            JOIN 
                menuitem mi ON mi.menuitemid = itm.menuitemid
            WHERE 
                mi.itemname = '%s';
            """;
}