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

    // Initialize Buttons
    Button buttonA = new Button("Button A");
    Button buttonB = new Button("Button B");
    Button buttonC = new Button("Button C");

    {
        // Initialize ArrayLists of Strings for each button
        ArrayList<String> listA = new ArrayList<>(Arrays.asList("Item A1", "Item A2"));
        ArrayList<String> listB = new ArrayList<>(Arrays.asList("Item B1", "Item B2"));
        ArrayList<String> listC = new ArrayList<>(Arrays.asList("Item C1", "Item C2"));

        // Add Buttons and their corresponding ArrayLists to the HashMap
        items.put(buttonA, listA);
        items.put(buttonB, listB);
        items.put(buttonC, listC);
    }

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
        // Load the layout or popup as necessary
        popup();

        // Ensure buttonContainer is initialized
        if (buttonContainer == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
            Parent root = loader.load();
            buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        }

        HashMap<Button, ArrayList<String>> items = DataStore.getInstance().getMenuItems();

        // Add new button to the container and items map
        Button newButton = new Button("Button D");
        ArrayList<String> newList = new ArrayList<>(); // Create a new ArrayList for the new button
        items.put(newButton, newList); // Associate the new button with its item list
        buttonContainer.getChildren().add(newButton);

        DataStore.getInstance().setMenuItems(items); // Update HashMap
        DataStore.getInstance().setNumItems(items.size()); // Update numItems

        // For debugging
        System.out.println("Number of items: " + DataStore.getInstance().getNumItems());
    }

    public void switchToScene1(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
        Parent root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get the items from DataStore
        HashMap<Button, ArrayList<String>> items = DataStore.getInstance().getMenuItems();

        // Label or TextArea to display the information
        Label infoLabel = new Label();
        buttonContainer.getChildren().add(infoLabel);

        // Iterate over each button in the HashMap
        for (Button button : items.keySet()) {
            // Set the event handler for each button
            button.setOnAction(e -> {
                // Retrieve the corresponding list of strings
                ArrayList<String> itemInfo = items.get(button);

                // Display the information in the label
                StringBuilder info = new StringBuilder();
                for (String infoItem : itemInfo) {
                    info.append(infoItem).append("\n");
                }
                infoLabel.setText(info.toString());
            });

            // Add the button to the HBox container
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

    public Stage popup() {
        VBox layout = new VBox(10);  // VBox layout with 10px spacing

        Label label = new Label("Select an option");

        // Create buttons
        Button button1 = new Button("Change Name");
        Button button2 = new Button("Change Price");

        // Set button actions to update the label and change num
        button1.setOnAction(e -> {
            label.setText(button1.getText());
            DataStore.getInstance().setPrice(1);  // Update num to 1 when button1 is clicked
        });

        button2.setOnAction(e -> {
            label.setText(button2.getText());
            DataStore.getInstance().setPrice(2);
        });

        Stage stage = new Stage();
        stage.setWidth(300);
        stage.setHeight(200);
        stage.setX(500);
        stage.setY(500);

        // Use VBox layout for vertical stacking
        layout.setAlignment(Pos.CENTER);  // Center the content in the popup

        layout.getChildren().addAll(label, button1, button2);

        Scene scene = new Scene(layout);
        stage.setScene(scene);
        stage.show();

        return stage;
    }
}
