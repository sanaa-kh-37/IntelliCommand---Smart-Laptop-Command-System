package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.model.User;
import com.example.commandinterpreter.service.AuthService;
import javafx.animation.FadeTransition;
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
import javafx.util.Duration;

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

            Stage stage = (Stage) usernameField.getScene().getWindow();


            boolean wasMaximized = stage.isMaximized();
            boolean wasFullscreen = stage.isFullScreen();

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/commandinterpreter/main.fxml")
            );
            Parent root = loader.load();

            Scene scene = new Scene(root);
            scene.setUserData(user);
            scene.getStylesheets().add(
                    getClass().getResource("/com/example/commandinterpreter/styles.css").toExternalForm()
            );


            root.setOpacity(0);
            stage.setScene(scene);

            FadeTransition fade = new FadeTransition(Duration.millis(350), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();


            stage.setMaximized(wasMaximized);
            stage.setFullScreen(wasFullscreen);

            stage.show();

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