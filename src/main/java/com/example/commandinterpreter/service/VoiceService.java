package com.example.commandinterpreter.service;

import java.io.IOException;

// Encapsulation: All speech logic hidden here
public class VoiceService {
    public void speakText(String text) {
        if (text == null || text.trim().isEmpty()) return;

        String cleanText = text.replace("'", "''").replace("\"", "\\\"");

        String speakCommand = "PowerShell -Command \"Add-Type -AssemblyName System.Speech; " +
                "$synth = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                "$synth.Rate = 2; $synth.Volume = 100; " +
                "$synth.SelectVoiceByHints('Female'); $synth.Speak('" + cleanText + "')\"";

        try {
            new ProcessBuilder("cmd.exe", "/c", speakCommand).inheritIO().start();
        } catch (IOException e) {
            // Silent
        }
    }
}