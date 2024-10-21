package org.example.pandaexpresspos.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * The MenuItem class represents an item on the menu in the Panda Express POS system.
 * It stores information about the menu item, including its ID, price, name, and associated inventory items.
 *
 * @author Kevin Zhang
 */
public class MenuItem {
    /**
     * Unique identifier for the menu item
     */
    public UUID menuItemId;

    /**
     * Price of the menu item
     */
    public Double price;

    /**
     * Name of the menu item
     */
    public String itemName;

    /**
     * A map to store inventory items required to create the menu item and their quantities
     */
    public Map<InventoryItem, Integer> inventoryItems = new HashMap<>();

    /**
     * Constructor to create a MenuItem with a specified ID.
     *
     * @param menuItemId the unique ID of the menu item
     * @param price      the price of the menu item
     * @param itemName   the name of the menu item
     */
    public MenuItem(UUID menuItemId, Double price, String itemName) {
        this.menuItemId = menuItemId;
        this.price = price;
        this.itemName = itemName;
    }

    /**
     * Constructor to create a MenuItem with an automatically generated ID.
     *
     * @param price    the price of the menu item
     * @param itemName the name of the menu item
     */
    public MenuItem(Double price, String itemName) {
        this(UUID.randomUUID(), price, itemName);
    }

    /**
     * Adds or updates the quantity of an InventoryItem required for this menu item.
     *
     * @param item     the inventory item to be added or updated
     * @param quantity the quantity of the inventory item required for the menu item
     */
    public void addOrUpdateInventoryItem(InventoryItem item, Integer quantity) {
        inventoryItems.put(item, quantity);
    }

    /**
     * Checks if the menu item is available based on the availability of its required inventory items.
     *
     * @return true if all inventory items required for the menu item are available, false otherwise
     */
    public Boolean isAvailable() {
        for (InventoryItem item : inventoryItems.keySet()) {
            if (item.availableStock <= 0)
                return false;
        }
        return true;
    }
}
