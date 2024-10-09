package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/hello-view.fxml"));
        FXMLLoader CashierLoader = new FXMLLoader(getClass().getResource("fxml/cashier-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Scene cashierApp = new Scene(CashierLoader.load(), 1200, 800);
        stage.setTitle("Hello!");
        stage.setScene(cashierApp);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}