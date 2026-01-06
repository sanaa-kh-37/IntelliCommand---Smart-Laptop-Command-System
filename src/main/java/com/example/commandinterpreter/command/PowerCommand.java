package com.example.commandinterpreter.command;


public class PowerCommand extends AbstractCommand {
    public PowerCommand(String osCommand, String feedback) {
        super(osCommand, feedback);
    }

    @Override
    protected boolean requiresAdmin() {
        return true; // Power commands require admin (polymorphism in access)
    }
}