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
import java.util.*;


/**
 * CashierController is responsible for managing the behavior and functionality of the cashier
 * interface in the POS system. It handles user interactions with the menu, manages the current
 * order, and updates the UI accordingly.
 *
 * <p>The controller uses a singleton database driver and snapshot to interact with the underlying
 * database and inventory system. It also manages the cashier's logged-in status and keeps track
 * of the items ordered, their quantities, and the total price.</p>
 *
 * @author Shreyan Satheesh
 * @author Soham Nagawanshi
 */
public class CashierController {

    private final DBDriverSingleton dbDriver = DBDriverSingleton.getInstance();
    private final DBSnapshotSingleton dbSnapshot = DBSnapshotSingleton.getInstance();

    private Employee loggedInUser;

    // New Order instance
    private Order currentOrder; // Added to manage the current order

    @FXML
    private TableView<Map.Entry<MenuItem, Integer>> orderTable; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, String> nameColumn; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, Integer> quantityColumn; // Updated type to OrderItem

    @FXML
    private TableColumn<Map.Entry<MenuItem, Integer>, Double> priceColumn; // Updated type to OrderItem

    @FXML
    private Button
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
            button9;

    @FXML
    private TextField cashierTextField;

    @FXML
    private TextField totalField;

    @FXML
    private GridPane menuItemGridPane;

    private ObservableList<Map.Entry<MenuItem, Integer>> orderItems;

    private MenuItem lastSelectedItem = null;

    private StringBuilder currentQuantity = new StringBuilder();

    /**
     * Logs out the current user and returns them to the login screen.
     *
     * @param event ActionEvent triggered by clicking the logout button.
     * @throws IOException if the login screen cannot be loaded.
     */
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

    /**
     * Initializes the cashier controller by refreshing snapshots, creating the menu item grid,
     * and initializing buttons and the order table.
     */
    @FXML
    public void initialize() {
        dbSnapshot.refreshAllSnapshots();

        resetCurrentOrder();

        createMenuItemGrid();
        initializeButtons();
        initializeTableView();
    }

    /**
     * Sets the logged-in user and displays their name in the cashier text field.
     *
     * @param user Employee object representing the logged-in user.
     */
    public void setLoggedInUser(Employee user) {
        loggedInUser = user;
        if (cashierTextField != null) {
            cashierTextField.setText("Cashier: " + loggedInUser.name);
        }
    }

    /**
     * Creates a grid layout of available menu items, displaying their name and image. Items
     * with insufficient inventory are grayed out and disabled.
     */
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

    /**
     * Initializes the order table by setting the columns for item name, quantity, and price.
     */
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

    /**
     * Initializes the numpad buttons, enter, clear, and place order buttons with their respective
     * action handlers.
     */
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

    /**
     * Attempts to add a menu item to the current order. If there is insufficient inventory,
     * an alert is shown.
     *
     * @param item The MenuItem to add to the order.
     * @return true if the item was added, false if inventory was insufficient.
     */
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

    /**
     * Shows an alert dialog with the specified title and message.
     *
     * @param title   The title of the alert dialog.
     * @param message The message to be displayed in the alert dialog.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Appends a number to the current quantity being entered for a menu item.
     *
     * @param num The number to append.
     */
    private void appendQuantity(int num) {
        currentQuantity.append(num);
    }

    /**
     * Updates the quantity of the last selected item in the order based on the entered quantity.
     */
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

    /**
     * Clears the quantity being entered for a menu item.
     */
    private void clearQuantity() {
        currentQuantity.setLength(0);
    }

    /**
     * Updates the quantity of a specific item in the order. If the item does not exist in the order,
     * it is added.
     *
     * @param item     The MenuItem to update.
     * @param quantity The new quantity of the item.
     */
    private void updateItemQuantity(MenuItem item, int quantity) {
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

    /**
     * Adds a new item to the current order or increases its quantity if already present.
     *
     * @param item The MenuItem to add to the order.
     */
    private void addItemToOrder(MenuItem item) {
        for (Map.Entry<MenuItem, Integer> existingItem : currentOrder.menuItems.entrySet()) {
            MenuItem prevItem = existingItem.getKey();
            Integer quantity = existingItem.getValue();

            if (prevItem.itemName.equals(item.itemName)) {
                currentOrder.addOrUpdateMenuItem(prevItem, quantity + 1);

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

    /**
     * Clears the current order, resetting inventory quantities and UI elements.
     */
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

    /**
     * Places the current order by inserting it into the database and resetting the order.
     */
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

    /**
     * Resets the current order by creating a new Order object and adding base inventory items
     */
    private void resetCurrentOrder() {

        // Add one because January = 0 in calendar
        int currentMonth = dbDriver.calendar.get(Calendar.MONTH) + 1;

        // Get the current hour in 12h format
        int currentHour = dbDriver.calendar.get(Calendar.HOUR);

        // 12 PM  is represented as 0
        currentHour = currentHour == 0 ? 12 : currentHour;

        // Workday starts at 10am and ends at 10pm
        currentHour = (currentHour - 10) % 12 + 1;

        // Return the positive modulus rather than negative
        if (currentHour < 0)
            currentHour += 12;

        currentOrder = new Order(
                loggedInUser.employeeId,
                currentMonth,
                dbDriver.calendar.get(Calendar.WEEK_OF_YEAR),
                dbDriver.calendar.get(Calendar.DAY_OF_MONTH),
                currentHour,
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

    /**
     * Updates the total price of the current order based on the items and their quantities.
     */
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
