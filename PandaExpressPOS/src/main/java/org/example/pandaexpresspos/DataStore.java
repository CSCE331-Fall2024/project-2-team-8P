package org.example.pandaexpresspos;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataStore {
    private static DataStore instance = new DataStore();
    private int numItems = 3;
    private HashMap<Button, Integer> inventoryItems = new HashMap<>();
    private HashMap<Button, ArrayList<String>> menuItems = new HashMap<>();

    // Private constructor to enforce singleton pattern
    private DataStore() {
        // Initialize menuItems with buttons and their corresponding item lists
        menuItems.put(new Button("Button A"), new ArrayList<>(Arrays.asList("Chow Mein", "2.50")));
        menuItems.put(new Button("Button B"), new ArrayList<>(Arrays.asList("Fried Rice", "2.50")));
        menuItems.put(new Button("Button C"), new ArrayList<>(Arrays.asList("Orange Chicken", "2.50")));

        // Initialize inventoryItems with buttons and corresponding inventory values
        inventoryItems.put(new Button("Napkins"), 50);
        inventoryItems.put(new Button("Silverware"), 100);
    }

    // Singleton instance getter
    public static DataStore getInstance() {
        return instance;
    }

    // Get number of menu items
    public int getNumItems() {
        return numItems;
    }

    // Set number of menu items
    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    // Get the menuItems HashMap
    public HashMap<Button, ArrayList<String>> getMenuItems() {
        return menuItems;
    }

    // Set the menuItems HashMap directly
    public void setMenuItems(HashMap<Button, ArrayList<String>> menuItems) {
        this.menuItems = menuItems;
    }

    // Get the inventoryItems HashMap
    public HashMap<Button, Integer> getInventoryItems() {
        return inventoryItems;
    }

    // Set the inventoryItems HashMap directly
    public void setInventoryItems(HashMap<Button, Integer> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    // Get the inventory amount for a specific button
    public Integer getInventoryForItem(Button itemButton) {
        return inventoryItems.get(itemButton);
    }

    // Set the inventory amount for a specific item (button)
    public void setInventoryForItem(Button itemButton, Integer newInventory) {
        inventoryItems.put(itemButton, newInventory);
    }
}
