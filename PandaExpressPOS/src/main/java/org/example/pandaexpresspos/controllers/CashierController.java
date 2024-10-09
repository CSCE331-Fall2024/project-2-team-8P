package org.example.pandaexpresspos.controllers;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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
    public Button GrilledTeriyaki;

    @FXML
    public Button BroccoliBeef;

    @FXML
    public Button KungPao;

    @FXML
    public Button OrangeChicken;

    @FXML
    public Button BlackPepperChicken;

    @FXML
    public Button BlackPepperSteak;

    @FXML
    public Button HoneyWalnut;

    @FXML
    public Button HoneySesame;

    @FXML
    public Button HotOnes;

    @FXML
    public Button BeijingBeef;

    @FXML
    public Button Coke;

    @FXML
    public Button Water;

    @FXML
    public Button SweetTea;

    @FXML
    public Button DrPepper;

    @FXML
    public Button WhiteRice;

    @FXML
    public Button FriedRice;

    @FXML
    public Button SuperGreens;

    @FXML
    public Button ChowMein;

    @FXML
    public Button ChickenEggRoll;

    @FXML
    public Button Rangoon;

    @FXML
    public Button clear;
    @FXML
    public Button placeOrder;

    @FXML
    public Button clearNum;

    @FXML
    private TextField taxField;

    @FXML
    private TextField totalField;

    private ObservableList<Item> orderItems = FXCollections.observableArrayList();
    private static final double TAX_RATE = 0.0825;

    // Initialize method to set up the table columns
    @FXML
    public void initialize() {
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.setItems(orderItems);


        GrilledTeriyaki.setOnAction(event -> addItemToOrder("Grilled Teriyaki",1,5.2));
        BroccoliBeef.setOnAction(event -> addItemToOrder("Broccoli Beef",1,5.2));
        KungPao.setOnAction(event -> addItemToOrder("Kung Pao",1,5.2));
        OrangeChicken.setOnAction(event -> addItemToOrder("Orange Chicken",1,5.2));
        BlackPepperChicken.setOnAction(event -> addItemToOrder("Black Pepper Chicken",1,5.2));
        BlackPepperSteak.setOnAction(event -> addItemToOrder("Black Pepper Steak",1,6.7));
        HoneyWalnut.setOnAction(event -> addItemToOrder("Honey Walnut",1,6.7));
        HoneySesame.setOnAction(event -> addItemToOrder("Honey Sesame",1,5.2));
        HotOnes.setOnAction(event -> addItemToOrder("HotOnes",1,5.2));
        BeijingBeef.setOnAction(event -> addItemToOrder("Beijing Beef",1,5.2));
        Coke.setOnAction(event -> addItemToOrder("Coke",1,2.1));
        Water.setOnAction(event -> addItemToOrder("Water",1,2.1));
        DrPepper.setOnAction(event -> addItemToOrder("Dr Pepper",1,2.1));
        SweetTea.setOnAction(event -> addItemToOrder("Sweet Tea",1,2.1));
        WhiteRice.setOnAction(event -> addItemToOrder("White Rice",1,4.4));
        FriedRice.setOnAction(event -> addItemToOrder("Fried Rice",1,4.4));
        SuperGreens.setOnAction(event -> addItemToOrder("Super Greens",1,4.4));
        ChowMein.setOnAction(event -> addItemToOrder("Chow Mein",1,4.4));
        Rangoon.setOnAction(event -> addItemToOrder("Rangoon",1,2.0));
        ChickenEggRoll.setOnAction(event -> addItemToOrder("Chicken Egg Roll",1,2.0));
        clear.setOnAction(event -> clearTable());
        placeOrder.setOnAction(event -> addOrder());
    }

    // Method to add an item to the order
    private void addItemToOrder(String itemName, int quantity, double price) {
        // Logic to add the item to the order
        // For example, update the TableView with the new item
        for (Item item : orderItems) {
            if (item.getItemName().equals(itemName)) {
                // If it exists, update the quantity and price
                item.setQuantity(item.getQuantity() + quantity);

                //TODO: have to add quantity -- for the inventory


                item.setPrice(item.getPrice() + price * quantity);
                orderTable.refresh();
                updateTotals();// Update total price
                return;
            }
        }

        // If the item does not exist, add a new item
        //TODO: have to add quantity -- for the inventory
        orderItems.add(new Item(itemName, quantity, price));
        updateTotals();
    }

    @FXML
    private void clearTable() {
        orderItems.clear();
        updateTotals();

    }

    private void addOrder(){
        //TODO: have to add order to the database


        orderItems.clear();
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = orderItems.stream().mapToDouble(Item::getPrice).sum();
        double tax = subtotal * TAX_RATE;
        double total = subtotal + tax;

        taxField.setText("Tax: " + String.format("$%.2f", tax));
        totalField.setText("Total: "+String.format("$%.2f", total));
    }


    }






