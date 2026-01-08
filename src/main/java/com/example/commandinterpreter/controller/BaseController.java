package com.example.commandinterpreter.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public abstract class BaseController {

    protected void switchScene(String fxmlPath, double width, double height) throws IOException {

        Stage stage = (Stage) getReferenceNode().getScene().getWindow();

        boolean wasMaximized = stage.isMaximized();
        boolean wasFullscreen = stage.isFullScreen();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        scene.getStylesheets().add(
                getClass().getResource("/com/example/commandinterpreter/styles.css").toExternalForm()
        );

        stage.setScene(scene);

        stage.setMaximized(wasMaximized);
        stage.setFullScreen(wasFullscreen);

        stage.show();
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