package org.example.pandaexpresspos.models.wrappers;

import org.example.pandaexpresspos.models.MenuItem;

public class MenuItemWithQty {
    public MenuItem menuItem;
    public int quantity;

    public MenuItemWithQty(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }
}
