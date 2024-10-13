package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.pandaexpresspos.database.DBDriverSingleton;
import java.io.IOException;
import java.util.HashMap;

public class LoginApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
