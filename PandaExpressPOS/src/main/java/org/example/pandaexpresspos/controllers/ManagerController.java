package org.example.pandaexpresspos.controllers;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import javafx.event.ActionEvent;
import org.example.pandaexpresspos.models.Employee;
import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;
import java.io.IOException;
import java.util.HashMap;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Optional;

public class ManagerController {

    @FXML
    private GridPane inventoryItemsGridPane;
    @FXML
    private GridPane menuItemsGridPane;
    @FXML
    private GridPane employeeItemsGridPane;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addItemButton;
    @FXML
    private TabPane itemsTabPane;

    // Global Constant for Images
    String sampleImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png").toExternalForm();

    // Enum to check which tab user has selected
    enum Tab {
        INVENTORYITEMS(0),
        MENUITEMS(1),
        EMPLOYEES(2);

        private final int value;

        // Constructor
        Tab(int value) {
            this.value = value;
        }

        // Method to get the value
        public int getValue() {
            return value;
        }

        // Static method to convert an integer to an enum value
        public static Tab fromValue(int value) {
            for (Tab tab : Tab.values()) {
                if (tab.getValue() == value) {
                    return tab;
                }
            }
            throw new IllegalArgumentException("No tab found with value: " + value);
        }
    }


    // Data structure for inventoryItems, menuItems, employees

    ArrayList<InventoryItem> inventoryItems = new ArrayList<>();
    ArrayList<MenuItem> menuItems = new ArrayList<>();
    ArrayList<Employee> employees = new ArrayList<>();

    @FXML
    public void initialize() {
        populateInventory();
        populateMenuItems();
        populateEmployees();
        createInventoryGrid();
        createMenuItemsGrid();
        createEmployeesGrid();
    }

    // Handle logout button click
    public void logout(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        // Create a new scene and set it to the stage
        Scene scene = new Scene(loader.load(), 1200, 800);
        Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    // Handle adding items
    public void addItem() throws RuntimeException {
        // check if inventory items, menuitems, or employees is selected
        Tab selectedTab = Tab.fromValue(itemsTabPane.getSelectionModel().getSelectedIndex());
        TextInputDialog dialog = new TextInputDialog();
        switch (selectedTab) {
            case INVENTORYITEMS:
                updateInventoryItems(Optional.empty());
                break;
            case MENUITEMS:
//                addMenuItem();
                dialog.show();
                break;
            case EMPLOYEES:
//                addEmployee();
                break;
            default:
                throw new RuntimeException();

        }
    }

    public void updateInventoryItems(Optional<InventoryItem> inventoryItem) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout
        VBox inputs = new VBox();
        Label itemNameLabel = new Label("Item Name: ");
        TextField itemName = new TextField();

        Label itemCostLabel = new Label("Item Cost: ");
        TextField itemCost = new TextField();

        Label availableStockLabel = new Label("Available Stock: ");
        TextField availableStock = new TextField();

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If name is non-empty, we are in update mode
        inventoryItem.ifPresent(safeItem -> {
            itemName.setText(safeItem.itemName);
            itemCost.setText(String.valueOf(safeItem.cost));
            availableStock.setText(String.valueOf(safeItem.availableStock));
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");

        });

        dialog.setTitle(dialogLabelName + " Inventory Item");
        dialog.setHeaderText(dialogLabelName + " Inventory Item");


        // Add to field
        inputs.getChildren().addAll(itemNameLabel, itemName, itemCostLabel, itemCost, availableStockLabel, availableStock, imageUrlLabel, imageUrl);
        dialog.getDialogPane().setContent(inputs);

        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                //TODO: add validation
                return new ButtonType(
                        itemName.getText() + "," +
                           itemCost.getText() + ", " +
                           availableStock.getText() + ", " +
                           imageUrl.getText()
                );
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(outputFields -> {
            String[] outputs = outputFields.getText().split(",");
            String name = outputs[0];
            String cost = outputs[1];
            String stock = outputs[2];
            String url = outputs[3];

            // Remove the previous item if we are updating, in case name changed
            inventoryItem.ifPresent(safeItem->{
                inventoryItems.removeIf(item -> (
                        item.itemName.equals(safeItem.itemName)
                ));
                inventoryItems.add(new InventoryItem(safeItem.inventoryItemId, Double.parseDouble(cost.trim()),
                        Integer.parseInt(stock.trim()), name));
                inventoryItemsGridPane.getChildren().clear();
            });

            // If we need to add a new item, no inventory item will be passed in
            if (inventoryItem.isEmpty()) {
                inventoryItems.add(new InventoryItem(Double.parseDouble(cost.trim()),
                        Integer.parseInt(stock.trim()), name));
            }

            // redraw the grid to reflect the updates
            inventoryItemsGridPane.getChildren().clear();
            createInventoryGrid();

        });
    }

