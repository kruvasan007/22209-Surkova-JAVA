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
    private final String CONFIG_FILE_NAME = "config.config";
    private final ArrayList<Argument> argumentList = new ArrayList<>();
    private final TreeMap<String, String> config = new TreeMap<>();

    private void parseConfig() throws Exception {
        try {
            log.logInfo("Parsing arguments");
            File file = new File(getClass().getClassLoader().getResource(CONFIG_FILE_NAME).getFile());
            var configStream = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(configStream);
            String curStr;
            while ((curStr = bufferedReader.readLine()) != null) {
                String[] str = curStr.split(" ");
                var list = new ArrayList<>(Arrays.stream(str).toList());
                config.put(list.get(0), list.get(1));
            }
            log.logInfo("Argument parsing is complete");
        } catch (Exception e) {
            throw new Exception("Wrong config!");
        }
    }

    private void parseFile(String fileName) {
        InputStreamReader inputStream = null;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            log.logInfo("Start parse command file...");
            File file = new File(classLoader.getResource(fileName).getFile());
            inputStream = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String curStr;
            while ((curStr = bufferedReader.readLine()) != null)
                editLine(curStr);
        } catch (Exception e) {
            log.logError("PARSER: Error while reading file - " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    log.logInfo("Command file parsing is complete");
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Argument> parse() throws Exception {
        parseConfig();
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

    public ArrayList<Argument> parse(String fileName) throws Exception {
        parseConfig();
        parseFile(fileName);
        return argumentList;
    }

    private void editLine(String curStr) {
        var str = curStr.split(" ");
        var list = new ArrayList<>(Arrays.stream(str).toList());
        var comm = config.get(list.getFirst());
        Argument argument = new Argument(comm);
        for (int i = 1; i < list.size(); ++i)
            argument.setArgs(list.get(i));
        argumentList.add(argument);
    }
}
