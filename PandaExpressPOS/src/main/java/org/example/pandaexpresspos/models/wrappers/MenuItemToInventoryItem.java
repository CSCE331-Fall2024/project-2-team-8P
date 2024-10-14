package org.example.pandaexpresspos.models.wrappers;

import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;

public class MenuItemToInventoryItem {
    public MenuItem menuItem;
    public InventoryItem inventoryItem;
    public Integer quantity;

    public MenuItemToInventoryItem(MenuItem menuItem, InventoryItem inventoryItem, Integer quantity) {
        this.menuItem = menuItem;
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
    }
}
