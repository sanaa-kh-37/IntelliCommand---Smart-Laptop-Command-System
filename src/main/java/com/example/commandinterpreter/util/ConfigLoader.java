package com.example.commandinterpreter.util;

import com.example.commandinterpreter.command.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

// Encapsulation: Loads config; Abstraction for dynamic commands
public class ConfigLoader {
    private final Map<String, Command> dynamicCommands = new HashMap<>();

    public ConfigLoader() {
        loadFromJson(); // Load at init (scalable)
    }

    private void loadFromJson() {
        try (InputStream inputStream = getClass().getResourceAsStream("/com/example/commandinterpreter/commands.json")) {
            if (inputStream == null) {
                System.out.println("Warning: commands.json not found – no dynamic commands loaded");
                return; // Graceful exit – app continues without dynamic commands
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder json = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                json.append(line);
            }

            JSONArray array = new JSONArray(json.toString());
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String key = obj.getString("key");
                String type = obj.getString("type");
                String osCmd = obj.getString("osCommand");
                String feedback = obj.getString("feedback");
                // boolean admin = obj.getBoolean("admin"); // Uncomment if needed

                Command cmd;
                if ("browser".equals(type)) {
                    cmd = new BrowserCommand(osCmd, feedback);
                } else if ("power".equals(type)) {
                    cmd = new PowerCommand(osCmd, feedback);
                } else {
                    cmd = new SystemCommand(osCmd, feedback);
                }
                dynamicCommands.put(key, cmd);
            }
            System.out.println("Loaded " + dynamicCommands.size() + " dynamic commands from JSON.");
        } catch (Exception e) {
            System.err.println("Error loading commands.json:");
            e.printStackTrace();
        }
    }

    public Command loadCommand(String key) {
        return dynamicCommands.get(key);
    }
}