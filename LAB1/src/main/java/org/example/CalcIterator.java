package org.example;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Debug.ColorLogger;
import org.example.Factory.Command.Command;
import org.example.Factory.CommandFactory;

import java.util.ArrayList;
import java.util.TreeMap;

public class CalcIterator {
    private ColorLogger log = new ColorLogger();

    private final ArrayList<Argument> argList;
    private final Context context = new Context();

    private final TreeMap<String, Command> commandList = new TreeMap<>();

    public CalcIterator(ArrayList<Argument> argList) {
        this.argList = argList;
    }

    public void calculation() {
        var commandFactory = new CommandFactory();
        log.logInfo("Start calculation...");
        for (Argument argument : argList) {
            if (!commandList.containsKey(argument.getCommand())) {
                var command = commandFactory.createCommand(argument.getCommand());
                commandList.put(argument.getCommand(), command);
            }
            try {
                commandList.get(argument.getCommand()).doOperation(context, argument);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