    //TODO: Retrieve Inventory Items from Database
    public void populateInventory() {
        inventoryItems.add(new InventoryItem(5.99, 100, "Napkins"));
        inventoryItems.add(new InventoryItem(9.99, 50, "Silverware"));
        inventoryItems.add(new InventoryItem(3.99, 200, "Orange Sauce"));
        inventoryItems.add(new InventoryItem(2.99, 150, "Soy Sauce"));
        inventoryItems.add(new InventoryItem(1.49, 300, "Prepackaged Noodles"));
        inventoryItems.add(new InventoryItem(7.99, 80, "Beef"));
        inventoryItems.add(new InventoryItem(6.99, 120, "Chicken"));
    }

    //TODO: Retrieve Employees from Database
    public void populateEmployees() {
        employees.add(new Employee(true, "Ash"));
        employees.add(new Employee(true, "Brock"));
        employees.add(new Employee(false, "Pikachu"));
        employees.add(new Employee(false, "Charmander"));
        employees.add(new Employee(false, "Bulbasaur"));
        employees.add(new Employee(false, "Squirtle"));
        employees.add(new Employee(false, "Jigglypuff"));
        employees.add(new Employee(false, "Meowth"));
        employees.add(new Employee(false, "Psyduck"));
    }

    //TODO: Retrieve Menu Items from Database
    public void populateMenuItems() {
        menuItems.add(new MenuItem(6.99, 100, "Orange Chicken"));
        menuItems.add(new MenuItem(5.49, 120, "Chow Mein"));
        menuItems.add(new MenuItem(4.99, 150, "Fried Rice"));
        menuItems.add(new MenuItem(7.99, 80, "Beijing Beef"));
        menuItems.add(new MenuItem(5.99, 90, "Super Greens"));
    }


    public void createInventoryGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        inventoryItemsGridPane.setHgap(10);
        inventoryItemsGridPane.setAlignment(Pos.CENTER);

        for (InventoryItem item : inventoryItems) {

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

            // Handle clicks
            button.setOnMouseClicked(e ->{
                this.updateInventoryItems(Optional.of(item));
            });

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
            inventoryItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }

        }


    }

    public void createMenuItemsGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
        menuItemsGridPane.setHgap(10); // Horizontal gap between columns
        menuItemsGridPane.setAlignment(Pos.CENTER);

        for (MenuItem menuItem : menuItems) {

            String menuItemName = menuItem.itemName;
            String menuItemStock = String.valueOf(menuItem.availableStock);
            String menuItemImg = sampleImg;

            // Create a vertical box for button and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button; set background
            Button button = new Button();
            button.setMinSize(60, 60);
            button.setStyle("-fx-background-image: url('" + menuItemImg + "');" +
                    "-fx-background-size: cover;");

            // Create a quantity label
            Label nameLabel = new Label(menuItemName);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            Label menuStockLabel = new Label("Qty: " + menuItemStock);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, nameLabel, menuStockLabel);

            // Add Vbox to grid
            menuItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }

        }


    }

    public void createEmployeesGrid() {
        int columns = 5; // Max number of columns in row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
        employeeItemsGridPane.setHgap(10); // Horizontal gap between columns
        employeeItemsGridPane.setAlignment(Pos.CENTER);

        for (Employee employee : employees) {
            String employeeName = employee.name;
            String employeeImage = sampleImg;
            String employeePosition = employee.isManager ? "Manager" : "Cashier";

            // Create a vbox to store items
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);

            // Create a button; set background
            Button button = new Button();
            button.setMinSize(60, 60);
            button.setStyle("-fx-background-image: url('" + employeeImage + "');" +
                    "-fx-background-size: cover;");

            // Create name and position labels
            Label employeeNameLabel = new Label(employeeName);
            Label employeePositionLabel = new Label(employeePosition);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, employeeNameLabel, employeePositionLabel);

            // Add vbox to grid pane
            employeeItemsGridPane.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }
        }
    }


    // Helper method to display error alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}