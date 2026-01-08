package com.example.commandinterpreter.command;

public class SystemCommand extends AbstractCommand {
    public SystemCommand(String osCommand, String feedback) {
        super(osCommand, feedback);
    }

    @Override
    protected boolean requiresAdmin() {
        return false; // Basic commands
    }
}