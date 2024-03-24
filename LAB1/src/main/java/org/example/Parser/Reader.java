package org.example.Parser;

import org.example.Debug.ColorLogger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Reader {
    private ColorLogger log = new ColorLogger();
    private Scanner in;
    private BufferedReader bufferedReader;
    private int MODE;
    private final int MODE_COMMAND_LINE = 1;
    private final int MODE_FILE = 2;

    public void createInstance(String filePath) throws Exception {
        MODE = MODE_FILE;
        try {
            bufferedReader = new BufferedReader(new FileReader(filePath));
            log.logInfo("Open command file...");
        } catch (Exception e) {
            log.logError("PARSER: Error while reading file - " + e.getMessage());
            throw new Exception(e);
        }
    }

    public void createInstance() {
        MODE = MODE_COMMAND_LINE;
        in = new Scanner(System.in);
        log.logInfo("Open console...");
        log.logInfo("Enter 'EXIT' to exit");
    }

    public String getNextLine() {
        String curStr;
        if (MODE == MODE_COMMAND_LINE) {
            if ((curStr = in.nextLine()).equals("EXIT")) {
                log.logInfo("Console parsing is complete");
            }
        } else {
            try {
                if ((curStr = bufferedReader.readLine()) == null) {
                    log.logInfo("File parsing is complete");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return curStr;
    }
}
