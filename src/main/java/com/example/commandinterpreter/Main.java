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
        // Absolute path for consistency
        URL resource = getClass().getResource("/com/example/commandinterpreter/login.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find /com/example/commandinterpreter/login.fxml!");
        }
        System.out.println("Found login.fxml at: " + resource);

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        primaryStage.setTitle("Command Interpreter");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

        // Optional: Test DB connection (remove later)
        new DatabaseHandler().testConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}