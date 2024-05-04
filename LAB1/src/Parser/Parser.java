package Parser;

import Data.Argument;
import Factory.CommandFactory;
import Factory.CommandType;

import java.io.*;
import java.util.*;

public class Parser {
    private final String CONFIG_FILE_NAME = "config.config";
    private final ArrayList<Argument> argumentList = new ArrayList<>();
    private final TreeMap<String, CommandType> config = new TreeMap<>();

    private void parseConfig() {
        for (CommandType commandType : CommandType.values()) {
            config.put(commandType.toString().toUpperCase(), commandType);
        }
        /*try {
            var configStream = new InputStreamReader(Parser.class.getResourceAsStream(CONFIG_FILE_NAME));

            BufferedReader bufferedReader = new BufferedReader(configStream);
            String curStr;
            while ((curStr = bufferedReader.readLine()) != null) {
                String[] str = curStr.split(",");
                var list = new ArrayList<>(Arrays.stream(str).toList());
                System.out.println(str[0] + " " + str[1]);
                config.put(list.get(0), CommandType.);
            }
        } catch (Exception e) {
            System.out.println("Error while open config " + e.getMessage());
        }*/
    }

    private void parseFile(String fileName) {
        InputStreamReader inputStream = null;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            File file = new File(classLoader.getResource(fileName).getFile());
            inputStream = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStream);
            String curStr;

            while ((curStr = bufferedReader.readLine()) != null)
                editLine(curStr);

        } catch (Exception e) {
            System.err.println("Error while reading file: " + e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ArrayList<Argument> parse(){
        parseConfig();
        parseIS();
        return argumentList;
    }

    private void parseIS() {
        Scanner in = new Scanner(System.in);
        try {
            String curStr;
            while (!(curStr = in.nextLine()).equals("EXIT"))
                editLine(curStr);
        } catch (Exception e) {
            System.err.println("Error while reading console: " + e.getMessage());
        }
    }

    public ArrayList<Argument> parse(String fileName) {
        parseConfig();
        parseFile(fileName);
        return argumentList;
    }

    private void editLine(String curStr) {
        var str = curStr.split(" ");
        var list = new ArrayList<>(Arrays.stream(str).toList());
        var comm = config.get(list.getFirst());
        if (comm == CommandType.Unknown) {
            System.out.println("ERROR CommandType: unknown command");
        } else {
            Argument argument = new Argument(comm);
            for (int i = 1; i < list.size(); ++i)
                argument.setArgs(list.get(i));
            argumentList.add(argument);
        }
    }
}
