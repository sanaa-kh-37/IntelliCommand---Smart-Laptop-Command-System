package com.example.commandinterpreter.service;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingService {
    private static final Logger LOGGER = Logger.getLogger(LoggingService.class.getName());

    public void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }

    public void logError(String message, Throwable t) {
        LOGGER.log(Level.SEVERE, message, t);
    }
}