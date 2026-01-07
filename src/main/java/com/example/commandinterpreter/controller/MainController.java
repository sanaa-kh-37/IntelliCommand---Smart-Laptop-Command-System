package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.command.Command;
import com.example.commandinterpreter.factory.CommandFactory;
import com.example.commandinterpreter.model.User;
import com.example.commandinterpreter.service.HistoryService;
import com.example.commandinterpreter.service.VoiceService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController extends BaseController implements Initializable {

    @FXML private ComboBox<String> commandCombo;
    @FXML private ListView<String> historyList;
    @FXML private Label userLabel;
    @FXML private Button historyToggleBtn;
    @FXML private VBox historyPanel;

    private final CommandFactory factory = new CommandFactory();
    private final VoiceService voice = new VoiceService();
    private final HistoryService historyService = new HistoryService();

    private User currentUser;
    private final ObservableList<String> favorites = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            currentUser = (User) commandCombo.getScene().getUserData();
            if (currentUser != null) {
                userLabel.setText("Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
                historyService.setUser(currentUser.getId());
                refreshHistory();
            }
            setupAutocomplete();
            setupModernHistoryList(); // ← Improved styling
        });
    }

    private void setupAutocomplete() {
        commandCombo.setEditable(true);
        commandCombo.getEditor().focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (isNowFocused) {
                commandCombo.show();
            }
        });

        commandCombo.getEditor().textProperty().addListener((obs, old, newVal) -> {
            if (newVal == null || newVal.trim().isEmpty()) {
                commandCombo.hide();
                return;
            }
            String lower = newVal.toLowerCase().trim();
            var suggestions = factory.getAllKnownCommands().stream()
                    .filter(cmd -> cmd.toLowerCase().contains(lower))
                    .limit(12)
                    .collect(Collectors.toList());

            if (!suggestions.isEmpty()) {
                commandCombo.setItems(FXCollections.observableArrayList(suggestions));
                commandCombo.show();
            } else {
                commandCombo.hide();
            }
        });
    }

    // NEW: Modern, clean history list that matches the purple theme
    private void setupModernHistoryList() {
        historyList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("  " + item);
                    setStyle("""
                        -fx-font-family: 'Segoe UI', Arial, sans-serif;
                        -fx-font-size: 15px;
                        -fx-text-fill: #e9d8fd;
                        -fx-padding: 10 15;
                        -fx-background-color: rgba(255, 255, 255, 0.08);
                        -fx-background-radius: 12;
                        -fx-border-radius: 12;
                        """);
                }
            }
        });

        // Hover effect on history items
        historyList.setOnMouseClicked(e -> {
            String selected = historyList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                commandCombo.getEditor().setText(selected);
                commandCombo.getEditor().positionCaret(selected.length());
            }
        });
    }

    private void refreshHistory() {
        ObservableList<String> combined = FXCollections.observableArrayList();
        combined.addAll(favorites);
        combined.addAll(historyService.getHistory().stream()
                .filter(c -> !favorites.contains(c))
                .toList());
        historyList.setItems(combined);
    }

    @FXML private void handleExecute(ActionEvent event) {
        String input = commandCombo.getEditor().getText();
        if (input == null || input.trim().isEmpty()) return;

        String cmdText = input.trim();
        Command cmd = factory.getCommand(cmdText.toLowerCase());
        if (cmd != null) {
            try {
                cmd.execute(currentUser);
                new Thread(() -> voice.speakText(cmd.getSpokenFeedback())).start();
                historyService.addCommand(cmdText);
                refreshHistory();
            } catch (Exception ex) {
                // Silent fail
            }
        }
        commandCombo.getEditor().clear();
        commandCombo.hide();
    }

    // Quick Actions (unchanged)
    @FXML private void quickNotepad() { runCommand("open notepad"); }
    @FXML private void quickCalculator() { runCommand("open calculator"); }
    @FXML private void quickScreenshot() { runCommand("take screenshot"); }
    @FXML private void quickYoutube() { runCommand("open youtube"); }
    @FXML private void quickTaskManager() { runCommand("open task manager"); }
    @FXML private void quickSettings() { runCommand("open settings"); }

    private void runCommand(String cmd) {
        commandCombo.getEditor().setText(cmd);
        handleExecute(null);
    }

    @FXML private void toggleHistory() {
        boolean visible = historyPanel.isVisible();
        historyPanel.setVisible(!visible);
        historyPanel.setManaged(!visible);
        historyToggleBtn.setText(visible ? "📜 History ▼" : "📜 History ▲");
    }

    @FXML private void handleLogout(ActionEvent event) throws IOException {
        switchScene("/com/example/commandinterpreter/login.fxml", 600, 500);
    }

    @Override
    protected Node getReferenceNode() {
        return commandCombo;
    }
}