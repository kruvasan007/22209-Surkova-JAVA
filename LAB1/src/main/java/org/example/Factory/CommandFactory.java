package org.example.Factory;


import org.example.Debug.ColorLogger;
import org.example.Factory.Command.Command;

public class CommandFactory {
    private ColorLogger log = new ColorLogger();

    public Command createCommand(String pathToCommandClass) {
        Command command = null;
        try {
            command = (Command) Class.forName(pathToCommandClass).getConstructors()[0].newInstance();
            log.logInfo("Create command: " + command.getClass().getName());
        } catch (Exception e) {
            log.logError("Error in command class factory: " + e.getLocalizedMessage());
        }
        return command;
    }
}
