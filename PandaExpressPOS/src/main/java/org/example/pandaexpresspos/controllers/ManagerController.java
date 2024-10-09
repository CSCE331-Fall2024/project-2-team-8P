package org.example.pandaexpresspos.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

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
import org.example.pandaexpresspos.DataStore;

public class ManagerController {

    public HBox buttonContainer;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private Rectangle2D screen;

    private HashMap<Button, ArrayList<String>> menuItems;
    private HashMap<Button, ArrayList<String>> employees;

    /*
            ========================================
                           Menu Items
            ========================================
     */


    public void MenuItems(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/manager-view.fxml"));
        root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get items from DataStore
        menuItems = DataStore.getInstance().getMenuItems();

        List<Button> sortedMenuItems = new ArrayList<>(menuItems.keySet());
        sortedMenuItems.sort(Comparator.comparing(Button::getText));

        for (Button button : sortedMenuItems) {
            button.setOnAction(e -> {
                // Retrieve information of button clicked
                ArrayList<String> itemInfo = menuItems.get(button);

                // Update information
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

    public void addMenuItem(){
        ArrayList<String> newItem = new ArrayList<>();
        newItem.add(""); // Placeholders
        newItem.add("");

        if (buttonContainer == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MenuItems.fxml"));
            buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        }

        ChangeMenuItemInfo(newItem, () -> {
            if (!newItem.getFirst().isEmpty()) {
                Button newButton = new Button(newItem.getFirst());  // Set button's label to entered name
                menuItems = DataStore.getInstance().getMenuItems();

                // Add newButton and newItem into menuItems
                menuItems.put(newButton, newItem);
                buttonContainer.getChildren().add(newButton);

                DataStore.getInstance().setMenuItems(menuItems);  // Update MenuItems in DataStore

                // Debugging statements
                System.out.println("Added new button: " + newButton.getText());
            } else {
                System.out.println("Failed to add new button: No name provided.");
            }
        });
    }

    public void deleteMenuItems(ActionEvent event) throws IOException {
        // Load layout from FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/deleteMenuItems.fxml"));
        root = loader.load();

        // Get HBox from FXML using its fx:id
        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        menuItems = DataStore.getInstance().getMenuItems();


        List<Button> sortedMenuItems = new ArrayList<>(menuItems.keySet());
        sortedMenuItems.sort(Comparator.comparing(Button::getText));
        // Iterate through buttons to remove them when necessary
        for (Button button : sortedMenuItems) {
            button.setOnAction(e -> {
                buttonContainer.getChildren().remove(button);
                menuItems.remove(button);


                DataStore.getInstance().setMenuItems(menuItems); // Update HashMap
            });

            buttonContainer.getChildren().add(button); // Shows menu items that can be deleted
        }

        setUpScene(event, root);
    }

    public void ChangeMenuItemInfo(ArrayList<String> itemInfo, Runnable onComplete) {
        VBox layout = new VBox(10);  // VBox layout with 10px spacing

        Label label = new Label("Item Information:");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(itemInfo);

        // Allow user to enter new name
        TextField nameField = new TextField();
        nameField.setPromptText("Enter new name");

        // Allow user to enter new price
        TextField priceField = new TextField();
        priceField.setPromptText("Enter new price");

        // Finalize values
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
                if (onComplete != null) { // Adds button to Menu Items after attributes are set
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

    /*
            ========================================
                           Inventory
            ========================================
     */

    public void Inventory(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Inventory.fxml"));
        root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get inventory items from DataStore
        HashMap<Button, Integer> inventoryItems = DataStore.getInstance().getInventoryItems();

        List<Button> sortedInventory = new ArrayList<>(inventoryItems.keySet());
        sortedInventory.sort(Comparator.comparing(Button::getText));

        for (Button button : sortedInventory) {
            Integer inventoryInfo = DataStore.getInstance().getInventoryForItem(button);

            if(inventoryInfo != null) {
                if(inventoryInfo <= 25) {
                    if(inventoryInfo <= 10) {
                        button.setStyle("-fx-background-color: red;");
                    }
                    else {
                        button.setStyle("-fx-background-color: yellow;");
                    }
                }
                else {
                    button.setStyle("-fx-background-color: green;");
                }
            }

            button.setOnAction(e -> {
                // Retrieve corresponding inventory value for button clicked


                if (inventoryInfo != null) {
                    ChangeInventoryItemInfo(DataStore.getInstance(), button, () -> {
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

    public void ChangeInventoryItemInfo(DataStore inventoryWrapper, Button button, Runnable onComplete) {
        VBox layout = new VBox(10);

        Label label = new Label("Inventory Information:");

        ListView<Integer> listView = new ListView<>();
        listView.getItems().add(inventoryWrapper.getInventoryForItem(button));

        TextField inventoryField = new TextField();
        inventoryField.setPromptText("Enter amount you would like to add to inventory");

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
                    if(newInventory <= 25) {
                        if(newInventory <= 10) {
                            button.setStyle("-fx-background-color: red;");
                        }
                        else {
                            button.setStyle("-fx-background-color: yellow;");
                        }
                    }
                    else {
                        button.setStyle("-fx-background-color: green;");
                    }
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

    /*
            ========================================
                            Employees
            ========================================
     */

    public void Employees(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Employees.fxml"));
        root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        // Get items from DataStore
        employees = DataStore.getInstance().getEmployees();

        List<Button> sortedEmployees = new ArrayList<>(employees.keySet());
        sortedEmployees.sort(Comparator.comparing(Button::getText));

        for (Button button : sortedEmployees) {
            button.setOnAction(e -> {
                ArrayList<String> itemInfo = employees.get(button);

                if (itemInfo != null) {
                    ChangeEmployeeInfo(itemInfo, () -> {
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

    public void addEmployee(){
        ArrayList<String> newItem = new ArrayList<>();
        newItem.add("");
        newItem.add("");

        if (buttonContainer == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/Employees.fxml"));
            buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        }

        ChangeEmployeeInfo(newItem, () -> {
            if (!newItem.getFirst().isEmpty()) {
                Button newButton = new Button(newItem.getFirst());
                employees = DataStore.getInstance().getEmployees();

                employees.put(newButton, newItem);
                buttonContainer.getChildren().add(newButton);

                DataStore.getInstance().setEmployees(employees);

                // Debugging statements
                System.out.println("Added new button: " + newButton.getText());
            } else {
                System.out.println("Failed to add new button: No name provided.");
            }
        });
    }

    public void deleteEmployee(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/deleteEmployees.fxml"));
        root = loader.load();

        HBox buttonContainer = (HBox) loader.getNamespace().get("buttonContainer");
        buttonContainer.getChildren().clear();

        employees = DataStore.getInstance().getEmployees();

        List<Button> sortedEmployees = new ArrayList<>(employees.keySet());
        sortedEmployees.sort(Comparator.comparing(Button::getText));

        for (Button button : sortedEmployees) {
            button.setOnAction(e -> {
                buttonContainer.getChildren().remove(button);
                employees.remove(button);


                DataStore.getInstance().setEmployees(employees); // Update HashMap
            });

            buttonContainer.getChildren().add(button);
        }

        setUpScene(event, root);
    }

    public void ChangeEmployeeInfo(ArrayList<String> itemInfo, Runnable onComplete) {
        VBox layout = new VBox(10);

        Label label = new Label("Item Information:");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(itemInfo);

        TextField nameField = new TextField();
        nameField.setPromptText("Enter new name");

        TextField positionField = new TextField();
        positionField.setPromptText("Enter position");

        Button changeNameButton = new Button("Change Name");
        Button changePosition = new Button("Change Position");

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

        changePosition.setOnAction(e -> {
            String newPosition = positionField.getText();
            if (!newPosition.isEmpty() && (newPosition.equals("Manager") || newPosition.equals("Cashier"))) {
                itemInfo.set(1, newPosition);
                listView.getItems().clear();
                listView.getItems().addAll(itemInfo);
                if (onComplete != null) { // Adds button to Employees
                    onComplete.run();
                }
            } else {
                System.out.println("Position cannot be empty.");
            }
        });

        layout.getChildren().addAll(label, listView, nameField, changeNameButton, positionField, changePosition);

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

    /*
            ========================================
                         Display Scenes
            ========================================
     */

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
}