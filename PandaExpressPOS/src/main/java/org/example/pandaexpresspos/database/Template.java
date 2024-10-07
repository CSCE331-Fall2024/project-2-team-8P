package org.example.pandaexpresspos.database;

public class Template {
    public static String AddOrder ="""
        INSERT INTO "order" (orderId, cashierId, month, week, day, hour, price)
        VALUES ('%s', '%s', %d, %d, %d, %d, %d);
    """;
    public static String AddOrderToMenuItem ="""
        INSERT INTO ordertoinventoryitem (orderId, menuItemId, quantity)
        VALUES ('%s', '%s', %d);
    """;
    public static String AddOrderToInventoryItem ="""
        INSERT INTO orders (orderId, inventoryItemId, quantity)
        VALUES ('%s', '%s', %d);
        """;
    public static String UpdateMenuAndInventory = """
       UPDATE menuItem AND inventoryItem
       SET availableStock =%d
       WHERE itemname = '%s';
       """;


}