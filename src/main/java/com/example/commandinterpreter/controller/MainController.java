package com.example.commandinterpreter.controller;

import com.example.commandinterpreter.command.Command;
import com.example.commandinterpreter.factory.CommandFactory;
import com.example.commandinterpreter.model.User;
import com.example.commandinterpreter.service.HistoryService;
import com.example.commandinterpreter.service.VoiceService;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class MainController extends BaseController implements Initializable {

    @FXML private ComboBox<String> commandCombo;
    @FXML private TextArea outputArea;
    @FXML private ListView<String> historyList;
    @FXML private Label userLabel;
    @FXML private Button themeButton;

    private final CommandFactory factory = new CommandFactory();
    private final VoiceService voice = new VoiceService();
    private final HistoryService historyService = new HistoryService();

    private User currentUser;
    private boolean isDarkMode = true;
    private final ObservableList<String> favorites = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            currentUser = (User) commandCombo.getScene().getUserData();
            if (currentUser != null) {
                userLabel.setText("👤 Logged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
                historyService.setUser(currentUser.getId());
                refreshHistory();
            }

            setupAutocomplete();
            setupFavorites();
            applyTheme(); // Initial theme
        });
    }

    private void setupAutocomplete() {
        commandCombo.setEditable(true);

        // Create a dedicated list for suggestions
        ObservableList<String> suggestions = FXCollections.observableArrayList();

        commandCombo.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.trim().isEmpty()) {
                commandCombo.hide();
                suggestions.clear();
                return;
            }

            String lower = newText.toLowerCase().trim();

            // Get matching commands from factory
            var matching = factory.getAllKnownCommands().stream()
                    .filter(cmd -> cmd.startsWith(lower))
                    .limit(10)
                    .collect(Collectors.toList());

            // Also include matching history commands
            var historyMatches = historyService.getHistory().stream()
                    .filter(cmd -> cmd.toLowerCase().startsWith(lower))
                    .limit(5)
                    .collect(Collectors.toList());

            suggestions.clear();
            suggestions.addAll(matching);
            suggestions.addAll(historyMatches);

            if (!suggestions.isEmpty()) {
                commandCombo.setItems(suggestions);
                commandCombo.show();
            } else {
                commandCombo.hide();
            }
        });

        // Execute on Enter
        commandCombo.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                handleExecute(null);
            }
        });

        // When user selects from dropdown, fill editor
        commandCombo.setOnAction(e -> {
            String selected = commandCombo.getSelectionModel().getSelectedItem();
            if (selected != null) {
                commandCombo.getEditor().setText(selected);
                commandCombo.getEditor().positionCaret(selected.length());
            }
        });
    }

    private void setupFavorites() {
        historyList.setCellFactory(lv -> new ListCell<String>() {
            private final Button starBtn = new Button("☆");

            {
                starBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: gold; -fx-font-size: 16px;");
                starBtn.setOnAction(e -> {
                    String item = getItem();
                    if (item != null) toggleFavorite(item);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(item);
                    starBtn.setText(favorites.contains(item) ? "★" : "☆");
                    setGraphic(starBtn);
                }
            }
        });
    }

    private void toggleFavorite(String cmd) {
        if (favorites.contains(cmd)) {
            favorites.remove(cmd);
        } else {
            favorites.add(0, cmd);
        }
        refreshHistory();
    }

    private void refreshHistory() {
        // ← FIX: Explicitly declare as ObservableList<String>
        ObservableList<String> combined = FXCollections.observableArrayList();

        // Add favorites first (pinned at top)
        combined.addAll(favorites);

        // Add non-favorite history items
        combined.addAll(
                historyService.getHistory().stream()
                        .filter(cmd -> !favorites.contains(cmd))
                        .toList()
        );

        // Now safe: both sides are ObservableList<String>
        historyList.setItems(combined);
    }

    @FXML
    private void handleExecute(ActionEvent event) {
        String input = commandCombo.getEditor().getText();
        if (input == null || input.trim().isEmpty()) return;

        String cmdText = input.trim();
        outputArea.appendText("> " + cmdText + "\n");

        Command cmd = factory.getCommand(cmdText.toLowerCase());
        if (cmd != null) {
            try {
                cmd.execute(currentUser);
                outputArea.appendText("✔ " + cmd.getSpokenFeedback() + "\n\n");
                new Thread(() -> voice.speakText(cmd.getSpokenFeedback())).start();
                historyService.addCommand(cmdText);
                refreshHistory();
                animatePulse();
            } catch (Exception ex) {
                outputArea.appendText("❌ Error\n\n");
            }
        } else {
            outputArea.appendText("❌ Unknown command\n\n");
        }
        commandCombo.getEditor().clear();
    }

    // Quick Actions
    @FXML private void quickNotepad() { runCommand("open notepad"); }
    @FXML private void quickCalculator() { runCommand("open calculator"); }
    @FXML private void quickScreenshot() { runCommand("take screenshot"); }
    @FXML private void quickYoutube() { runCommand("open youtube"); }

    private void runCommand(String cmd) {
        commandCombo.getEditor().setText(cmd);
        handleExecute(null);
    }

    @FXML
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        applyTheme();
        themeButton.setText(isDarkMode ? "🌙 Dark Mode" : "☀ Light Mode");
    }

    private void applyTheme() {
        String bg = isDarkMode ? "#1e1e1e" : "#f8f9fa";
        String card = isDarkMode ? "#2d2d30" : "#ffffff";
        String text = isDarkMode ? "white" : "#333333";
        String inputBg = isDarkMode ? "#333333" : "#eeeeee";
        String outputBg = isDarkMode ? "#00000080" : "#00000040";

        VBox center = (VBox) commandCombo.getScene().lookup("VBox");
        if (center != null) center.setStyle("-fx-background-color: " + bg + "; -fx-background-radius: 20;");

        commandCombo.setStyle("-fx-background-color: " + inputBg + "; -fx-text-fill: " + text + ";");
        outputArea.setStyle("-fx-background-color: " + outputBg + "; -fx-text-fill: " + text + ";");
        userLabel.setStyle("-fx-text-fill: " + (isDarkMode ? "#a0ffa0" : "#006400") + ";");
    }

    private void animatePulse() {
        ScaleTransition st = new ScaleTransition(Duration.millis(150), commandCombo);
        st.setFromX(1); st.setToX(1.08);
        st.setFromY(1); st.setToY(1.08);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    @FXML
    private void handleLogout(ActionEvent event) throws IOException {
        switchScene("/com/example/commandinterpreter/login.fxml", 600, 500);
    }

    @Override
    protected Node getReferenceNode() {
        return commandCombo;
    }
}