package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.db.DatabaseHandler;
import com.example.commandinterpreter.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    private final DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        User user = dbHandler.loginUser(username, password);
        if (user != null) {
            switchToMainScene();
        } else {
            showAlert("Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        switchToRegisterScene();
    }

    private void switchToMainScene() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        URL resource = getClass().getResource("/com/example/commandinterpreter/main.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find /com/example/commandinterpreter/main.fxml!");
        }
        System.out.println("Found main.fxml at: " + resource);

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 600, 400));
    }

    private void switchToRegisterScene() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        URL resource = getClass().getResource("/com/example/commandinterpreter/register.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find /com/example/commandinterpreter/register.fxml!");
        }
        System.out.println("Found register.fxml at: " + resource);

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 600, 400));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}