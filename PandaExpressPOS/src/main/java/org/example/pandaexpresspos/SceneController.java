package org.example.pandaexpresspos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

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

    HashMap<Button, ArrayList<String>> items = new HashMap<>();  // Declare the HashMap


    public void deleteButtons(ActionEvent event) throws IOException {
        // Load the layout from FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/deleteMenuItems.fxml"));
        Parent root = loader.load();

        // Get the HBox from the FXML using its fx:id
        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        HashMap<Button, ArrayList<String>> items = DataStore.getInstance().getMenuItems();
        // Get the buttons from the HashMap instead of DataStore
        for (Button button : new ArrayList<>(items.keySet())) { // Create a new ArrayList
            button.setOnAction(e -> {
                buttonContainer.getChildren().remove(button); // Remove button from the UI
                items.remove(button); // Remove button from the HashMap

                // Update DataStore
                DataStore.getInstance().setMenuItems(items); // Update HashMap
                DataStore.getInstance().setNumItems(items.size()); // Update numItems
            });

            buttonContainer.getChildren().add(button); // Add button to the UI
        }

        setUpScene(event, root);
    }

    public void addButton(ActionEvent event) throws IOException {
        ArrayList<String> newItem = new ArrayList<>();
        newItem.add("");  // Placeholder for the name (index 0)
        newItem.add("");  // Placeholder for the price (index 1)

        if (buttonContainer == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
            Parent root = loader.load();
            buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        }

        popup(newItem, () -> {
            // This block will execute after the user enters the name and presses "Change Name"
            if (!newItem.get(0).isEmpty()) {
                // Add new button to the container and items map
                Button newButton = new Button(newItem.get(0));  // Set the button's label to the entered name
                HashMap<Button, ArrayList<String>> items = DataStore.getInstance().getMenuItems();

                // Add the new button and associate it with the newItem list in the HashMap
                items.put(newButton, newItem);
                buttonContainer.getChildren().add(newButton);

                // Update DataStore to reflect the changes
                DataStore.getInstance().setMenuItems(items);  // Update the items map in DataStore
                DataStore.getInstance().setNumItems(items.size());  // Update the number of items

                // Debugging output to confirm the new button was added
                System.out.println("Added new button: " + newButton.getText());
                System.out.println("Number of items: " + DataStore.getInstance().getNumItems());
            } else {
                System.out.println("Failed to add new button: No name provided.");
            }
        });
    }



    public void switchToScene1(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
        Parent root = loader.load();

        // Get the HBox container from the FXML file
        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get the items from DataStore
        HashMap<Button, ArrayList<String>> items = DataStore.getInstance().getMenuItems();

        // Iterate over each button in the HashMap
        for (Button button : items.keySet()) {
            button.setOnAction(e -> {
                // Retrieve the corresponding ArrayList of strings for the clicked button
                ArrayList<String> itemInfo = items.get(button);

                // Call the popup2 method and pass the itemInfo along with a callback (Runnable)
                if (itemInfo != null) {
                    popup(itemInfo, () -> {
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
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/ManagerStartingPage.fxml"));
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }

    public void switchToScene3(ActionEvent event) throws IOException {
        DataStore.getInstance().setNumItems(5);
        Parent root = FXMLLoader.load(getClass().getResource("fxml/Inventory.fxml"));

        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);

        stage.setScene(scene);
        stage.setX(0);
        stage.setY(0);
        stage.setWidth(screen.getWidth());
        stage.setHeight(screen.getHeight());
        stage.show();
    }

    public Stage popup(ArrayList<String> itemInfo, Runnable onComplete) {
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

        // Handle changing name
        changeNameButton.setOnAction(e -> {
            String newName = nameField.getText();
            if (!newName.isEmpty()) {
                itemInfo.set(0, newName);  // Modify the first element in the list
                listView.getItems().clear();
                listView.getItems().addAll(itemInfo);  // Update ListView after modification

                // Call the onComplete callback after setting the new name
                if (onComplete != null) {
                    onComplete.run();  // This will trigger the callback to finish the task
                }
            } else {
                System.out.println("Name cannot be empty.");
            }
        });

        // Handle changing price (or any other attribute)
        changePriceButton.setOnAction(e -> {
            String newPrice = priceField.getText();
            if (!newPrice.isEmpty()) {
                itemInfo.set(1, newPrice);  // Modify the second element in the list
                listView.getItems().clear();
                listView.getItems().addAll(itemInfo);  // Update ListView after modification
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

        return stage;
    }

}
