package org.example.Parser;

import org.example.CalcIterator;
import org.example.Data.Argument;
import org.example.Debug.ColorLogger;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Parser {
    private ColorLogger log = new ColorLogger();
    private Scanner in;
    private BufferedReader bufferedReader;
    private int MODE;
    private final int MODE_COMMAND_LINE = 1;
    private final int MODE_FILE = 2;
    private final ArrayList<Argument> argumentList = new ArrayList<>();

    private void parseFile(String filePath) {
        log.logInfo("Start parse command file...");
        try (InputStreamReader inputStream = new InputStreamReader(new FileInputStream(filePath))) {
            bufferedReader = new BufferedReader(inputStream);
        } catch (Exception e) {
            log.logError("PARSER: Error while reading file - " + e.getMessage());
        }
    }

    public void parse() {
        MODE = MODE_COMMAND_LINE;
        parseIS();
    }

    public Argument getNextArg() {
        String curStr;
        Argument arg = null;
        if (MODE == MODE_COMMAND_LINE) {
            if (!(curStr = in.nextLine()).equals("EXIT")) {
                arg = editLine(curStr);
            } else {
                log.logInfo("Console parsing is complete");
            }
        } else {
            try {
                if ((curStr = bufferedReader.readLine()) != null)
                    arg = editLine(curStr);
                else {
                    log.logInfo("File parsing is complete");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return arg;
    }

    private void parseIS() {
        MODE = MODE_COMMAND_LINE;
        in = new Scanner(System.in);
        try {
            log.logInfo("Start parse console...");
            log.logInfo("Enter 'EXIT' to exit");
        } catch (Exception e) {
            System.err.println("Error while reading console: " + e.getMessage());
        }
    }

    public void parse(String fileName) {
        MODE = MODE_FILE;
        parseFile(fileName);
    }

    private Argument editLine(String curStr) {
        var str = curStr.split(" ");
        var list = new ArrayList<>(Arrays.stream(str).toList());
        var comm = list.getFirst();
        Argument argument = new Argument(comm);
        for (int i = 1; i < list.size(); ++i)
            argument.setArgs(list.get(i));
        return argument;
    }
}
