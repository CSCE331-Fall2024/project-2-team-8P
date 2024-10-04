package org.example.pandaexpresspos.models;

import java.util.UUID;

public class InventoryItem {
    public UUID inventoryItemId;
    public Double cost;
    public Integer availableStock;
    public String itemName;

    public InventoryItem(UUID inventoryItemId, Double cost, Integer availableStock, String itemName) {
        this.inventoryItemId = inventoryItemId;
        this.cost = cost;
        this.availableStock = availableStock;
        this.itemName = itemName;
    }

    public InventoryItem(Double cost, Integer availableStock, String itemName) {
        this(UUID.randomUUID(), cost, availableStock, itemName);
    }
}
