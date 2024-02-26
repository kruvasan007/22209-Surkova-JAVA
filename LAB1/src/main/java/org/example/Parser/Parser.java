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
    private final ArrayList<Argument> argumentList = new ArrayList<>();

    private void parseFile(String filePath) {
        try (InputStreamReader inputStream = new InputStreamReader(new FileInputStream(filePath))) {
            log.logInfo("Start parse command file...");
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String curStr;
            while ((curStr = bufferedReader.readLine()) != null)
                editLine(curStr);
        } catch (Exception e) {
            log.logError("PARSER: Error while reading file - " + e.getMessage());
        }
    }

    public ArrayList<Argument> parse() {
        parseIS();
        return argumentList;
    }

    private void parseIS() {
        Scanner in = new Scanner(System.in);
        try {
            log.logInfo("Start parse console...");
            log.logInfo("Enter 'EXIT' to exit");
            String curStr;
            while (!(curStr = in.nextLine()).equals("EXIT"))
                editLine(curStr);
            log.logInfo("Console parsing is complete");
        } catch (Exception e) {
            System.err.println("Error while reading console: " + e.getMessage());
        }
    }

    public ArrayList<Argument> parse(String fileName) {
        parseFile(fileName);
        return argumentList;
    }

    private void editLine(String curStr) {
        var str = curStr.split(" ");
        var list = new ArrayList<>(Arrays.stream(str).toList());
        var comm = list.getFirst();
        Argument argument = new Argument(comm);
        for (int i = 1; i < list.size(); ++i)
            argument.setArgs(list.get(i));
        argumentList.add(argument);
    }
}
