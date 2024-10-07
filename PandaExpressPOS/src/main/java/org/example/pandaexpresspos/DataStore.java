package org.example.pandaexpresspos;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataStore {
    private static DataStore instance = new DataStore();
    private int numItems = 3;
    private int price = 0;
    // Initialize the HashMap with buttons and their associated item lists
    private HashMap<Button, ArrayList<String>> menuItems = new HashMap<>();

    private DataStore() {
        // Initialize menuItems with buttons and their corresponding item lists
        menuItems.put(new Button("Button A"), new ArrayList<>(Arrays.asList("Item A1", "Item A2")));
        menuItems.put(new Button("Button B"), new ArrayList<>(Arrays.asList("Item B1", "Item B2")));
        menuItems.put(new Button("Button C"), new ArrayList<>(Arrays.asList("Item C1", "Item C2")));
    }

    public static DataStore getInstance() {
        return instance;
    }

    public int getNumItems() {
        return numItems;
    }

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

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
