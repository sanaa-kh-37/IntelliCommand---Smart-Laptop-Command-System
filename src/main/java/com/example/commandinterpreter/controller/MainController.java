package com.example.commandinterpreter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {
    @FXML
    private TextField commandInput;

    @FXML
    private TextArea outputArea;

    private Map<String, String> commandMap = new HashMap<>();

    public MainController() {
        // Initialize 10 command mappings (Windows-specific; adjust for other OS)
        commandMap.put("open chrome", "start chrome");
        commandMap.put("open notepad", "notepad");
        commandMap.put("open explorer", "explorer");
        commandMap.put("shutdown", "shutdown /s /t 0");
        commandMap.put("restart", "shutdown /r /t 0");
        commandMap.put("open calculator", "calc");
        commandMap.put("open paint", "mspaint");
        commandMap.put("lock screen", "rundll32.exe user32.dll,LockWorkStation");
        commandMap.put("open cmd", "cmd");
        commandMap.put("open task manager", "taskmgr");

        commandMap.put("open chrome", "start chrome");
        commandMap.put("open firefox", "start firefox");
        commandMap.put("open edge", "start msedge");
        commandMap.put("open notepad", "notepad");
        commandMap.put("open calculator", "calc");
        commandMap.put("open paint", "mspaint");
        commandMap.put("open task manager", "taskmgr");
        commandMap.put("open command prompt", "cmd");
        commandMap.put("open powershell", "powershell");

        // System & Explorer
        commandMap.put("open file explorer", "explorer");
        commandMap.put("open this pc", "explorer");
        commandMap.put("open desktop", "explorer shell:Desktop");
        commandMap.put("open downloads", "explorer shell:Downloads");
        commandMap.put("open documents", "explorer shell:Documents");
        commandMap.put("open pictures", "explorer shell:Pictures");
        commandMap.put("open control panel", "control");

        // Websites (Opens in default browser)
        commandMap.put("open youtube", "start https://www.youtube.com");
        commandMap.put("open google", "start https://www.google.com");
        commandMap.put("open gmail", "start https://mail.google.com");
        commandMap.put("open whatsapp", "start https://web.whatsapp.com");
        commandMap.put("open netflix", "start https://www.netflix.com");
        commandMap.put("open github", "start https://github.com");
        commandMap.put("open chatgpt", "start https://chat.openai.com");

        // Fun & Useful System Commands
        commandMap.put("lock computer", "rundll32.exe user32.dll,LockWorkStation");
        commandMap.put("open settings", "start ms-settings:");
        commandMap.put("open wifi settings", "start ms-settings:network-wifi");
        commandMap.put("open sound settings", "start ms-settings:sound");
        commandMap.put("open volume mixer", "sndvol");
        commandMap.put("clear recycle bin", "PowerShell Clear-RecycleBin -Force");

        // Power Options (with confirmation recommended in real apps)
        commandMap.put("shutdown pc", "shutdown /s /t 10"); // 10 sec delay
        commandMap.put("restart pc", "shutdown /r /t 10");
        commandMap.put("sleep pc", "rundll32.exe powrprof.dll,SetSuspendState 0,1,0");

        // Bonus Creative Ones
        commandMap.put("open god mode", "explorer shell:::{ED7BA470-8E54-465E-825C-99712043E01C}");
        commandMap.put("open snipping tool", "start ms-screenclip:");
        commandMap.put("open emoji picker", "start ms-keyboard:");
    }

    @FXML
    private void handleExecute(ActionEvent event) {
        String userCommand = commandInput.getText().trim().toLowerCase();
        String osCommand = commandMap.get(userCommand);

        outputArea.appendText("> " + commandInput.getText() + "\n"); // Echo input

        if (osCommand != null) {
            // Determine spoken feedback (short & clear for best quality)
            String spokenMessage = switch (userCommand) {
                case "open chrome" -> "Opening Chrome";
                case "open firefox" -> "Opening Firefox";
                case "open edge" -> "Opening Edge";
                case "open notepad" -> "Opening Notepad";
                case "open calculator" -> "Opening Calculator";
                case "open paint" -> "Opening Paint";
                case "open task manager" -> "Opening Task Manager";
                case "open command prompt" -> "Opening Command Prompt";
                case "open powershell" -> "Opening PowerShell";

                case "open file explorer", "open this pc" -> "Opening File Explorer";
                case "open desktop" -> "Opening Desktop";
                case "open downloads" -> "Opening Downloads";
                case "open documents" -> "Opening Documents";
                case "open pictures" -> "Opening Pictures";
                case "open control panel" -> "Opening Control Panel";

                case "open youtube" -> "Opening YouTube. Enjoy!";
                case "open google" -> "Opening Google";
                case "open gmail" -> "Opening Gmail";
                case "open whatsapp" -> "Opening WhatsApp Web";
                case "open netflix" -> "Opening Netflix. Time to relax!";
                case "open github" -> "Opening GitHub. Happy coding!";
                case "open chatgpt" -> "Opening ChatGPT";

                case "lock computer" -> "Locking computer now. Goodbye!";
                case "open settings" -> "Opening Settings";
                case "open wifi settings" -> "Opening Wi-Fi Settings";
                case "open sound settings" -> "Opening Sound Settings";
                case "open volume mixer" -> "Opening Volume Mixer";
                case "clear recycle bin" -> "Clearing Recycle Bin";

                case "shutdown pc" -> "Shutting down in one minute";
                case "restart pc" -> "Restarting in one minute";
                case "sleep pc" -> "Going to sleep. Good night!";

                case "open god mode" -> "God Mode activated!";
                case "open snipping tool" -> "Opening Snipping Tool";
                case "open emoji picker" -> "Opening Emoji Picker";

                default -> "Command executed";
            };

            // Visual feedback immediately
            outputArea.appendText("✔ " + spokenMessage + "\n\n");

            // STEP 1: SPEAK FIRST — Run speech in background thread (starts instantly)
            new Thread(() -> speakText(spokenMessage)).start();

            // STEP 2: THEN EXECUTE COMMAND — Happens right after speech starts
            try {
                new ProcessBuilder("cmd.exe", "/c", osCommand).start();
            } catch (IOException e) {
                outputArea.appendText("❌ Error executing command\n\n");
                new Thread(() -> speakText("Error executing command")).start();
            }

        } else {
            outputArea.appendText("❌ Unknown command: " + userCommand + "\n");
            outputArea.appendText("💡 Try 'open youtube' or 'open file explorer'\n\n");
            new Thread(() -> speakText("Sorry, I don't know that command")).start();
        }

        commandInput.clear();
    }

    private void speakText(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String cleanText = text.replace("'", "''").replace("\"", "\\\"");

        String speakCommand = "PowerShell -Command \"Add-Type -AssemblyName System.Speech; " +
                "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                "$synth.Rate = 2; " +                     // Slightly faster (clear & natural)
                "$synth.Volume = 100; " +
                "$synth.SelectVoiceByHints('Female'); " + // Best female voice available
                "$synth.Speak('" + cleanText + "')\"";

        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", speakCommand);
            pb.inheritIO(); // Reduces overhead
            Process process = pb.start();
            // DO NOT waitFor() here — lets speech run freely in background
        } catch (IOException e) {
            // Silent fail
        }
    }
}