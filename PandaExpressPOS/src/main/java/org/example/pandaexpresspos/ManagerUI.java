package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class ManagerUI extends Application {

    @Override
    public void start(Stage stage) {
        try {
            Rectangle2D screen = Screen.getPrimary().getVisualBounds();


            Parent root = FXMLLoader.load(getClass().getResource("fxml/ManagerStartingPage.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setX(0);
            stage.setY(0);
            stage.setWidth(screen.getWidth());
            stage.setHeight(screen.getHeight());
            stage.show();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}