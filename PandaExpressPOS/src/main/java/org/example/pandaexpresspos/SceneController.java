package org.example.pandaexpresspos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SceneController {

    public HBox buttonContainer;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Rectangle2D screen;

    private HashMap<Button, ArrayList<String>> menuItems;


    public void deleteMenuItems(ActionEvent event) throws IOException {
        // Load the layout from FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/deleteMenuItems.fxml"));
        root = loader.load();

        // Get the HBox from the FXML using its fx:id
        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        menuItems = DataStore.getInstance().getMenuItems();

        for (Button button : new ArrayList<>(menuItems.keySet())) {
            button.setOnAction(e -> {
                buttonContainer.getChildren().remove(button);
                menuItems.remove(button);


                DataStore.getInstance().setMenuItems(menuItems); // Update HashMap
                DataStore.getInstance().setNumItems(menuItems.size()); // Update numItems
            });

            buttonContainer.getChildren().add(button);
        }

        setUpScene(event, root);
    }

    public void addMenuItem(){
        ArrayList<String> newItem = new ArrayList<>();
        newItem.add(""); // Placeholders
        newItem.add("");

        if (buttonContainer == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
            buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        }

        ChangeMenuItemInfo(newItem, () -> {
            // This block will execute after the user enters the name and presses "Change Name"
            if (!newItem.getFirst().isEmpty()) {
                // Add new button to the container and MenuItems map
                Button newButton = new Button(newItem.getFirst());  // Set button's label to entered name
                menuItems = DataStore.getInstance().getMenuItems();

                // Add the new button and associate it with the newItem list in the HashMap
                menuItems.put(newButton, newItem);
                buttonContainer.getChildren().add(newButton);

                // Update DataStore to reflect the changes
                DataStore.getInstance().setMenuItems(menuItems);  // Update the items map in DataStore
                DataStore.getInstance().setNumItems(menuItems.size());  // Update the number of items

                // Debugging statements
                System.out.println("Added new button: " + newButton.getText());
                System.out.println("Number of items: " + DataStore.getInstance().getNumItems());
            } else {
                System.out.println("Failed to add new button: No name provided.");
            }
        });
    }



    public void MenuItems(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
        root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get the items from DataStore
        menuItems = DataStore.getInstance().getMenuItems();

        // Iterate over each button in the HashMap
        for (Button button : menuItems.keySet()) {
            button.setOnAction(e -> {
                // Retrieve information of button clicked
                ArrayList<String> itemInfo = menuItems.get(button);

                // Create popup to update information
                if (itemInfo != null) {
                    ChangeMenuItemInfo(itemInfo, () -> {
                        System.out.println("Popup closed and data possibly updated.");
                    });
                } else {
                    System.out.println("Error: No information found for button " + button.getText());
                }
            });

            buttonContainer.getChildren().add(button);
        }

        setUpScene(event, root);
    }

    private void setUpScene(ActionEvent event, Parent root) {
        screen = Screen.getPrimary().getVisualBounds();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }

    public void ManagerStartingPage(ActionEvent event) throws IOException {
        root = FXMLLoader.load(getClass().getResource("fxml/ManagerStartingPage.fxml"));
        screen = Screen.getPrimary().getVisualBounds();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }

    public void Inventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Inventory.fxml"));
        root = loader.load();

        // Get the HBox container from the FXML file
        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get the inventory items from DataStore
        HashMap<Button, Integer> inventoryItems = DataStore.getInstance().getInventoryItems();

        // Iterate over each button in the HashMap
        for (Button button : inventoryItems.keySet()) {

            button.setOnAction(e -> {
                // Retrieve the corresponding inventory value for the clicked button
                Integer inventoryInfo = DataStore.getInstance().getInventoryForItem(button);

                // Call the ChangeInventoryItemInfo method and pass the inventoryWrapper along with a callback (Runnable)
                if (inventoryInfo != null) {
                    ChangeInventoryItemInfo(DataStore.getInstance(), button, () -> {
                        // Callback to handle any post-popup logic, such as updating the UI
                        System.out.println("Popup closed and inventory updated.");
                    });
                } else {
                    System.out.println("Error: No inventory data found for button " + button.getText());
                }
            });

            buttonContainer.getChildren().add(button);
        }

        setUpScene(event, root);
    }




    public void ChangeMenuItemInfo(ArrayList<String> itemInfo, Runnable onComplete) {
        VBox layout = new VBox(10);  // VBox layout with 10px spacing

        Label label = new Label("Item Information:");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(itemInfo);

        // Create a TextField for the user to enter a new name
        TextField nameField = new TextField();
        nameField.setPromptText("Enter new name");

        // Create a TextField for the user to enter a new price (or any other attribute)
        TextField priceField = new TextField();
        priceField.setPromptText("Enter new price");

        // Create buttons for further actions (e.g., changing values)
        Button changeNameButton = new Button("Change Name");
        Button changePriceButton = new Button("Change Price");

        changeNameButton.setOnAction(e -> {
            String newName = nameField.getText();
            if (!newName.isEmpty()) {
                itemInfo.set(0, newName);
                listView.getItems().clear();
                listView.getItems().addAll(itemInfo);


            } else {
                System.out.println("Name cannot be empty.");
            }
        });

        changePriceButton.setOnAction(e -> {
            String newPrice = priceField.getText();
            if (!newPrice.isEmpty() && Double.parseDouble(newPrice) > 0) {
                itemInfo.set(1, newPrice);
                listView.getItems().clear();
                listView.getItems().addAll(itemInfo);
                if (onComplete != null) { // Adds button to Menu Items
                    onComplete.run();
                }
            } else {
                System.out.println("Price cannot be empty.");
            }
        });

        layout.getChildren().addAll(label, listView, nameField, changeNameButton, priceField, changePriceButton);

        Stage stage = new Stage();
        stage.setWidth(300);
        stage.setHeight(300);
        stage.setX(500);
        stage.setY(500);

        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();

    }

    public void ChangeInventoryItemInfo(DataStore inventoryWrapper, Button button, Runnable onComplete) {
        VBox layout = new VBox(10);

        Label label = new Label("Inventory Information:");

        ListView<Integer> listView = new ListView<>();
        listView.getItems().add(inventoryWrapper.getInventoryForItem(button));

        TextField inventoryField = new TextField();
        inventoryField.setPromptText("Enter new inventory amount");

        Button changeInventoryButton = new Button("Add to Inventory");

        changeInventoryButton.setOnAction(e -> {
            String newInventoryText = inventoryField.getText();
            if (!newInventoryText.isEmpty() && Integer.parseInt(newInventoryText) > 0) {
                try {
                    Integer newInventory = Integer.parseInt(newInventoryText) + DataStore.getInstance().getInventoryForItem(button);
                    listView.getItems().clear();
                    listView.getItems().add(newInventory);
                    inventoryWrapper.setInventoryForItem(button, newInventory);

                    onComplete.run();
                } catch (NumberFormatException ex) {
                    System.out.println("Invalid inventory amount. Please enter a valid number.");
                }
            } else {
                System.out.println("Invalid inventory amount. Please enter a valid number.");
            }
        });

        layout.getChildren().addAll(label, listView, inventoryField, changeInventoryButton);

        Stage stage = new Stage();
        stage.setWidth(300);
        stage.setHeight(300);
        stage.setX(500);
        stage.setY(500);

        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();

    }
}



