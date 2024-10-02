package org.example.pandaexpresspos.models;

import java.util.UUID;

public class InventoryItem {
    public UUID inventoryItemId;
    public Double price;
    public Integer availableStock;
    public String itemName;

    public InventoryItem(UUID inventoryItemId, Double price, Integer availableStock, String itemName) {
        this.inventoryItemId = inventoryItemId;
        this.price = price;
        this.availableStock = availableStock;
        this.itemName = itemName;
    }

    public InventoryItem(Double price, Integer availableStock, String itemName) {
        this(UUID.randomUUID(), price, availableStock, itemName);
    }
}
