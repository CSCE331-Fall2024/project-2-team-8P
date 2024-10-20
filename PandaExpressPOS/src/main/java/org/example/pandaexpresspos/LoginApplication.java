package org.example.pandaexpresspos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The LoginApplication class is the main entry point for the Panda Express POS application.
 *
 * <p>The class configures logging, loads the login FXML view, and displays it within a scene.
 * The application window is sized at 1200x800 pixels and is titled "Login".</p>
 */
public class LoginApplication extends Application {

    /**
     * The start method is called when the JavaFX application is launched.
     *
     * @param stage the primary stage for the JavaFX application
     * @throws IOException if the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Suppress verbose logging output
        Logger.getLogger("javafx").setLevel(Level.SEVERE);
        Logger.getLogger("com.sun.javafx").setLevel(Level.SEVERE);

        // Load the login view FXML file and create a scene
        FXMLLoader fxmlLoader = new FXMLLoader(LoginApplication.class.getResource("fxml/login-view.fxml"));
        Scene loginScene = new Scene(fxmlLoader.load(), 1200, 800);

        // Set up the stage with the scene and display it
        stage.setTitle("Login");
        stage.setScene(loginScene);
        stage.show();
    }

    /**
     * The main method serves as the entry point for the JavaFX application.
     * It calls {@code launch()} to start the JavaFX runtime and open the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch();
    }
}
