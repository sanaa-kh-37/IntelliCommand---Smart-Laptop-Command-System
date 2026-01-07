package com.example.commandinterpreter.factory;

import com.example.commandinterpreter.command.BrowserCommand;
import com.example.commandinterpreter.command.Command;
import com.example.commandinterpreter.command.PowerCommand;
import com.example.commandinterpreter.command.SystemCommand;
import com.example.commandinterpreter.util.ConfigLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class CommandFactory {
    private final Map<String, Command> commandsMap = new HashMap<>();
    private final ConfigLoader loader = new ConfigLoader();

    public Command getCommand(String userInput) {
        String key = userInput.toLowerCase().trim();
        return commandsMap.computeIfAbsent(key, this::createCommand);
    }

    public List<String> getAllKnownCommands() {
        return new ArrayList<>(commandsMap.keySet());
    }

    private Command createCommand(String key) {
        // Dynamic commands from JSON first
        Command dynamic = loader.loadCommand(key);
        if (dynamic != null) return dynamic;

        // Built-in commands
        return switch (key) {
            // Basic apps
            case "open notepad" -> new SystemCommand("notepad", "Opening Notepad");
            case "open calculator", "open calc" -> new SystemCommand("calc", "Opening Calculator");
            case "open paint" -> new SystemCommand("mspaint", "Opening Paint");
            case "open command prompt", "open cmd" -> new SystemCommand("cmd", "Opening Command Prompt");
            case "open task manager" -> new SystemCommand("taskmgr", "Opening Task Manager");
            case "open file explorer" -> new SystemCommand("explorer", "Opening File Explorer");
            case "take screenshot", "open snipping tool" -> new SystemCommand("snippingtool", "Opening Snipping Tool");
            case "open volume mixer" -> new SystemCommand("sndvol", "Opening Volume Mixer");

            // Settings & Control Panel (use 'start ms-settings:' or direct)
            case "open settings" -> new SystemCommand("start ms-settings:", "Opening Settings");
            case "open display settings" -> new SystemCommand("start ms-settings:display", "Opening Display Settings");
            case "open sound settings" -> new SystemCommand("start ms-settings:sound", "Opening Sound Settings");
            case "open control panel" -> new SystemCommand("control", "Opening Control Panel");
            case "open devices and printers" -> new SystemCommand("control printers", "Opening Devices and Printers");
            case "open programs and features" -> new SystemCommand("appwiz.cpl", "Opening Programs and Features");

            // Browsers (Windows registered app commands)
            case "open chrome" -> new SystemCommand("start chrome", "Opening Chrome");
            case "open edge" -> new SystemCommand("start msedge", "Opening Microsoft Edge");
            case "open firefox" -> new SystemCommand("start firefox", "Opening Firefox");

            // Websites
            case "open youtube" -> new BrowserCommand("https://www.youtube.com", "Opening YouTube");
            case "open google" -> new BrowserCommand("https://www.google.com", "Opening Google");
            case "open gmail" -> new BrowserCommand("https://mail.google.com", "Opening Gmail");
            case "open github" -> new BrowserCommand("https://github.com", "Opening GitHub");
            case "open whatsapp web" -> new BrowserCommand("https://web.whatsapp.com", "Opening WhatsApp Web");
            case "open chatgpt" -> new BrowserCommand("https://chat.openai.com", "Opening ChatGPT");

            // Admin-only power commands
            case "shutdown pc", "turn off computer" -> new PowerCommand("shutdown /s /t 10", "Shutting down in 10 seconds");
            case "restart pc", "reboot computer" -> new PowerCommand("shutdown /r /t 10", "Restarting in 10 seconds");
            case "sleep pc", "put computer to sleep" -> new PowerCommand("rundll32.exe powrprof.dll,SetSuspendState 0,1,0", "Sending computer to sleep");
            case "lock computer", "lock screen" -> new PowerCommand("rundll32.exe user32.dll,LockWorkStation", "Locking workstation");

            // Fun / Extra (Clear Recycle Bin now works without admin)
            case "open god mode" -> new SystemCommand("explorer shell:::{ED7BA470-8E54-465E-825C-99712043E01C}", "God Mode activated!");
            case "clear recycle bin" -> new SystemCommand("PowerShell -Command \"Clear-RecycleBin -Force\"", "Clearing Recycle Bin");

            default -> null;
        };
    }
}