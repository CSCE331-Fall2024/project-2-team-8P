package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loginLoader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        Scene cashierApp = new Scene(loginLoader.load(), 1200, 800);
        stage.setTitle("Hello!");
        stage.setScene(cashierApp);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
