package org.example.pandaexpresspos.models;

import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

/**
 * The Order class represents a customer order in the Panda Express POS system.
 * It stores information about the order, including the order ID, cashier ID, date and time details,
 * total price, and items purchased. The items are divided into two categories: inventory items and menu items.
 */
public class Order {
    /** Unique identifier for the order */
    public UUID orderId;

    /** Unique identifier for the cashier handling the order */
    public UUID cashierId;

    /** Month the order was placed */
    public Integer month;

    /** Week the order was placed */
    public Integer week;

    /** Day the order was placed */
    public Integer day;

    /** Hour the order was placed */
    public Integer hour;

    /** Total price of the order */
    public Double price;

    /** A map to store inventory items in the order and their quantities */
    public Map<InventoryItem, Integer> inventoryItems = new HashMap<>();

    /** A map to store menu items in the order and their quantities */
    public Map<MenuItem, Integer> menuItems = new HashMap<>();

    /**
     * Constructor to create an Order with an automatically generated order ID.
     *
     * @param cashierId the unique ID of the cashier handling the order
     * @param month the month the order was placed
     * @param week the week the order was placed
     * @param day the day the order was placed
     * @param hour the hour the order was placed
     * @param price the total price of the order
     */
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

    /**
     * Constructor to create an Order with a specified order ID.
     *
     * @param orderId the unique ID of the order
     * @param cashierId the unique ID of the cashier handling the order
     * @param month the month the order was placed
     * @param week the week the order was placed
     * @param day the day the order was placed
     * @param hour the hour the order was placed
     * @param price the total price of the order
     */
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

    /**
     * Adds or updates the quantity of an InventoryItem in the order.
     *
     * @param item the inventory item to be added or updated
     * @param quantity the quantity of the item
     */
    public void addOrUpdateInventoryItem(InventoryItem item, int quantity) {
        inventoryItems.put(item, quantity);
    }

    /**
     * Adds or updates the quantity of a MenuItem in the order.
     * Adjusts the total price of the order based on the new quantity of the menu item.
     *
     * @param item the menu item to be added or updated
     * @param quantity the quantity of the item
     */
    public void addOrUpdateMenuItem(MenuItem item, int quantity) {
        double priceDiff = (quantity - menuItems.getOrDefault(item, 0)) * item.price;
        price += priceDiff;
        menuItems.put(item, quantity);
    }
}
