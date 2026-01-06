package com.example.commandinterpreter.command;

import com.example.commandinterpreter.model.Role;
import com.example.commandinterpreter.model.User;
import java.io.IOException;

// Inheritance: Subclasses inherit execute logic; Abstraction: Hides base exec
public abstract class AbstractCommand implements Command {
    protected final String osCommand;
    protected final String defaultFeedback;

    protected AbstractCommand(String osCommand, String defaultFeedback) {
        this.osCommand = osCommand;
        this.defaultFeedback = defaultFeedback;
    }

    @Override
    public void execute(User user) throws IOException {
        // Polymorphism: Can be overridden; Role check example
        if (requiresAdmin() && user.getRole() != Role.ADMIN) {
            throw new SecurityException("Admin only");
        }
        new ProcessBuilder("cmd.exe", "/c", osCommand).start();
    }

    protected abstract boolean requiresAdmin(); // To be implemented by subclasses

    @Override
    public String getSpokenFeedback() {
        return defaultFeedback;
    }
}