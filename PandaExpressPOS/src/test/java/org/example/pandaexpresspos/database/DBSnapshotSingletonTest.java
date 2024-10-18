package org.example.pandaexpresspos.database;

import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.util.Map;

import static java.lang.System.out;
import static org.junit.jupiter.api.Assertions.*;

class DBSnapshotSingletonTest {

    private static DBSnapshotSingleton snapshot;

    @BeforeAll
    static void setUp() {
        snapshot = DBSnapshotSingleton.getInstance();
    }

    @Test
    void getMenuSnapshot() {
        snapshot.refreshMenuSnapshot();
        Map<String, MenuItem> menuSnapshot = snapshot.getMenuSnapshot();
        for (Map.Entry<String, MenuItem> entry : menuSnapshot.entrySet()) {
            out.println("Menu Item: " + entry.getKey());

            Map<InventoryItem, Integer> associatedInventory = entry.getValue().inventoryItems;
            out.println("Associated inventory items:");
            for (InventoryItem item : associatedInventory.keySet()) {
                out.println("\t- " + item.itemName);
            }

            printSeparator();
        }
    }

    private void printSeparator() {
        out.println("-".repeat(100));
    }
}