package com.example.commandinterpreter.command;

// Inheritance & Polymorphism: Overrides feedback
public class BrowserCommand extends AbstractCommand {
    public BrowserCommand(String url, String siteName) {
        super("start " + url, "Opening " + siteName);
    }

    @Override
    public String getSpokenFeedback() {
        return super.getSpokenFeedback() + " in browser."; // Override
    }

    @Override
    protected boolean requiresAdmin() {
        return false;
    }
}