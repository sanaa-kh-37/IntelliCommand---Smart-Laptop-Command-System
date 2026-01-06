package com.example.commandinterpreter.service;

import com.example.commandinterpreter.db.DatabaseHandler;
import com.example.commandinterpreter.model.Role;
import com.example.commandinterpreter.model.User;

// Abstraction: Interface could be added, but here service abstracts DB
public class AuthService {
    private final DatabaseHandler dbHandler = new DatabaseHandler(); // Composition (Aggregation)

    public boolean registerUser(String username, String password, Role role) {
        User user = new User(username, password, role);
        return dbHandler.registerUser(user);
    }

    public User loginUser(String username, String password) {
        User tempUser = dbHandler.loginUser(username, password);
        if (tempUser != null) {
            int userId = dbHandler.getUserId(username);
            tempUser.setId(userId);
        }
        return tempUser;
    }
}