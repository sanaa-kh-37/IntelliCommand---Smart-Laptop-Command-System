package com.example.commandinterpreter.command;

import com.example.commandinterpreter.model.User;
import java.io.IOException;


public interface Command {
    void execute(User user) throws IOException;
    String getSpokenFeedback();
}