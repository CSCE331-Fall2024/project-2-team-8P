package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;

import java.io.IOException;


public class LoginController {

    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    private enum EmployeeType {
        CASHIER,
        MANAGER,
        ERROR
    }

    @FXML
    void loginUser(ActionEvent event) throws IOException {
        EmployeeType emp = getEmployeeType(usernameTextField.getText());
        switch (emp) {
            case CASHIER:
                // load the fxml for screen switch
                FXMLLoader loader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));
                // Create a new scene and set it to the stage
                Scene scene = new Scene(loader.load(), 1200, 800);
                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                break;
            case MANAGER:
                // TODO: Connect to Manager View
                break;
            default:
                // TODO: UI for Error
                System.out.println("An error has occured");
        }
    }

    EmployeeType getEmployeeType(String username) {
        // TODO: Query Database for name matching
        return switch (username.toLowerCase()) {
            case "cashier" -> EmployeeType.CASHIER;
            case "manager" -> EmployeeType.MANAGER;
            default -> EmployeeType.ERROR;
        };
    }

}
