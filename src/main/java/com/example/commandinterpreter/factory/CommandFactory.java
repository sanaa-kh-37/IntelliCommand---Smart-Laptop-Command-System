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
    private final Map<String, Command> cache = new HashMap<>();
    private final ConfigLoader configLoader = new ConfigLoader(); // For future JSON extensions

    public Command getCommand(String userInput) {
        String key = userInput.toLowerCase().trim();
        return cache.computeIfAbsent(key, this::createCommand);
    }

    // Add this method for autocomplete
    public List<String> getAllKnownCommands() {
        return new ArrayList<>(cache.keySet()); // Returns List<String>
    }

    private Command createCommand(String key) {
        // First try dynamic commands from JSON (if any)
        Command dynamic = configLoader.loadCommand(key);
        if (dynamic != null) return dynamic;

        // Built-in safe commands (all lowercase)
        return switch (key) {
            // === Apps & Tools ===
            case "open notepad" -> new SystemCommand("notepad", "Opening Notepad");
            case "open calculator", "open calc" -> new SystemCommand("calc", "Opening Calculator");
            case "open paint", "open mspaint" -> new SystemCommand("mspaint", "Opening Paint");
            case "open task manager", "open taskmgr" -> new SystemCommand("taskmgr", "Opening Task Manager");
            case "open command prompt", "open cmd" -> new SystemCommand("cmd", "Opening Command Prompt");
            case "open powershell" -> new SystemCommand("powershell", "Opening PowerShell");
            case "open snipping tool", "take screenshot" -> new SystemCommand("snippingtool", "Opening Snipping Tool");
            case "open snip and sketch" -> new SystemCommand("ms-screenclip:", "Opening Snip & Sketch");
            case "open emoji picker", "open emojis" -> new SystemCommand("win + .", "Opening Emoji Picker (press Win + .)");

            // === File Explorer Locations ===
            case "open file explorer", "open this pc", "open my computer" -> new SystemCommand("explorer", "Opening File Explorer");
            case "open desktop" -> new SystemCommand("explorer shell:Desktop", "Opening Desktop");
            case "open documents", "open my documents" -> new SystemCommand("explorer shell:Documents", "Opening Documents");
            case "open downloads" -> new SystemCommand("explorer shell:Downloads", "Opening Downloads");
            case "open pictures", "open my pictures" -> new SystemCommand("explorer shell:Pictures", "Opening Pictures");
            case "open music", "open my music" -> new SystemCommand("explorer shell:Music", "Opening Music");
            case "open videos" -> new SystemCommand("explorer shell:Videos", "Opening Videos");
            case "open recycle bin" -> new SystemCommand("explorer shell:RecycleBinFolder", "Opening Recycle Bin");

            // === System Settings ===
            case "open settings" -> new SystemCommand("ms-settings:", "Opening Windows Settings");
            case "open display settings" -> new SystemCommand("ms-settings:display", "Opening Display Settings");
            case "open sound settings", "open audio settings" -> new SystemCommand("ms-settings:sound", "Opening Sound Settings");
            case "open wifi settings", "open wi-fi settings" -> new SystemCommand("ms-settings:network-wifi", "Opening Wi-Fi Settings");
            case "open bluetooth settings" -> new SystemCommand("ms-settings:bluetooth", "Opening Bluetooth Settings");
            case "open battery settings" -> new SystemCommand("ms-settings:powersleep", "Opening Power & Battery Settings");
            case "open storage settings" -> new SystemCommand("ms-settings:storagesense", "Opening Storage Settings");
            case "open apps settings", "open installed apps" -> new SystemCommand("ms-settings:appsfeatures", "Opening Installed Apps");

            // === Control Panel Classics ===
            case "open control panel" -> new SystemCommand("control", "Opening Control Panel");
            case "open devices and printers" -> new SystemCommand("control printers", "Opening Devices and Printers");
            case "open programs and features" -> new SystemCommand("appwiz.cpl", "Opening Programs and Features");
            case "open system info", "about my pc" -> new SystemCommand("msinfo32", "Opening System Information");
            case "open device manager" -> new SystemCommand("devmgmt.msc", "Opening Device Manager");
            case "open disk management" -> new SystemCommand("diskmgmt.msc", "Opening Disk Management");

            // === Browsers & Websites (Safe) ===
            case "open chrome" -> new SystemCommand("chrome", "Opening Google Chrome");
            case "open firefox" -> new SystemCommand("firefox", "Opening Firefox");
            case "open edge" -> new SystemCommand("msedge", "Opening Microsoft Edge");

            case "open youtube" -> new BrowserCommand("https://www.youtube.com", "YouTube");
            case "open google" -> new BrowserCommand("https://www.google.com", "Google");
            case "open gmail" -> new BrowserCommand("https://mail.google.com", "Gmail");
            case "open whatsapp", "open whatsapp web" -> new BrowserCommand("https://web.whatsapp.com", "WhatsApp Web");
            case "open netflix" -> new BrowserCommand("https://www.netflix.com", "Netflix");
            case "open github" -> new BrowserCommand("https://github.com", "GitHub");
            case "open chatgpt" -> new BrowserCommand("https://chat.openai.com", "ChatGPT");

            // === Admin-only Power Commands (only if user is ADMIN) ===
            case "shutdown pc", "turn off computer" -> new PowerCommand("shutdown /s /t 10", "Shutting down in 10 seconds");
            case "restart pc", "reboot computer" -> new PowerCommand("shutdown /r /t 10", "Restarting in 10 seconds");
            case "sleep pc", "put computer to sleep" -> new PowerCommand("rundll32.exe powrprof.dll,SetSuspendState 0,1,0", "Sending computer to sleep");
            case "lock computer", "lock screen" -> new PowerCommand("rundll32.exe user32.dll,LockWorkStation", "Locking workstation");

            // === Fun Extras ===
            case "open god mode" -> new SystemCommand("explorer shell:::{ED7BA470-8E54-465E-825C-99712043E01C}", "God Mode activated!");
            case "clear recycle bin" -> new PowerCommand("PowerShell -Command \"Clear-RecycleBin -Force\"", "Clearing Recycle Bin");
            case "open volume mixer" -> new SystemCommand("sndvol", "Opening Volume Mixer");

            default -> null; // Unknown command
        };
    }
}