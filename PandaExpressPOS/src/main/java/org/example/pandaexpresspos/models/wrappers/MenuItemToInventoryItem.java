package org.example.pandaexpresspos.models.wrappers;

import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;

/**
 * The MenuItemToInventoryItem class serves as a mapping between a MenuItem
 * and its corresponding InventoryItem.
 * This class is useful for tracking the inventory requirements of menu items.
 *
 * @author Kevin Zhang
 */
public class MenuItemToInventoryItem {
    /**
     * The menu item associated with the inventory item
     */
    public MenuItem menuItem;

    /**
     * The inventory item associated with the menu item
     */
    public InventoryItem inventoryItem;

    /**
     * Constructor to create an instance of MenuItemToInventoryItem.
     *
     * @param menuItem      the menu item to be mapped
     * @param inventoryItem the inventory item associated with the menu item
     */
    public MenuItemToInventoryItem(MenuItem menuItem, InventoryItem inventoryItem) {
        this.menuItem = menuItem;
        this.inventoryItem = inventoryItem;
    }
}
