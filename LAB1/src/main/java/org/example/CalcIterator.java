package org.example;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Debug.ColorLogger;
import org.example.Factory.CommandFactory;

import java.util.ArrayList;

public class CalcIterator {
    private ColorLogger log = new ColorLogger();
    private final ArrayList<Argument> argList;
    private final String CONFIG_PATH = "C:\\Users\\Anastasia\\IdeaProjects\\Calculator\\22209-Surkova-JAVA\\LAB1\\src\\main\\resources\\config.config";
    private final Context context = new Context();

    public CalcIterator(ArrayList<Argument> argList) {
        this.argList = argList;
    }

    public void calculation() {
        var commandFactory = new CommandFactory(CONFIG_PATH);
        log.logInfo("Start calculation...");
        for (Argument argument : argList) {
            try {
                commandFactory.createCommand(argument.getCommandName()).doOperation(context, argument);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
