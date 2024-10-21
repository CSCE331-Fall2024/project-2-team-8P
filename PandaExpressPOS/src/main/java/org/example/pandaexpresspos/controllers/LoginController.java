package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.models.Employee;

import java.io.IOException;

/**
 * Controller for handling the login functionality in the Panda Express POS system.
 * Responsible for managing login input and redirecting to different views based on user roles (Cashier, Manager).
 *
 * @author Soham Nagawanshi
 * @author Kevin Zhang
 */
public class LoginController {

    private final DBDriverSingleton dbDriverSingleton = DBDriverSingleton.getInstance();

    private Employee currentUser;

    @FXML
    private TextField usernameTextField;

    private FXMLLoader loader;
    private Scene scene;
    private Stage stage;

    /**
     * Enum representing the type of employee attempting to log in.
     */
    private enum EmployeeType {
        CASHIER,
        MANAGER,
        ERROR
    }

    /**
     * Handles the login action when the user clicks the login button.
     * Determines the employee type (Cashier, Manager) based on the entered username,
     * and loads the corresponding view (cashier or manager) if the username is valid.
     *
     * @param event The event triggered when the login button is clicked.
     * @throws IOException If there is an issue loading the FXML files for the views.
     */
    @FXML
    public void loginUser(ActionEvent event) throws IOException {
        EmployeeType employeeType = getEmployeeType(usernameTextField.getText());
        Parent root = null;

        switch (employeeType) {
            case CASHIER:
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));

                // Configure the controller factory to set the logged-in user before calling `initialize()`
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
                showAlert("Invalid Username", "The username you entered does not exist.");
                return;
        }

        // Create a new scene and set it to the stage
        scene = new Scene(root, 1200, 800);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Sets the currently logged-in user based on the provided username by querying the database.
     *
     * @param username The username of the employee attempting to log in.
     */
    private void setCurrentUser(String username) {
        currentUser = dbDriverSingleton.selectEmployee(username);
    }

    /**
     * Determines the type of employee (Cashier or Manager) based on the provided username.
     * Returns an EmployeeType based on the user's role or an error if the user does not exist.
     *
     * @param username The username of the employee.
     * @return The type of employee (CASHIER, MANAGER, ERROR).
     */
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

    /**
     * Displays an alert to the user with the specified title and message.
     *
     * @param title   The title of the alert.
     * @param message The message content of the alert.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
