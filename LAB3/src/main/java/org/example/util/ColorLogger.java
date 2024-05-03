package org.example.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class ColorLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(
            ColorLogger.class);


    public void logInfo(String logging) {
        LOGGER.info("\u001B[32m {}: " + logging + "\u001B[0m", LocalDateTime.now());
    }

    public void logError(String logging) {
        LOGGER.error("\u001B[31m {}: " + logging + "\u001B[0m", LocalDateTime.now());
    }
}
