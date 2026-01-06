package com.example.commandinterpreter.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

// Abstract base for controllers (Abstraction & Inheritance)
public abstract class BaseController {

    protected void switchScene(String fxmlPath, double width, double height) throws IOException {
        Stage stage = (Stage) getReferenceNode().getScene().getWindow();
        URL resource = getClass().getResource(fxmlPath);
        if (resource == null) {
            throw new IllegalStateException("FXML not found: " + fxmlPath);
        }

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        Scene scene = new Scene(root, width, height);

        // Safely add stylesheet if exists
        URL cssUrl = getClass().getResource("/com/example/commandinterpreter/styles.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        } else {
            System.out.println("Warning: styles.css not found – running without custom styling");
        }

        stage.setScene(scene);
    }

    protected void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected abstract Node getReferenceNode();
}