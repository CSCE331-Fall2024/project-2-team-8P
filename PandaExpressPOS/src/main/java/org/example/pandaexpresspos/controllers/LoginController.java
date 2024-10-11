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

import static org.example.pandaexpresspos.database.DBDriverSingleton.checkLogin;



public class LoginController {

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
    void loginUser(ActionEvent event) throws IOException {
        EmployeeType emp = getEmployeeType(usernameTextField.getText());
        switch (emp) {
            case CASHIER:
                // load the fxml for screen switch
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));
                // Create a new scene and set it to the stage
                scene = new Scene(loader.load(), 1200, 800);
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                break;
            case MANAGER:
                // TODO: Connect to Manager View
                // load the fxml for screen switch
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/manager-view.fxml"));
                // Create a new scene and set it to the stage
                scene = new Scene(loader.load(), 1200, 800);
                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();
                break;
            default:
                // TODO: UI for Error
                System.out.println("An error has occured");
        }
    }

    EmployeeType getEmployeeType(String username) {
        // TODO: Query Database for name matching
        username = checkLogin(username);
        return switch (username) {
            case "Cashier" -> EmployeeType.CASHIER;
            case "Manager" -> EmployeeType.MANAGER;
            default -> EmployeeType.ERROR;
        };
    }

}
