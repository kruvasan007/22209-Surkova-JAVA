package org.example.Factory;


import org.example.Debug.ColorLogger;
import org.example.Factory.Command.Command;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import java.util.TreeMap;

public class CommandFactory {
    private ColorLogger log = new ColorLogger();

    private final Properties properties = new Properties();

    private final TreeMap<String, Command> commandList = new TreeMap<>();

    public CommandFactory(String inputFilePath) {
        try (InputStreamReader configStream = new InputStreamReader(new FileInputStream(inputFilePath))) {
            properties.load(configStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Command createCommand(String commandName) {
        Command command = null;
        if (commandList.containsKey(commandName)) {
            return commandList.get(commandName);
        } else {
            if (properties.get(commandName) != null) {
                try {
                    command = (Command) Class.forName(properties.get(commandName).toString()).getConstructors()[0].newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                commandList.put(commandName, command);
                log.logInfo("Create command: " + command.getClass().getName());
            } else {
                log.logError("Create command: no this command in config");
            }
        }
        return command;
    }
}
