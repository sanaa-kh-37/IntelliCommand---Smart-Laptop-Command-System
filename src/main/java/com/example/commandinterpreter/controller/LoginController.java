package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.model.User;
import com.example.commandinterpreter.service.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;


public class LoginController extends BaseController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Required", "Please enter username and password.");
            return;
        }

        User user = authService.loginUser(username, password);
        if (user != null) {
            // Load new scene
            URL resource = getClass().getResource("/com/example/commandinterpreter/main.fxml");
            if (resource == null) {
                throw new IllegalStateException("main.fxml not found");
            }

            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            Scene newScene = new Scene(root, 800, 600);


            newScene.setUserData(user);


            URL cssUrl = getClass().getResource("/com/example/commandinterpreter/styles.css");
            if (cssUrl != null) {
                newScene.getStylesheets().add(cssUrl.toExternalForm());
            }

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(newScene);
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) throws IOException {
        switchScene("/com/example/commandinterpreter/register.fxml", 600, 500);
    }

    @Override
    protected Node getReferenceNode() {
        return usernameField;
    }
}