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

// Inheritance: Extends BaseController for shared functionality
public class RegisterController extends BaseController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleRegisterSubmit(ActionEvent event) throws IOException {
        // Get and trim input
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // === Input Validation (in correct order) ===

        // Check empty fields first
        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Required", "Please fill in all fields.");
            return;
        }

        // Check password match
        if (!password.equals(confirm)) {
            showAlert(Alert.AlertType.ERROR, "Password Mismatch", "Passwords do not match. Please try again.");
            return;
        }

        // Optional: Username format validation (basic)
        if (username.length() < 3) {
            showAlert(Alert.AlertType.WARNING, "Weak Username", "Username should be at least 3 characters long.");
            return;
        }

        // Disable button to prevent double-click submission
        Button submitButton = (Button) event.getSource();
        submitButton.setDisable(true);

        // === Attempt Registration ===
        boolean registered = authService.registerUser(username, password, Role.USER);

        if (registered) {
            // Success!
            showAlert(Alert.AlertType.INFORMATION, "Success!",
                    "User '" + username + "' registered successfully!\nYou can now log in.");
            // Clear fields for good UX
            usernameField.clear();
            passwordField.clear();
            confirmPasswordField.clear();
            // Go back to login screen
            switchScene("/com/example/commandinterpreter/login.fxml", 600, 500);
        } else {
            // Failure – most likely duplicate username
            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "The username '" + username + "' is already taken.\nPlease choose a different one.");
        }

        // Re-enable button in case of failure
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

        // Add more for confirmPasswordField in Register

        ParallelTransition pt = new ParallelTransition(fadeUsername, fadePassword);
        pt.play();
    }
    @Override
    protected Node getReferenceNode() {
        // Used by BaseController to get current stage
        return usernameField;
    }
    @FXML
    public void handleGoBackToLogin(ActionEvent event) {
        try {
            // Load the Login FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/commandinterpreter/login.fxml"));
            Parent root = loader.load();

            // Get the current stage/window from the event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Intellicommand"); // consistent title
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}