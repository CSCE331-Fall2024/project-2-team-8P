package org.example.pandaexpresspos.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MenuItem {
    public UUID menuItemId;
    public Double price;
    public Integer availableStock;
    public String itemName;

    public Map<InventoryItem, Integer> inventoryItems = new HashMap<>();

    public MenuItem(UUID menuItemId, Double price, Integer availableStock, String itemName) {
        this.menuItemId = menuItemId;
        this.price = price;
        this.availableStock = availableStock;
        this.itemName = itemName;
    }

    public MenuItem(Double price, Integer availableStock, String itemName) {
        this(UUID.randomUUID(), price, availableStock, itemName);
    }

    public void addOrUpdateInventoryItem(InventoryItem item, Integer quantity) {
        inventoryItems.put(item, quantity);
    }

    public Boolean isAvailable() {
        for (InventoryItem item : inventoryItems.keySet()) {
            if (item.availableStock <= 0)
                return false;
        }
        return true;
    }
}
