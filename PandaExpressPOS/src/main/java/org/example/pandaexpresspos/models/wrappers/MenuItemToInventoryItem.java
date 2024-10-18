package org.example.pandaexpresspos.models.wrappers;

import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;

public class MenuItemToInventoryItem {
    public MenuItem menuItem;
    public InventoryItem inventoryItem;

    public MenuItemToInventoryItem(MenuItem menuItem, InventoryItem inventoryItem) {
        this.menuItem = menuItem;
        this.inventoryItem = inventoryItem;
    }
}
