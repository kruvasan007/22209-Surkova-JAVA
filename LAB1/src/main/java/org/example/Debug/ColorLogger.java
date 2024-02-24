package org.example.Debug;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ColorLogger {

    private static final Logger LOGGER = Logger.getLogger(ColorLogger.class.getName());

    public void logInfo(String logging) {
        LOGGER.log(Level.INFO, "\u001B[32m" + logging + "\u001B[0m");
    }

    public void logError(String logging) {
        LOGGER.log(Level.WARNING, "\u001B[31m" + logging + "\u001B[0m");
    }
}