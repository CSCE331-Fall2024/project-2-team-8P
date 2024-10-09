package org.example.pandaexpresspos.controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;

import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ManagerController {

    @FXML
    private GridPane inventoryItems;
    @FXML
    private GridPane menuItems;
    @FXML
    private GridPane employeeItems;
    @FXML
    private Button logoutButton;
    @FXML
    private Button addItemButton;
    @FXML
    private TabPane itemsTabPane;

    // Map inventory item name to [stock, image url]
    Map<String, String[]> inventoryList = new HashMap<>();
    Map<String, Object[]> menuList = new HashMap<>();
    Map<String, String[]> employeeList = new HashMap<>();

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


    // Arbitrary values for inventory items, menu items, and employees
    String[] itemNames = {
            "Napkins", "Silverware", "Orange Sauce", "Soy Sauce", "Prepackaged Noodles", "Beef", "Chicken"
    };

    String[] menuNames = {
            "Orange Chicken", "Chow Mein", "Fried Rice", "Beijing Beef", "Super Greens"
    };

    String[] employees = {
            "Pikachu", "Ash", "Charizard"
    };

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
                addInventoryItem();
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

    // Handle inventory items
    public void handleInventoryItemClicked(Button button, String name) {

        button.setOnMouseClicked(e -> {
            // Create a TextInputDialog to add to inventory
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Update Inventory");
            dialog.setHeaderText("Enter amount to add to inventory");
            dialog.setContentText("Amount:");

            // Show the dialog and capture the input
            Optional<String> result = dialog.showAndWait();

            // Only process if user provides value
            if (result.isPresent()) {
                String newInventoryText = result.get();
                try {
                    String inventoryStock = inventoryList.get(name)[0];
                    int currentStock = Integer.parseInt(inventoryStock);
                    int addedStock = Integer.parseInt(newInventoryText);

                    if (addedStock > 0) {
                        String newInventory = String.valueOf(currentStock + addedStock);
                        String inventoryImg = inventoryList.get(name)[1];
                        inventoryList.put(name, new String[]{newInventory, inventoryImg});
                        // redraw the grid
                        inventoryItems.getChildren().clear();
                        createInventoryGrid();

                    } else {
                        // Alert user for invalid input
                        showAlert("Invalid inventory amount", "Please enter a positive number.");
                    }
                } catch (NumberFormatException ex) {
                    // Alert user for invalid number format
                    showAlert("Invalid input", "Please enter a valid number.");
                }
            }

        });

    }

    public void addInventoryItem() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Inventory Item");
        dialog.setHeaderText("Add Inventory Item");

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
            // Add to existing list of inventory items
            inventoryList.put(name, new String[]{stock, url});
            // redraw the grid
            inventoryItems.getChildren().clear();
            createInventoryGrid();

        });
    }

    // Populate mock objects
    public void populateInventory() {
        try {
            String inventoryItemImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png").toExternalForm();
            String inventoryItemCount = "50";
            for (String name : itemNames) {
                // Store the stock amount and image in the map
                inventoryList.put(name, new String[]{"50", inventoryItemImg});
            }
        } catch (Exception e) {
            System.out.println("Image not found");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public void createInventoryGrid() {
        int columns = 5; // max columns per row
        int x = 0;
        int y = 0;

        inventoryItems.setHgap(10);
//        inventoryItems.setVgap(-30);
        inventoryItems.setAlignment(Pos.CENTER);

        for (String name : inventoryList.keySet()) {
            String itemStock = inventoryList.get(name)[0];
            String itemImg = inventoryList.get(name)[1];

            // Create a vertical box for image and label
            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            Button button = new Button();
            button.setMinSize(60, 60);
            button.setStyle("-fx-background-image: url('" + itemImg + "');" +
                            "-fx-background-size: cover;");
            handleInventoryItemClicked(button, name);
            Label nameLabel = new Label(name);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            Label itemStockLabel = new Label("Qty: " + itemStock);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            // Add items to vbox
            layout.getChildren().addAll(button, nameLabel, itemStockLabel);


            inventoryItems.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }

        }


    }




    public void populateMenuItems() {
        URL imageUrl = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png");
        if (imageUrl != null) {
            Image menuImage = new Image(imageUrl.toExternalForm());
            for (String name : menuNames) {
                // Store stock amount and image in map
                menuList.put(name, new Object[]{"2.50", menuImage});
            }
        } else {
            System.out.println("Image not found");
        }
    }

    public void createMenuItemsGrid() {
        int columns = 5; // Max number of columns in row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
//        inventoryItems.setVgap(30); // Vertical gap between rows
        menuItems.setHgap(10); // Horizontal gap between columns
        menuItems.setAlignment(Pos.CENTER);

        for (String name : menuList.keySet()) {
            ImageView menuImage = new ImageView((Image) menuList.get(name)[1]);
            String menuPrice = (String) menuList.get(name)[0];

            menuImage.setFitHeight(60);
            menuImage.setFitWidth(60);
            menuImage.setPreserveRatio(true);


            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            layout.setAlignment(Pos.CENTER);
            Label label = new Label(name + ": " + menuPrice);

            menuImage.setOnMouseClicked(e -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Update Menu Item");
                dialog.setHeaderText("Enter new price");
                dialog.setContentText("Price:");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    String newMenuText = result.get();
                    try {
                        double newPrice = Double.parseDouble(newMenuText);

                        if (newPrice > 0) {
                            label.setText(name + ": " + newPrice);
                            menuList.put(name, new Object[]{String.valueOf(newPrice), menuImage.getImage()});

                        } else {
                            // Alert user for invalid input
                            showAlert("Invalid price", "Please enter a positive number.");
                        }
                    } catch (NumberFormatException ex) {
                        // Alert user for invalid number format
                        showAlert("Invalid input", "Please enter a valid number.");
                    }
                }
            });

            layout.getChildren().addAll(menuImage, label);

            menuItems.add(layout, x, y);

            // Update grid position
            x++;
            if (x == columns) {
                x = 0;
                y++;
            }
        }
    }

    public void populateEmployees() {
        try {
            String employeeImg = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png").toExternalForm();
            String employeePosition = "Cashier";
            for (String name : employees) {
                // Store the stock amount and image in the map
                employeeList.put(name, new String[]{employeePosition, employeeImg});
            }
        } catch (Exception e) {
            System.out.println("Image not found");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void createEmployeesGrid() {
        int columns = 5; // Max number of columns in row
        int x = 0;
        int y = 0;
        System.out.println("hi");

        // Set gaps for GridPane
//        inventoryItems.setVgap(30); // Vertical gap between rows
        employeeItems.setHgap(10); // Horizontal gap between columns
        employeeItems.setAlignment(Pos.CENTER);

        for (String name : employeeList.keySet()) {
//            System.out.println(name);
            String employeeImage = employeeList.get(name)[1];
            String employeePosition = employeeList.get(name)[0];

            VBox layout = new VBox(10);
            layout.setAlignment(Pos.CENTER);
            Button button = new Button();
            button.setMinSize(60, 60);
            button.setStyle("-fx-background-image: url('" + employeeImage + "');" +
                    "-fx-background-size: cover;");

//            handleInventoryItemClicked(button, name);
            Label nameLabel = new Label(name);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            Label employeeLabel = new Label(name);
            Label employeePositionLabel = new Label(employeePosition);

            // Allow the VBox to grow in the GridPane cell
            layout.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Let it grow
            GridPane.setVgrow(layout, Priority.ALWAYS); // Let the VBox grow vertically
            GridPane.setHgrow(layout, Priority.ALWAYS); // Let the VBox grow horizontally

            layout.getChildren().addAll(button, employeeLabel, employeePositionLabel);

            employeeItems.add(layout, x, y);

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