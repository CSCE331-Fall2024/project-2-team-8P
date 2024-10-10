package org.example.pandaexpresspos.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashierController {

    @FXML
    private TableView<Item> orderTable;

    @FXML
    private TableColumn<Item, String> itemColumn;

    @FXML
    private TableColumn<Item, Integer> quantityColumn;

    @FXML
    private TableColumn<Item, Double> priceColumn;

    @FXML
    public Button GrilledTeriyaki, BroccoliBeef, KungPao, OrangeChicken, BlackPepperChicken, BlackPepperSteak, HoneyWalnut, HoneySesame, HotOnes, BeijingBeef, Coke, Water, SweetTea, DrPepper, WhiteRice, FriedRice, SuperGreens, ChowMein, ChickenEggRoll, Rangoon, clear, placeOrder, clearNum, Enter, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @FXML
    private TextField taxField, totalField;

    private ObservableList<Item> orderItems = FXCollections.observableArrayList();
    private static final double TAX_RATE = 0.0825;

    private String lastSelectedItem = null;
    private double lastSelectedPrice = 0.0;
    private StringBuilder currentQuantity = new StringBuilder();

    @FXML
    public void initialize() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.setItems(orderItems);

        // Product buttons
        GrilledTeriyaki.setOnAction(event -> selectItem("Grilled Teriyaki", 5.2));
        BroccoliBeef.setOnAction(event -> selectItem("Broccoli Beef", 5.2));
        KungPao.setOnAction(event -> selectItem("Kung Pao", 5.2));
        OrangeChicken.setOnAction(event -> selectItem("Orange Chicken", 5.2));
        BlackPepperChicken.setOnAction(event -> selectItem("Black Pepper Chicken", 5.2));
        BlackPepperSteak.setOnAction(event -> selectItem("Black Pepper Steak", 6.7));
        HoneyWalnut.setOnAction(event -> selectItem("Honey Walnut", 6.7));
        HoneySesame.setOnAction(event -> selectItem("Honey Sesame", 5.2));
        HotOnes.setOnAction(event -> selectItem("HotOnes", 5.2));
        BeijingBeef.setOnAction(event -> selectItem("Beijing Beef", 5.2));
        Coke.setOnAction(event -> selectItem("Coke", 2.1));
        Water.setOnAction(event -> selectItem("Water", 2.1));
        DrPepper.setOnAction(event -> selectItem("Dr Pepper", 2.1));
        SweetTea.setOnAction(event -> selectItem("Sweet Tea", 2.1));
        WhiteRice.setOnAction(event -> selectItem("White Rice", 4.4));
        FriedRice.setOnAction(event -> selectItem("Fried Rice", 4.4));
        SuperGreens.setOnAction(event -> selectItem("Super Greens", 4.4));
        ChowMein.setOnAction(event -> selectItem("Chow Mein", 4.4));
        Rangoon.setOnAction(event -> selectItem("Rangoon", 2.0));
        ChickenEggRoll.setOnAction(event -> selectItem("Chicken Egg Roll", 2.0));

        // Numpad buttons
        Button[] numpadButtons = {button0, button1, button2, button3, button4, button5, button6, button7, button8, button9};
        for (int i = 0; i < numpadButtons.length; i++) {
            final int num = i;
            numpadButtons[i].setOnAction(event -> appendQuantity(num));
        }

        Enter.setOnAction(event -> updateSelectedItemQuantity());
        clearNum.setOnAction(event -> clearQuantity());

        clear.setOnAction(event -> clearTable());
        placeOrder.setOnAction(event -> addOrder());
    }

    private void selectItem(String itemName, double price) {
        lastSelectedItem = itemName;
        lastSelectedPrice = price;
        currentQuantity.setLength(0); // Clear the current quantity
        addItemToOrder(itemName, 1, price); // Add one item immediately
    }

    private void appendQuantity(int num) {
        currentQuantity.append(num);
    }

    private void updateSelectedItemQuantity() {
        if (lastSelectedItem != null && currentQuantity.length() > 0) {
            int quantity = Integer.parseInt(currentQuantity.toString());
            updateItemQuantity(lastSelectedItem, quantity, lastSelectedPrice);
            currentQuantity.setLength(0); // Clear the quantity after updating
        }
    }

    private void clearQuantity() {
        currentQuantity.setLength(0);
    }

    private void updateItemQuantity(String itemName, int quantity, double price) {
        BigDecimal priceBD = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);

        for (Item item : orderItems) {
            if (item.getItemName().equals(itemName)) {
                item.setQuantity(quantity);

                // Calculate the new price based on the new quantity
                BigDecimal newPrice = priceBD.multiply(BigDecimal.valueOf(quantity)).setScale(2, RoundingMode.HALF_UP);

                item.setPrice(newPrice.doubleValue());
                orderTable.refresh();
                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        orderItems.add(new Item(itemName, quantity, priceBD.multiply(BigDecimal.valueOf(quantity)).doubleValue()));
        updateTotals();
    }

    private void addItemToOrder(String itemName, int quantity, double price) {
        BigDecimal priceBD = BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);

        for (Item item : orderItems) {
            if (item.getItemName().equals(itemName)) {
                item.setQuantity(item.getQuantity() + quantity);

                // Calculate the new price and round it
                BigDecimal newPrice = priceBD.multiply(BigDecimal.valueOf(item.getQuantity())).setScale(2, RoundingMode.HALF_UP);

                item.setPrice(newPrice.doubleValue());
                orderTable.refresh();
                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        orderItems.add(new Item(itemName, quantity, priceBD.multiply(BigDecimal.valueOf(quantity)).doubleValue()));
        updateTotals();
    }

    @FXML
    private void clearTable() {
        orderItems.clear();
        updateTotals();
    }

    private void addOrder() {
        // TODO: have to add order to the database

        orderItems.clear();
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = orderItems.stream().mapToDouble(Item::getPrice).sum();
        BigDecimal subtotalBD = BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP);

        double tax = subtotalBD.multiply(BigDecimal.valueOf(TAX_RATE)).doubleValue();
        BigDecimal taxBD = BigDecimal.valueOf(tax).setScale(2, RoundingMode.HALF_UP);

        double total = subtotalBD.add(taxBD).doubleValue();
        BigDecimal totalBD = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);

        taxField.setText("Tax: " + String.format("$%.2f", taxBD.doubleValue()));
        totalField.setText("Total: " + String.format("$%.2f", totalBD.doubleValue()));
    }
}