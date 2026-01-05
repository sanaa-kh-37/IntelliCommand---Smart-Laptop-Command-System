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

public class RegisterController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    private final DatabaseHandler dbHandler = new DatabaseHandler();

    @FXML
    private void handleRegisterSubmit(ActionEvent event) throws IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!password.equals(confirmPassword)) {
            showAlert("Registration Failed", "Passwords do not match.");
            return;
        }

        User user = new User(username, password);
        if (dbHandler.registerUser(user)) {
            showAlert("Success", "User registered successfully.");
            switchToLoginScene();
        } else {
            showAlert("Registration Failed", "Username already exists or error occurred.");
        }
    }

    private void switchToLoginScene() throws IOException {
        Stage stage = (Stage) usernameField.getScene().getWindow();
        URL resource = getClass().getResource("/com/example/commandinterpreter/login.fxml");
        if (resource == null) {
            throw new IllegalStateException("Cannot find /com/example/commandinterpreter/login.fxml!");
        }
        System.out.println("Found login.fxml at: " + resource);

        FXMLLoader loader = new FXMLLoader(resource);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 600, 400));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}