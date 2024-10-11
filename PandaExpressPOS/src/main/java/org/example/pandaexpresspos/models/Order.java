package org.example.pandaexpresspos.models;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class Order {
    public UUID orderId;
    public UUID cashierId;
    public Integer month;
    public Integer week;
    public Integer day;
    public Integer hour;
    public Double price;

    public Map<InventoryItem, Integer> inventoryItems = new HashMap<>();
    public Map<MenuItem, Integer> menuItems = new HashMap<>();

    public Order(
            UUID cashierId,
            Integer month,
            Integer week,
            Integer day,
            Integer hour,
            Double price)
    {
        this(UUID.randomUUID(), cashierId, month, week, day, hour, price);
    }

    public Order(
            UUID orderId,
            UUID cashierId,
            Integer month,
            Integer week,
            Integer day,
            Integer hour,
            Double price)
    {
        this.orderId = orderId;
        this.cashierId = cashierId;
        this.month = month;
        this.week = week;
        this.day = day;
        this.hour = hour;
        this.price = price;
    }

    public void addInventoryItem(InventoryItem item, int quantity) {
        inventoryItems.put(item, quantity);
    }

    public void removeInventoryItem(InventoryItem item) {
        inventoryItems.remove(item);
    }

    public void addMenuItem(MenuItem item, int quantity) {
        menuItems.put(item, quantity);
    }

    public void removeMenuItem(MenuItem item) {
        menuItems.remove(item);
    }

    public void setPrice(double price){
        this.price = price;
    }
}
