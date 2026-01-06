package com.example.commandinterpreter;

import com.example.commandinterpreter.db.DatabaseHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load login FXML (Abstraction: UI separated from logic)
        URL resource = getClass().getResource("/com/example/commandinterpreter/login.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find login.fxml");
        }
        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        primaryStage.setTitle("IntelliCommand - Voice Command Interpreter");
        primaryStage.setScene(new Scene(root, 600, 500)); // Larger for advanced layout
        primaryStage.show();

        // Test DB (remove in prod)
        new DatabaseHandler().testConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}