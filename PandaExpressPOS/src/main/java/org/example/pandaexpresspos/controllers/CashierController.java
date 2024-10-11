package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
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
import org.example.pandaexpresspos.models.MenuItem;
import org.example.pandaexpresspos.models.Order; // Import Order class

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

public class CashierController {

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
    public Button clear, placeOrder, clearNum, Enter, button0, button1, button2, button3, button4, button5, button6, button7, button8, button9,Logout;

    @FXML
    private TextField taxField, totalField;
    @FXML
    private GridPane menuItemGridPane;

    private ObservableList<Map.Entry<MenuItem, Integer>> orderItems;

    // constants for global use
    private static final double TAX_RATE = 0.0825;
    private UUID cashierID = UUID.randomUUID();
    private LocalDate currentDate = LocalDate.now();
    private Integer month = currentDate.getMonthValue();
    private Integer week = (currentDate.getMonthValue()
                           * currentDate.getDayOfMonth())/7;
    private Integer day = currentDate.getDayOfMonth();
    private Integer hour = Calendar.HOUR_OF_DAY;


    private MenuItem lastSelectedItem = null;

    private StringBuilder currentQuantity = new StringBuilder();

    ArrayList<MenuItem> menuItems = new ArrayList<>();

    @FXML
    void logOutUser(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 800);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    // Constant sample image
    //String sampleImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/BeijingBeef.png").toExternalForm();

    @FXML
    public void initialize() {// Initialize current order
        currentOrder = new Order(
                cashierID,
                month,
                week,
                day,
                hour,
                0.0
        );
        populateMenuItems();
        createInventoryGrid();
        initializeButtons();
        initializeTableView();

    }

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
        menuItems.add(new MenuItem(11.99, 70, "Grilled Teriyaki Chicken"));
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

        menuItemGridPane.setHgap(100);
        menuItemGridPane.setAlignment(Pos.CENTER);
        menuItemGridPane.setStyle("-fx-padding: 10;");

        for (MenuItem item : menuItems) {
            String itemName = item.itemName;
            String itemImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/"+itemName+".png").toExternalForm();
            String itemStock = String.valueOf(item.availableStock);

            // Create a vertical box for image and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button, set background to img
            Button button = new Button();
            button.setMinSize(100, 80);
            button.setStyle("-fx-background-image: url('" + itemImg + "');" +
                    "-fx-background-size: cover;-fx-cursor: hand;");

            // Handle clicks
            button.setOnMouseClicked(e -> {
                selectItem(item);
            });

            // Create labels with styles
            Label nameLabel = new Label(itemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            nameLabel.setStyle("-fx-padding:5;-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333; -fx-background-color: white");

            Label itemStockLabel = new Label("Qty: " + itemStock);
            itemStockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;-fx-background-color: black;");

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

    private void initializeTableView() {
        nameColumn.setCellValueFactory( cellData->
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

    private void selectItem(MenuItem item) {
        lastSelectedItem = item;
        currentQuantity.setLength(0); // Clear the current quantity
        addItemToOrder(item); // Add one item immediately
    }

    private void appendQuantity(int num) {
        currentQuantity.append(num);
    }

    private void updateSelectedItemQuantity() {

        if (lastSelectedItem != null && !currentQuantity.isEmpty()) {
            int quantity = Integer.parseInt(currentQuantity.toString());
            updateItemQuantity(lastSelectedItem, quantity);
            currentQuantity.setLength(0); // Clear the quantity after updating
        }
    }

    private void clearQuantity() {
        currentQuantity.setLength(0);
    }

    private void updateItemQuantity(MenuItem item, int quantity) {
        BigDecimal priceBD = BigDecimal.valueOf(item.price).setScale(2, RoundingMode.HALF_UP);

        for (Map.Entry<MenuItem, Integer> existingItem : currentOrder.menuItems.entrySet()) {

            MenuItem prevItem = existingItem.getKey();

            if (prevItem.itemName.equals(item.itemName)) {
                currentOrder.menuItems.put(prevItem, quantity);
                orderItems.clear();
                orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
                orderTable.setItems(orderItems);
                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        currentOrder.menuItems.put(item, quantity);
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

                currentOrder.menuItems.replace(prevItem, quantity, quantity+1);
                orderItems.clear();
                orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
                orderTable.setItems(orderItems);
                updateTotals();
                return;
            }
        }

        // If the item does not exist, add a new item with the specified quantity
        currentOrder.menuItems.put(item, 1);
        orderItems.clear();
        orderItems = FXCollections.observableArrayList(currentOrder.menuItems.entrySet());
        orderTable.setItems(orderItems);
        updateTotals();
    }

    @FXML
    private void clearTable() {
        // Remove all items from the menuItems map in the current order
        currentOrder.menuItems.clear();

        // Clear the observable list to reflect the deletion in the UI
        orderItems.clear();
        orderTable.setItems(orderItems);

        // Reset tax and total fields to $0.00
        taxField.setText("Tax: $0.00");
        totalField.setText("Total: $0.00");

        // Reset the price in the current order
        currentOrder.price = 0.0;

        // Update totals to reflect the reset
        updateTotals();
    }

    private void addOrder() {
        // TODO: have to add order to the database
        // Add current order to the database logic here
        currentOrder = new Order(
                cashierID,
                month,
                week,
                day,
                hour,
                0.0
        ); // Reset current order
        orderItems.clear();
        updateTotals();
    }

    private void updateTotals() {
//        double subtotal = orderItems.stream().mapToDouble(OrderItem::getPrice).sum(); // Updated to OrderItem
        double subtotal = 0;
        for (Map.Entry<MenuItem, Integer> entry : currentOrder.menuItems.entrySet()) {
            MenuItem currentItem = entry.getKey();
            Integer quantity = entry.getValue();
            Double price = currentItem.price;

            subtotal += quantity * price;
        }

        BigDecimal subtotalBD = BigDecimal.valueOf(subtotal).setScale(2, RoundingMode.HALF_UP);

        double tax = subtotalBD.multiply(BigDecimal.valueOf(TAX_RATE)).doubleValue();
        BigDecimal taxBD = BigDecimal.valueOf(tax).setScale(2, RoundingMode.HALF_UP);

        double total = subtotalBD.add(taxBD).doubleValue();
        BigDecimal totalBD = BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP);

        taxField.setText("Tax: " + String.format("$%.2f", taxBD.doubleValue()));
        totalField.setText("Total: " + String.format("$%.2f", totalBD.doubleValue()));
    }
}
