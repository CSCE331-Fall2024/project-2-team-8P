package org.example.pandaexpresspos.models.wrappers;

import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;

public class InventoryItemWithQty {
    public InventoryItem inventoryItem;
    public Integer quantity;

    public InventoryItemWithQty(InventoryItem inventoryItem, Integer quantity) {
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
    }
}
