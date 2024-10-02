package org.example.pandaexpresspos.models;

import java.util.UUID;

public class MenuItem {
    public UUID menuItemId;
    public Double price;
    public Integer availableStock;
    public String itemName;

    public MenuItem(UUID menuItemId, Double price, Integer availableStock, String itemName) {
        this.menuItemId = menuItemId;
        this.price = price;
        this.availableStock = availableStock;
        this.itemName = itemName;
    }

    public MenuItem(Double price, Integer availableStock, String itemName) {
        this(UUID.randomUUID(), price, availableStock, itemName);
    }
}
