package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.database.DBSnapshotSingleton;
import org.example.pandaexpresspos.models.*;
import org.example.pandaexpresspos.models.Employee;
import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class CashierController {

    private final DBDriverSingleton dbDriver = DBDriverSingleton.getInstance();
    private final DBSnapshotSingleton dbSnapshot = DBSnapshotSingleton.getInstance();

    private Employee loggedInUser;

    // New Order instance
    private Order currentOrder; // Added to manage the current order

    private Order PreviousOrder;

    @FXML
    private TableView<Map.Entry<MenuItem, Integer>> orderTable; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, String> nameColumn; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, Integer> quantityColumn; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, Double> priceColumn; // Updated type to OrderItem

    @FXML
    public Button
            clear,
            placeOrder,
            clearNum,
            Enter,
            button0,
            button1,
            button2,
            button3,
            button4,
            button5,
            button6,
            button7,
            button8,
            button9,
            Logout;

    @FXML
    private TextField cashierTextField;

    @FXML
    private TextField
//            taxField,
            totalField;

    @FXML
    private GridPane menuItemGridPane;

    private ObservableList<Map.Entry<MenuItem, Integer>> orderItems;

    // constants for global use
    private static final double TAX_RATE = 0.0825;

    private LocalDate currentDate = LocalDate.now();
    private Integer month = currentDate.getMonthValue();
    private Integer week = (currentDate.getMonthValue()
            * currentDate.getDayOfMonth()) / 7;
    private Integer day = currentDate.getDayOfMonth();
    private Integer hour = Calendar.HOUR_OF_DAY;


    private MenuItem lastSelectedItem = null;

    private StringBuilder currentQuantity = new StringBuilder();

    @FXML
    void logOutUser(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }

    // Constant sample image

    private final String sampleImg = getClass()
            .getResource("/org/example/pandaexpresspos/fxml/images/sample_image.png")
            .toExternalForm();


    @FXML
    public void initialize() {
        dbSnapshot.refreshAllSnapshots();

        resetCurrentOrder();

        createMenuItemGrid();
        initializeButtons();
        initializeTableView();
    }

    public void setLoggedInUser(Employee user) {
        loggedInUser = user;
        if (cashierTextField != null) {
            cashierTextField.setText("Cashier: " + loggedInUser.name);
        }
    }

    public void createMenuItemGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        menuItemGridPane.setHgap(10);
        menuItemGridPane.setVgap(50);
        menuItemGridPane.setAlignment(Pos.CENTER);
        menuItemGridPane.setStyle("-fx-padding: 10;");

        for (MenuItem menuItem : dbSnapshot.getMenuSnapshot().values()) {
            String itemName = menuItem.itemName;
            String itemImg;
            try {
                itemImg = getClass()
                        .getResource("/org/example/pandaexpresspos/fxml/images/" + itemName + ".png")
                        .toExternalForm();
            } catch (Exception e) {
                itemImg = sampleImg;
            }

            // Create a vertical box for image and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button for every menu item in our grid of menu items
            Button menuItemBtn = new Button();
            menuItemBtn.setMinSize(100, 80);

            // Gray out items that don't have enough inventory
            if (!menuItem.isAvailable()) {
                menuItemBtn.setStyle(
                        "-fx-background-image: url('" + itemImg + "');" +
                                "-fx-background-size: cover;" +
                                "-fx-cursor: hand;" +
                                "-fx-background-color: rgba(128, 128, 128, 0.5)" + // grey with 50% transparency
                                "-fx-background-blend-mode: overlay;" // ensures transparency is blended with the image
                );
                menuItemBtn.setDisable(true);
            } else {
                menuItemBtn.setStyle("-fx-background-image: url('" + itemImg + "');" +
                        "-fx-background-size: cover;-fx-cursor: hand;");
            }

            // Handle when the user clicks on a menu item
            menuItemBtn.setOnMouseClicked(e -> {
                // If this returns false, then there wasn't enough inventory to add the item to the order
                if (tryAddItemToOrder(menuItem)) {
                    dbDriver.decreaseMenuItemInventoryQuantity(menuItem, 1);
                }

                menuItemGridPane.getChildren().clear();
                createMenuItemGrid();
            });

            // Create labels with styles
            Label nameLabel = new Label(itemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            nameLabel.setStyle("-fx-padding:5;-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: white");

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(menuItemBtn, nameLabel);

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

    private void initializeTableView() {
        nameColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getKey().itemName)
        );
        quantityColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getValue())
        );
        priceColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getKey().price)
        );

        orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
        orderTable.setItems(orderItems);
    }

    private void initializeButtons() {
        // Numpad buttons
        Button[] numpadButtons = {
                button0,
                button1,
                button2,
                button3,
                button4,
                button5,
                button6,
                button7,
                button8,
                button9
        };
        for (int i = 0; i < numpadButtons.length; i++) {
            final int num = i;
            numpadButtons[i].setOnAction(event -> appendQuantity(num));
        }

        Enter.setOnAction(event -> updateSelectedItemQuantity());
        clearNum.setOnAction(event -> clearQuantity());

        clear.setOnAction(event -> clearTable());
        placeOrder.setOnAction(event -> addOrder());
    }

    private Boolean tryAddItemToOrder(MenuItem item) {
        boolean hasEnoughInventory = item.isAvailable();

        if (!hasEnoughInventory) {
            showAlert("Cannot Fulfill Order", "Inventory Item out of stock");
        } else {
            lastSelectedItem = item;
            currentQuantity.setLength(0); // Clear the current quantity
            addItemToOrder(item); // Add one item immediately
        }

        return hasEnoughInventory;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void appendQuantity(int num) {
        currentQuantity.append(num);
    }

    private void updateSelectedItemQuantity() {
        if (!lastSelectedItem.isAvailable()) {
            showAlert("Cannot Fulfill Order", "Inventory Item out of stock");
            return;
        }

        int quantity = Integer.parseInt(currentQuantity.toString());
        if (lastSelectedItem != null && !currentQuantity.isEmpty()) {
            // If we are updating the quantity of `lastSelectedItem`, we need to find
            // the difference between our current quantity and the previous quantity
            int oldQuantity = currentOrder.menuItems.getOrDefault(lastSelectedItem, 0);
            int quantityDiff = quantity - oldQuantity;

            dbDriver.decreaseMenuItemInventoryQuantity(lastSelectedItem, quantityDiff);
            updateItemQuantity(lastSelectedItem, quantity);
        }
        // Clear the quantity after updating
        currentQuantity.setLength(0);
    }

    private void clearQuantity() {
        currentQuantity.setLength(0);
    }

    private void updateItemQuantity(MenuItem item, int quantity) {
        BigDecimal priceBD = BigDecimal.valueOf(item.price).setScale(2, RoundingMode.HALF_UP);

        for (Map.Entry<MenuItem, Integer> existingItem : currentOrder.menuItems.entrySet()) {
            MenuItem prevItem = existingItem.getKey();

            if (prevItem.itemName.equals(item.itemName)) {
                currentOrder.addOrUpdateMenuItem(prevItem, quantity);

                orderItems.clear();
                orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
                orderTable.setItems(orderItems);

                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        currentOrder.addOrUpdateMenuItem(item, quantity);

        // Refresh what's displayed in the order table
        orderItems.clear();
        orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
        orderTable.setItems(orderItems);

        //currentOrder.addMenuItem(new MenuItem(itemName, price), quantity); // Add to current order
        updateTotals();
    }

    private void addItemToOrder(MenuItem item) {
        BigDecimal priceBD = BigDecimal.valueOf(item.price).setScale(2, RoundingMode.HALF_UP);

        for (Map.Entry<MenuItem, Integer> existingItem : currentOrder.menuItems.entrySet()) { // Updated to OrderItem
            MenuItem prevItem = existingItem.getKey();
            Integer quantity = existingItem.getValue();

            if (prevItem.itemName.equals(item.itemName)) {
                currentOrder.menuItems.replace(prevItem, quantity, quantity + 1);

                orderItems.clear();
                orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
                orderTable.setItems(orderItems);
                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        currentOrder.addOrUpdateMenuItem(item, 1);
        orderItems.clear();
        orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
        orderTable.setItems(orderItems);
        updateTotals();
    }

    @FXML
    private void clearTable() {
        // Put back inventory if the order is cleared
        for (MenuItem associatedInventory : currentOrder.menuItems.keySet()) {
            dbDriver.increaseMenuItemInventoryQuantity(
                    associatedInventory,
                    currentOrder.menuItems.get(associatedInventory)
            );
        }
        // Remove all items from the menuItems map in the current order
        currentOrder.menuItems.clear();

        // Clear the observable list to reflect the deletion in the UI
        orderItems.clear();
        orderTable.setItems(orderItems);

        // Reset tax and total fields to $0.00
        totalField.setText("Total: $0.00");

        // Reset the price in the current order
        currentOrder.price = 0.0;

        // Update totals to reflect the reset
        updateTotals();
    }

    private void addOrder() {
        dbDriver.insertOrder(currentOrder);

        // Log the current order's ID so we can easily query for it
        System.out.println("Newly placed order: " + currentOrder.orderId);

        dbSnapshot.refreshMenuSnapshot();

        resetCurrentOrder();
        orderItems.clear();
        updateTotals();

        // Reset the list of menu items
        menuItemGridPane.getChildren().clear();
        createMenuItemGrid();
    }

    private void resetCurrentOrder() {
        currentOrder = new Order(
                loggedInUser.employeeId,
                month,
                week,
                day,
                hour,
                0.0
        );

        // Let's add the "base" inventory items to every order
        Map<String, InventoryItem> inventorySnapshot = dbSnapshot.getInventorySnapshot();
        InventoryItem[] baseItems = {
                inventorySnapshot.get("Napkin"),
                inventorySnapshot.get("Utensil"),
                inventorySnapshot.get("Fortune Cookie"),
        };
        for (InventoryItem item : baseItems) {
            currentOrder.addOrUpdateInventoryItem(item, 1);
        }
    }

    private void updateTotals() {
        double subtotal = 0;
        for (Map.Entry<MenuItem, Integer> entry : currentOrder.menuItems.entrySet()) {
            MenuItem currentItem = entry.getKey();
            Integer quantity = entry.getValue();
            Double price = currentItem.price;

            subtotal += quantity * price;
        }

        BigDecimal subtotalBD = BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP);

        totalField.setText("Total: " + String.format("$%.2f", subtotalBD.doubleValue()));
    }
}
