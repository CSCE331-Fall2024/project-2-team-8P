package org.example.pandaexpresspos.controllers;
import javafx.application.Platform;
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
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.database.DBSnapshotSingleton;
import org.example.pandaexpresspos.models.Employee;
import org.example.pandaexpresspos.models.InventoryItem;
import org.example.pandaexpresspos.models.MenuItem;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.Optional;

public class ManagerController {

    // Database logic
    private final DBDriverSingleton dbDriver = DBDriverSingleton.getInstance();
    private final DBSnapshotSingleton dbSnapshot = DBSnapshotSingleton.getInstance();

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
    private final String sampleImg = Objects.requireNonNull(getClass()
            .getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png"))
            .toExternalForm();

    // Enum to check which tab user has selected
    enum Tab {
        INVENTORY_ITEMS(0),
        MENU_ITEMS(1),
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

    // Initialize the state of the UI after FXML elements are injected
    @FXML
    public void initialize() {
        dbSnapshot.refreshAllSnapshots();

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

    // Handle adding items; special case of update item
    public void addItem() throws RuntimeException {
        // check if inventory items, menuitems, or employees is selected
        Tab selectedTab = Tab.fromValue(itemsTabPane.getSelectionModel().getSelectedIndex());


        switch (selectedTab) {
            case INVENTORY_ITEMS:
                addOrUpdateInventoryItem(Optional.empty());
                break;
            case MENU_ITEMS:
                updateMenuItem(Optional.empty());
                break;
            case EMPLOYEES:
                updateEmployee(Optional.empty());
                break;
            default:
                throw new RuntimeException();

        }
    }

    // Use this to update or add new inventory items; if null inventory item is passed in, add new item
    public void addOrUpdateInventoryItem(Optional<InventoryItem> inventoryItem) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox();

        Label inventoryItemNameLabel = new Label("Item Name: ");
        TextField inventoryItemName = new TextField();

        Label inventoryItemCostLabel = new Label("Inventory Item Cost: ");
        TextField inventoryItemCost = new TextField();

        Label availableStockLabel = new Label("Available Stock: ");
        TextField availableStock = new TextField();

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If name is non-empty, we are in update mode
        inventoryItem.ifPresent(safeItem -> {
            inventoryItemName.setText(safeItem.itemName);
            inventoryItemCost.setText(String.valueOf(safeItem.cost));
            availableStock.setText(String.valueOf(safeItem.availableStock));
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");
        });

        dialog.setTitle(dialogLabelName + " Inventory Item");
        dialog.setHeaderText(dialogLabelName + " Inventory Item");


        // Add to field
        inputsContainer.getChildren().addAll(
                inventoryItemNameLabel,
                inventoryItemName,
                inventoryItemCostLabel,
                inventoryItemCost,
                availableStockLabel,
                availableStock,
                imageUrlLabel,
                imageUrl
        );

        // If in update mode add a remove button and handle appropriately
//        inventoryItem.ifPresent(safeItem -> {
//            Label removeLabel = new Label("Remove Item: ");
//            Button removeButton = new Button("Remove");
//
//            // Handle button click
//            removeButton.setOnMouseClicked(e -> {
////                inventoryItems.removeIf(item -> (
////                        item.itemName.equals(safeItem.itemName)
////                ));
//                // Backend call here
//
//
//                // Added for thread safety
//                Platform.runLater(() -> {
//                    // Repopulate the grid
//                    inventoryItemsGridPane.getChildren().clear();
//                    createInventoryGrid();
//                });
//
//                dialog.close();
//            });
//
//            // Add to view hierarcy
//            inputsContainer.getChildren().addAll(removeLabel, removeButton);
//
//        });


        dialog.getDialogPane().setContent(inputsContainer);

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                //TODO: add validation
                return new ButtonType(
                        inventoryItemName.getText() + "," +
                           inventoryItemCost.getText() + ", " +
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

//            // Remove the previous item if we are updating
//            inventoryItem.ifPresent(safeItem->{
//                inventoryItems.removeIf(item -> (
//                        item.itemName.equals(safeItem.itemName)
//                ));
//                inventoryItems.add(new InventoryItem(
//                        safeItem.inventoryItemId,
//                        Double.parseDouble(cost.trim()),
//                        Integer.parseInt(stock.trim()),
//                        name
//                ));
//            });

            // If no inventory item is passed in, we need to add a new one
            if (inventoryItem.isEmpty()) {
                inventoryItems.add(new InventoryItem(
                        Double.parseDouble(cost.trim()),
                        Integer.parseInt(stock.trim()),
                        name
                ));
            }

            // redraw the grid to reflect the updates
            inventoryItemsGridPane.getChildren().clear();
            createInventoryGrid();

        });
    }

    // Use this to update or add new menu items; if null menu item is passed in, add new item
    public void updateMenuItem(Optional<MenuItem> menuItem) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox();

        Label menuItemNameLabel = new Label("Menu Item Name: ");
        TextField menuItemName = new TextField();

        Label menuItemPriceLabel = new Label("Menu Item Cost: ");
        TextField menuItemPrice = new TextField();

        Label availableStockLabel = new Label("Available Stock: ");
        TextField availableStock = new TextField();

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If non-empty menu item, populate text fields with item properties
        menuItem.ifPresent(safeItem -> {
            menuItemName.setText(safeItem.itemName);
            menuItemPrice.setText(String.valueOf(safeItem.price));
            availableStock.setText(String.valueOf(safeItem.availableStock));
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");
        });

        // Set the header and dialog to either Update or Add Menu Item
        dialog.setTitle(dialogLabelName + " Menu Item");
        dialog.setHeaderText(dialogLabelName + " Menu Item");

        // Add to dialog box
        inputsContainer.getChildren().addAll(
                menuItemNameLabel,
                menuItemName,
                menuItemPriceLabel,
                menuItemPrice,
                availableStockLabel,
                availableStock,
                imageUrlLabel,
                imageUrl
        );

        // If in update mode add a remove button and handle appropriately
//        menuItem.ifPresent(safeItem -> {
//            Label removeLabel = new Label("Remove Item: ");
////            Button removeButton = new Button("Remove");
//
//            // Handle button click
//            removeButton.setOnMouseClicked(e -> {
//                menuItems.removeIf(item -> (
//                        item.itemName.equals(safeItem.itemName)
//                ));
//
//                // Added for thread safety
//                Platform.runLater(() -> {
//                    // Repopulate the grid
//                    menuItemsGridPane.getChildren().clear();
//                    createMenuItemsGrid();
//                });
//
//                dialog.close();
//            });
//
//            // Add to view hierarcy
//            inputsContainer.getChildren().addAll(removeLabel, removeButton);
//
//        });

        dialog.getDialogPane().setContent(inputsContainer);

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                //TODO: add validation
                return new ButtonType(
                        menuItemName.getText() + "," +
                                menuItemPrice.getText() + ", " +
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
            String price = outputs[1];
            String stock = outputs[2];
            String url = outputs[3];

            // remove the previous item if we are updating
            menuItem.ifPresent(safeItem -> {
                menuItems.removeIf(item -> (
                        item.itemName.equals(safeItem.itemName)
                ));
                menuItems.add(new MenuItem(
                        safeItem.menuItemId,
                        Double.parseDouble(price.trim()),
                        Integer.parseInt(stock.trim()),
                        name
                ));
            });

            // If no menu item is passed in, we need to add a new one
            if (menuItem.isEmpty()) {
                menuItems.add(new MenuItem(
                        Double.parseDouble(price.trim()),
                        Integer.parseInt(stock.trim()),
                        name
                ));
            }

            // redraw the grid to reflect the updates
            menuItemsGridPane.getChildren().clear();
            createMenuItemsGrid();
        });



    }

    // Use this to update or add employees; if null employee is passed in, add new employee
    public void updateEmployee(Optional<Employee> employee) {
        Dialog<ButtonType> dialog = new Dialog<>();

        // Create the layout to add to dialog
        VBox inputsContainer = new VBox();

        Label employeeNameLabel = new Label("Employee Name: ");
        TextField employeeName = new TextField();

        Label isManagerLabel = new Label("Manager: ");
        ToggleButton isManagerButton = new ToggleButton("NO");
        isManagerButton.setSelected(false);

        // Style button appropriately
        isManagerButton.setOnAction(e -> {
            if (isManagerButton.isSelected()) {
                isManagerButton.setText("YES");
            } else {
                isManagerButton.setText("NO");
            }
        });

        Label imageUrlLabel = new Label("Image Url: ");
        TextField imageUrl = new TextField();

        AtomicReference<String> dialogLabelName = new AtomicReference<>("Add");

        // If non-empty menu item, populate text fields with employee properties
        employee.ifPresent(safeEmployee -> {
            employeeName.setText(safeEmployee.name);
            isManagerButton.setSelected(safeEmployee.isManager);
            if (safeEmployee.isManager) {
                isManagerButton.setText("YES");
            } else {
                isManagerButton.setText("NO");
            }
            imageUrl.setText(sampleImg);
            dialogLabelName.set("Update");
        });

        // Set the header and dialog to either Update or Add Employee
        dialog.setTitle(dialogLabelName + " Employee");
        dialog.setHeaderText(dialogLabelName + " Employee");

        // Add to dialog box
        inputsContainer.getChildren().addAll(
                employeeNameLabel,
                employeeName,
                isManagerLabel,
                isManagerButton,
                imageUrlLabel,
                imageUrl
        );

        // If in update mode add a remove button and handle appropriately
        employee.ifPresent(safeEmployee -> {
            Label removeLabel = new Label("Remove Item: ");
            Button removeButton = new Button("Remove");

            // Handle button click
            removeButton.setOnMouseClicked(e -> {
                employees.removeIf(person -> (
                        person.name.equals(safeEmployee.name)
                ));

                // Added for thread safety
                Platform.runLater(() -> {
                    // Repopulate the grid
                    employeeItemsGridPane.getChildren().clear();
                    createEmployeesGrid();
                });

                dialog.close();
            });

            // Add to view hierarcy
            inputsContainer.getChildren().addAll(removeLabel, removeButton);

        });

        dialog.getDialogPane().setContent(inputsContainer);

        // Add buttons to dialog pane
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Handle the result
        dialog.setResultConverter(dialogButton -> {
            // TODO: add validation
            if (dialogButton == ButtonType.OK) {
                return new ButtonType(
                        employeeName.getText() + "," +
                                isManagerButton.isSelected() + ',' +
                                imageUrl.getText()
                );
            }
            return null;
        });

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(outputFields -> {
            String[] outputs = outputFields.getText().split(",");
            String name = outputs[0];
            String isManager = outputs[1];
            String url = outputs[2];

        // remove the previous employee if we are updating
        employee.ifPresent( safeEmployee -> {
            employees.removeIf(person ->(
                person.name.equals(safeEmployee.name)
            ));
            employees.add(new Employee(
                    safeEmployee.employeeId,
                    Boolean.parseBoolean(isManager.trim()),
                    name
            ));
        });

        // If no employee is passed in, we need to create a new one
        if (employee.isEmpty()) {
            employees.add(new Employee(
                    Boolean.parseBoolean(isManager.trim()),
                    name
            ));
        }

        // redraw the grid to reflect the updates
        employeeItemsGridPane.getChildren().clear();
        createEmployeesGrid();
        });

    }

    // TODO: Retrieve Inventory Items from Database
