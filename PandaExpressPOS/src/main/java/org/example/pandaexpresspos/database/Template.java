package org.example.pandaexpresspos.database;

public class Template {
    public static String AddOrder ="""
        INSERT INTO "order" (orderId, cashierId, month, week, day, hour, price)
        VALUES ('%s', '%s', %d, %d, %d, %d, %d);
    """;
    public static String AddOrderToMenuItem ="""
        INSERT INTO ordertomenuitem (orderid, menuitemid, quantity)
        VALUES ('%s', '%s', %d);
    """;
    public static String AddOrderToInventoryItem ="""
        INSERT INTO ordertoinventoryitem (orderId, inventoryItemId, quantity)
        VALUES ('%s', '%s', %d);
        """;
    public static String UpdateMenu = """
       UPDATE menuItem 
       SET availableStock =%d
       WHERE itemname = '%s';
       """;
    public static String UpdateInventory = """
       UPDATE inventoryitem 
       SET availableStock =%d
           WHERE itemname = '%s';
       """;


}