package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.model.Role;
import com.example.commandinterpreter.service.AuthService;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.IOException;

public class RegisterController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegisterSubmit(ActionEvent event) throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();



        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Required", "Please fill in all fields.");
            return;
        }


        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }


        if (username.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Weak Username", "Username should be at least 3 characters long.");
            return;
        }


        Button submitButton = (Button) event.getSource();
        submitButton.setDisable(true);


        boolean registered = authService.registerUser(username, password, Role.USER);

        if (registered) {

            showAlert(Alert.AlertType.INFORMATION, "Success!",
                    "User '" + username + "' registered successfully!\nYou can now log in.");

            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();

            switchScene("/com/example/commandinterpreter/login.fxml", 600, 500);
        } else {

            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "The username '" + username + "' is already taken.\nPlease choose a different one.");
        }


        submitButton.setDisable(false);
    }
    @FXML
    private void initialize() {
        FadeTransition fadeUsername = new FadeTransition(Duration.millis(600), usernameField);
        fadeUsername.setFromValue(0);
        fadeUsername.setToValue(1);
        fadeUsername.setDelay(Duration.millis(200));

        FadeTransition fadePassword = new FadeTransition(Duration.millis(600), passwordField);
        fadePassword.setFromValue(0);
        fadePassword.setToValue(1);
        fadePassword.setDelay(Duration.millis(400));



        ParallelTransition pt = new ParallelTransition(fadeUsername, fadePassword);
        pt.play();
    }
    @Override
    protected Node getReferenceNode() {
        return usernameField;
    }
    @FXML
    public void handleGoBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/commandinterpreter/login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Intellicommand");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}