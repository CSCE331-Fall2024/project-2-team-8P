package org.example.pandaexpresspos.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ManagerController {

    @FXML
    private GridPane inventoryItems;

    // Each inventory Item: Name, amount, Image
    Map<String, Object[]> inventoryList = new HashMap<>();

    String[] itemNames = {
            "Napkins", "Silverware", "Orange Sauce", "Soy Sauce", "Prepackaged Noodles"
    };

    @FXML
    public void initialize() {
        populateInventory();
        createInventoryGrid();
    }

    public void populateInventory() {
        // Load image from resources
        URL imageUrl = getClass().getResource("/org/example/pandaexpresspos/fxml/Images/sample_image.png");
        if (imageUrl != null) {
            Image inventoryImage = new Image(imageUrl.toExternalForm());
            for (String name : itemNames) {
                // Store the stock amount and image in the map
                inventoryList.put(name, new Object[]{"50", inventoryImage});
            }
        } else {
            System.out.println("Image not found");
        }
    }

    public void createInventoryGrid() {
        int columns = 6; // Max number of columns in row
        int x = 0;
        int y = 0;

        // Set gaps for GridPane
        inventoryItems.setVgap(10); // Vertical gap between rows
        inventoryItems.setHgap(10); // Horizontal gap between columns

        for (String name : inventoryList.keySet()) {
            ImageView inventoryImage = new ImageView((Image) inventoryList.get(name)[1]);
            String inventoryStock = (String) inventoryList.get(name)[0];

            inventoryImage.setFitHeight(60);
            inventoryImage.setFitWidth(60);
            inventoryImage.setPreserveRatio(true);

            VBox layout = new VBox(10);
            layout.setPadding(new Insets(10));
            Label label = new Label(name + ": " + inventoryStock);

            inventoryImage.setOnMouseClicked(e -> {
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
                        int currentStock = Integer.parseInt(inventoryStock);
                        int addedStock = Integer.parseInt(newInventoryText);

                        if (addedStock > 0) {
                            int newInventory = currentStock + addedStock;
                            label.setText(name + ": " + newInventory);
                            inventoryList.put(name, new Object[]{String.valueOf(newInventory), inventoryImage.getImage()});

                            // Change button color based on new inventory value
//                            if (newInventory <= 10) {
//                                changeInventoryButton.setStyle("-fx-background-color: red;");
//                            } else if (newInventory <= 25) {
//                                changeInventoryButton.setStyle("-fx-background-color: yellow;");
//                            } else {
//                                changeInventoryButton.setStyle("-fx-background-color: green;");
//                            }
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

            layout.getChildren().addAll(inventoryImage, label);

            inventoryItems.add(layout, x, y);

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