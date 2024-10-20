package org.example.pandaexpresspos.models;

import java.util.UUID;

/**
 * The InventoryItem class represents an item in the inventory for the Panda Express POS system.
 * It stores information about the inventory item, including its ID, cost, available stock, and name.
 */
public class InventoryItem {
    /** Unique identifier for the inventory item */
    public UUID inventoryItemId;

    /** The cost of the inventory item */
    public Double cost;

    /** The amount of stock available for this inventory item */
    public Integer availableStock;

    /** The name of the inventory item */
    public String itemName;

    /**
     * Constructor to create an InventoryItem with a specified ID.
     *
     * @param inventoryItemId the unique ID of the inventory item
     * @param cost the cost of the inventory item
     * @param availableStock the available stock of the inventory item
     * @param itemName the name of the inventory item
     */
    public InventoryItem(UUID inventoryItemId, Double cost, Integer availableStock, String itemName) {
        this.inventoryItemId = inventoryItemId;
        this.cost = cost;
        this.availableStock = availableStock;
        this.itemName = itemName;
    }

    /**
     * Constructor to create an InventoryItem with an automatically generated ID.
     *
     * @param cost the cost of the inventory item
     * @param availableStock the available stock of the inventory item
     * @param itemName the name of the inventory item
     */
    public InventoryItem(Double cost, Integer availableStock, String itemName) {
        this(UUID.randomUUID(), cost, availableStock, itemName);
    }
}
