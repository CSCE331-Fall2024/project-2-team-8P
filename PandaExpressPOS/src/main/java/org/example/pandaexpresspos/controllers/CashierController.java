package org.example.pandaexpresspos.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.example.pandaexpresspos.models.MenuItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

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
    public Button clear, placeOrder, clearNum, Enter, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;

    @FXML
    private TextField taxField, totalField;
    @FXML
    private GridPane menuItemGridPane;

    private ObservableList<Item> orderItems = FXCollections.observableArrayList();
    private static final double TAX_RATE = 0.0825;

    private String lastSelectedItem = null;
    private double lastSelectedPrice = 0.0;
    private StringBuilder currentQuantity = new StringBuilder();

    ArrayList<MenuItem> menuItems = new ArrayList<>();

    // Global sample image
    // Global Constant for Images
    String sampleImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/BeijingBeef.png").toExternalForm();

    //TODO: Query database for items
    public void populateMenuItems() {
        menuItems.add(new MenuItem(9.99, 100, "Chow Mein"));
        menuItems.add(new MenuItem(8.99, 120, "Fried Rice"));
        menuItems.add(new MenuItem(7.99, 130, "White Steamed Rice"));
        menuItems.add(new MenuItem(8.49, 110, "Super Greens"));
        menuItems.add(new MenuItem(11.49, 80, "Hot Ones Blazing Bourbon Chicken"));
        menuItems.add(new MenuItem(10.99, 90, "The Original Orange Chicken"));
        menuItems.add(new MenuItem(12.99, 60, "Black Pepper Sirloin Steak"));
        menuItems.add(new MenuItem(13.99, 50, "Honey Walnut Shrimp"));
        menuItems.add(new MenuItem(11.99, 70, "Grilled Teriyaki Shrimp"));
        menuItems.add(new MenuItem(10.49, 100, "Broccoli Beef"));
        menuItems.add(new MenuItem(9.99, 90, "Kung Pao Chicken"));
        menuItems.add(new MenuItem(10.49, 85, "Honey Sesame Chicken Breast"));
        menuItems.add(new MenuItem(9.99, 110, "Beijing Beef"));
        menuItems.add(new MenuItem(8.49, 100, "Black Pepper Chicken"));
        menuItems.add(new MenuItem(5.49, 200, "Cream Cheese Rangoon"));
        menuItems.add(new MenuItem(4.99, 150, "Chicken Egg Roll"));
        menuItems.add(new MenuItem(1.99, 300, "Dr. Pepper"));
        menuItems.add(new MenuItem(1.49, 350, "Aquafina"));
        menuItems.add(new MenuItem(2.49, 300, "Sweet Tea"));
        menuItems.add(new MenuItem(1.99, 300, "Pepsi"));
    }

    public void createInventoryGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        menuItemGridPane.setHgap(10);
        menuItemGridPane.setAlignment(Pos.CENTER);

        for (MenuItem item : menuItems) {

            String itemImg = sampleImg;
            String itemName = item.itemName;
            String itemStock = String.valueOf(item.availableStock);


            // Create a vertical box for image and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button, set background to img
            Button button = new Button();
            button.setMinSize(60, 60);
            button.setStyle("-fx-background-image: url('" + itemImg + "');" +
                    "-fx-background-size: cover;");

//            // Handle clicks
//            button.setOnMouseClicked(e ->{
//                this.updateInventoryItem(Optional.of(item));
//            });

            // Create labels
            Label nameLabel = new Label(itemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            Label itemStockLabel = new Label("Qty: " + itemStock);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, nameLabel, itemStockLabel);

            // Add vbox to grid
            menuItemGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }

        }


    }

    @FXML
    public void initialize() {

        populateMenuItems();
        createInventoryGrid();

        itemColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.setItems(orderItems);


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