package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.models.Employee;

import java.io.IOException;


public class LoginController {

    private final DBDriverSingleton dbDriverSingleton = DBDriverSingleton.getInstance();

    private Employee currentUser;

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    private FXMLLoader loader;
    private Scene scene;
    private Stage stage;

    private enum EmployeeType {
        CASHIER,
        MANAGER,
        ERROR
    }

    @FXML
    public void loginUser(ActionEvent event) throws IOException {
        EmployeeType employeeType = getEmployeeType(usernameTextField.getText());
        Parent root = null;

        switch (employeeType) {
            case CASHIER:
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));

                // Configure the controller factory to set the logged in user before calling `initialize()`
                loader.setControllerFactory(controllerClass -> {
                    if (controllerClass == CashierController.class) {
                        CashierController controller = new CashierController();
                        controller.setLoggedInUser(currentUser); // Inject the state here
                        return controller;
                    } else {
                        try {
                            // Default behavior for other controllers
                            return controllerClass.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                });

                root = loader.load();
                CashierController cashierController = loader.getController();
                cashierController.setLoggedInUser(currentUser);
                break;
            case MANAGER:
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/manager-view.fxml"));
                root = loader.load();
                ManagerController managerController = loader.getController();
                managerController.setLoggedInUser(currentUser);
                break;
            default:
                // TODO: display UI for Error
                showAlert("Invalid Username", "The username you entered does not exist.");
                return;

        }

        // Create a new scene and set it to the stage
        scene = new Scene(root, 1200, 800);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void setCurrentUser(String username) {
        currentUser = dbDriverSingleton.selectEmployee(username);
    }

    private EmployeeType getEmployeeType(String username) {
        setCurrentUser(username);
        if (currentUser == null) {
            showAlert("Invalid Username", "The username you entered does not exist.");
            return EmployeeType.ERROR;
        }

        if (currentUser.isManager) {
            return EmployeeType.MANAGER;
        } else {
            return EmployeeType.CASHIER;
        }
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
