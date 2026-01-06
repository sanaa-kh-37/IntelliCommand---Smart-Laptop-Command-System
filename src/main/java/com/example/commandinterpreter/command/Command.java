package com.example.commandinterpreter.command;

import com.example.commandinterpreter.model.User;
import java.io.IOException;

// Abstraction & Polymorphism: All commands implement this uniformly
public interface Command {
    void execute(User user) throws IOException; // Polymorphism: User for role check
    String getSpokenFeedback();
}