//    public void populateInventory() {
//        inventoryItems.add(new InventoryItem(5.99, 100, "Napkins"));
//        inventoryItems.add(new InventoryItem(9.99, 50, "Silverware"));
//        inventoryItems.add(new InventoryItem(3.99, 200, "Orange Sauce"));
//        inventoryItems.add(new InventoryItem(2.99, 150, "Soy Sauce"));
//        inventoryItems.add(new InventoryItem(1.49, 300, "Prepackaged Noodles"));
//        inventoryItems.add(new InventoryItem(7.99, 80, "Beef"));
//        inventoryItems.add(new InventoryItem(6.99, 120, "Chicken"));
//    }

    // TODO: Retrieve Employees from Database
//    public void populateEmployees() {
//        employees.add(new Employee(true, "Ash"));
//        employees.add(new Employee(true, "Brock"));
//        employees.add(new Employee(false, "Pikachu"));
//        employees.add(new Employee(false, "Charmander"));
//        employees.add(new Employee(false, "Bulbasaur"));
//        employees.add(new Employee(false, "Squirtle"));
//        employees.add(new Employee(false, "Jigglypuff"));
//        employees.add(new Employee(false, "Meowth"));
//        employees.add(new Employee(false, "Psyduck"));
//    }

    // TODO: Retrieve Menu Items from Database
//    public void populateMenuItems() {
//        menuItems.add(new MenuItem(6.99, 100, "Orange Chicken"));
//        menuItems.add(new MenuItem(5.49, 120, "Chow Mein"));
//        menuItems.add(new MenuItem(4.99, 150, "Fried Rice"));
//        menuItems.add(new MenuItem(7.99, 80, "Beijing Beef"));
//        menuItems.add(new MenuItem(5.99, 90, "Super Greens"));
//    }


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
                this.addOrUpdateInventoryItem(Optional.of(item));
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

            // Handle the button click
            button.setOnMouseClicked(e -> {
                updateMenuItem(Optional.of(menuItem));
            });

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

            // Handle the button click
            button.setOnMouseClicked(e -> {
                updateEmployee(Optional.of(employee));
            });

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