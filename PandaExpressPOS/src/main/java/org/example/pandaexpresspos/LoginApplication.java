package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        FXMLLoader cashierLoader = new FXMLLoader(LoginApplication.class.getResource("fxml/cashier-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        Scene cashierApp = new Scene(cashierLoader.load(), 1200, 800);
        stage.setTitle("Hello!");
        stage.setScene(cashierApp);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
