package org.example.pandaexpresspos.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.pandaexpresspos.LoginApplication;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import org.example.pandaexpresspos.models.Employee;

import java.io.IOException;


public class LoginController {

    private DBDriverSingleton dbDriverSingleton = DBDriverSingleton.getInstance();

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
        EmployeeType employeeType = getEmployeeType(usernameTextField.getText());

        switch (employeeType) {
            case CASHIER:
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));

                break;
            case MANAGER:
                loader = new FXMLLoader(LoginApplication.class.getResource("fxml/manager-view.fxml"));
                break;
            default:
                // TODO: display UI for Error
                System.out.println("Employee does not exist, exiting...");
                System.exit(1);
        }


        // Create a new scene and set it to the stage
        scene = new Scene(loader.load(), 1200, 800);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    EmployeeType getEmployeeType(String username) {
        Employee currentEmployee = dbDriverSingleton.selectEmployee(username);
        if (currentEmployee == null) {
            return EmployeeType.ERROR;
        }

        if (currentEmployee.isManager) {
            return EmployeeType.MANAGER;
        } else {
            return EmployeeType.CASHIER;
        }
    }

}
