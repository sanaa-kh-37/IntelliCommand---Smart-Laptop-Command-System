package com.example.commandinterpreter.service;

import com.example.commandinterpreter.db.DatabaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Collections;

public class HistoryService {
    private final ObservableList<String> history = FXCollections.observableArrayList();
    private final DatabaseHandler dbHandler = new DatabaseHandler();
    private int currentUserId = -1;

    public void setUser(int userId) {
        this.currentUserId = userId;
        loadHistoryFromDB();
    }

    public void addCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return;
        }

        String trimmed = command.trim();

        if (currentUserId != -1) {

            history.add(0, trimmed);
            dbHandler.saveCommandHistory(currentUserId, trimmed);
        }
    }

    private void loadHistoryFromDB() {
        history.clear();
        if (currentUserId != -1) {
            var loaded = dbHandler.loadCommandHistory(currentUserId);
            Collections.reverse(loaded);
            history.addAll(loaded);
        }
    }

    public ObservableList<String> getHistory() {
        return history;
    }

    public void clearHistory() {
        history.clear();
    }
}