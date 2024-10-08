package org.example.pandaexpresspos;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataStore {
    private static DataStore instance = new DataStore();
    private HashMap<Button, Integer> inventoryItems = new HashMap<>();
    private HashMap<Button, ArrayList<String>> menuItems = new HashMap<>();
    private HashMap<Button, ArrayList<String>> employees = new HashMap<>();

    private DataStore() {
        // Arbitrary values for testing, can change later
        menuItems.put(new Button("Chow Mein"), new ArrayList<>(Arrays.asList("Chow Mein", "2.50")));
        menuItems.put(new Button("Fried Rice"), new ArrayList<>(Arrays.asList("Fried Rice", "2.50")));
        menuItems.put(new Button("Orange Chicken"), new ArrayList<>(Arrays.asList("Orange Chicken", "2.50")));

        inventoryItems.put(new Button("Napkins"), 50);
        inventoryItems.put(new Button("Silverware"), 100);

        employees.put(new Button("Pikachu"), new ArrayList<>(Arrays.asList("Pikachu", "Manager")));
        employees.put(new Button("Charizard"), new ArrayList<>(Arrays.asList("Charizard", "Cashier")));
    }

    public static DataStore getInstance() {
        return instance;
    }

    // Get menuItems
    public HashMap<Button, ArrayList<String>> getMenuItems() {
        return menuItems;
    }

    // Set menuItems
    public void setMenuItems(HashMap<Button, ArrayList<String>> menuItems) {
        this.menuItems = menuItems;
    }

    // Get inventoryItems
    public HashMap<Button, Integer> getInventoryItems() {
        return inventoryItems;
    }

    // Set inventoryItems
    public void setInventoryItems(HashMap<Button, Integer> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    // Get inventory amount for specific item
    public Integer getInventoryForItem(Button itemButton) {
        return inventoryItems.get(itemButton);
    }

    // Set inventory amount for specific item
    public void setInventoryForItem(Button itemButton, Integer newInventory) {
        inventoryItems.put(itemButton, newInventory);
    }

    // Get employees
    public HashMap<Button, ArrayList<String>> getEmployees() {
        return employees;
    }

    // Set employees
    public void setEmployees(HashMap<Button, ArrayList<String>> employees) {
        this.employees = employees;
    }

}
