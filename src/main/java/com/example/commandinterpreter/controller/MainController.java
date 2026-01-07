package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.command.Command;
import com.example.commandinterpreter.factory.CommandFactory;
import com.example.commandinterpreter.model.User;
import com.example.commandinterpreter.service.HistoryService;
import com.example.commandinterpreter.service.VoiceService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController extends BaseController implements Initializable {

    // UI Elements
    @FXML private TextField commandInput;        // Manual command input (no suggestions)
    @FXML private ListView<String> historyList;
    @FXML private Label userLabel;
    @FXML private Button historyToggleBtn;
    @FXML private VBox historyPanel;

    // Services
    private final CommandFactory factory = new CommandFactory();
    private final VoiceService voice = new VoiceService();
    private final HistoryService historyService = new HistoryService();

    private User currentUser;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load user info and history when the scene is ready
        Platform.runLater(() -> {
            if (commandInput.getScene() != null && commandInput.getScene().getUserData() instanceof User) {
                currentUser = (User) commandInput.getScene().getUserData();
                userLabel.setText("Logged in as: " + currentUser.getUsername());

                // Load this user's command history from database
                historyService.setUser(currentUser.getId());
                historyList.setItems(historyService.getHistory());
            } else {
                userLabel.setText("Logged in as: Guest");
            }
        });

        historyList.setOnMouseClicked(event -> {
            String selected = historyList.getSelectionModel().getSelectedItem();
            if (selected != null && !selected.isEmpty()) {
                commandInput.setText(selected);
                commandInput.requestFocus();
                commandInput.positionCaret(selected.length()); // Cursor at end
                // Optional: auto-execute on double click
                if (event.getClickCount() == 2) {
                    handleExecute(null);
                }
            }
        });
    }

    // Execute button or Enter key in text field triggers this
    @FXML
    private void handleExecute(ActionEvent event) {
        String input = commandInput.getText().trim();

        if (input.isEmpty()) {
            return; // Do nothing if empty
        }

        Command command = factory.getCommand(input);

        if (command == null) {
            // Unknown command → just clear and continue (no error noise)
            commandInput.clear();
            return;
        }

        try {
            // Execute the command
            command.execute(currentUser);

            // Speak feedback
            new Thread(() -> voice.speakText(command.getSpokenFeedback())).start();

            // Save to history
            historyService.addCommand(input);
            historyList.refresh(); // Update list view

        } catch (Exception e) {
            // Silent handling (or you can show an alert later if needed)
            e.printStackTrace();
        }

        // Clear input for next command
        commandInput.clear();
    }

    // Quick Action Buttons — they just trigger common commands
    @FXML private void quickNotepad()       { runCommand("open notepad"); }
    @FXML private void quickCalculator()   { runCommand("open calculator"); }
    @FXML private void quickScreenshot()   { runCommand("take screenshot"); }
    @FXML private void quickYoutube()      { runCommand("open youtube"); }
    @FXML private void quickTaskManager()  { runCommand("open task manager"); }
    @FXML private void quickSettings()     { runCommand("open settings"); }

    // Helper to run quick commands
    private void runCommand(String commandText) {
        commandInput.setText(commandText);
        handleExecute(null); // Trigger execution immediately
    }

    // Toggle history panel visibility
    @FXML
    private void toggleHistory() {
        boolean isVisible = historyPanel.isVisible();
        historyPanel.setVisible(!isVisible);
        historyPanel.setManaged(!isVisible);
        historyToggleBtn.setText(isVisible ? "📜 History ▼" : "📜 History ▲");
    }

    // Logout back to login screen
    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        switchScene("/com/example/commandinterpreter/login.fxml", 600, 500);
    }

    // Required by BaseController
    @Override
    protected Node getReferenceNode() {
        return commandInput;
    }
}