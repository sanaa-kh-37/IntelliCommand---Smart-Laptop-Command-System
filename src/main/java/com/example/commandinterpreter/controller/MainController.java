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
    @FXML private TextField commandInput;
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

        Platform.runLater(() -> {
            if (commandInput.getScene() != null && commandInput.getScene().getUserData() instanceof User) {
                currentUser = (User) commandInput.getScene().getUserData();
                userLabel.setText("Logged in as: " + currentUser.getUsername());

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
                commandInput.positionCaret(selected.length());
                if (event.getClickCount() == 2) {
                    handleExecute(null);
                }
            }
        });
    }

    @FXML
    private void handleExecute(ActionEvent event) {
        String input = commandInput.getText().trim();

        if (input.isEmpty()) {
            return;
        }

        Command command = factory.getCommand(input);

        if (command == null) {

            commandInput.clear();
            return;
        }

        try {
            command.execute(currentUser);

            new Thread(() -> voice.speakText(command.getSpokenFeedback())).start();

            historyService.addCommand(input);
            historyList.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }

        commandInput.clear();
    }

    @FXML private void quickNotepad()       { runCommand("open notepad"); }
    @FXML private void quickCalculator()   { runCommand("open calculator"); }
    @FXML private void quickScreenshot()   { runCommand("take screenshot"); }
    @FXML private void quickYoutube()      { runCommand("open youtube"); }
    @FXML private void quickTaskManager()  { runCommand("open task manager"); }
    @FXML private void quickSettings()     { runCommand("open settings"); }

    private void runCommand(String commandText) {
        commandInput.setText(commandText);
        handleExecute(null);
    }

    @FXML
    private void toggleHistory() {
        boolean isVisible = historyPanel.isVisible();
        historyPanel.setVisible(!isVisible);
        historyPanel.setManaged(!isVisible);
        historyToggleBtn.setText(isVisible ? "📜 History ▼" : "📜 History ▲");
    }

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