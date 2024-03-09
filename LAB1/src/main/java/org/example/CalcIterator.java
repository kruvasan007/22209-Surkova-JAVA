package org.example;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Debug.ColorLogger;
import org.example.Factory.CommandFactory;
import org.example.Parser.Parser;

import java.lang.reflect.AnnotatedArrayType;
import java.util.ArrayList;

public class CalcIterator {
    private Parser parser;
    private ColorLogger log = new ColorLogger();
    private final String CONFIG_PATH = "src/main/resources/config.config";
    private final Context context = new Context();

    public CalcIterator(Parser parser) {
        this.parser = parser;
    }

    public void calculation() {
        var commandFactory = new CommandFactory(CONFIG_PATH);
        log.logInfo("Start calculation...");
        Argument argument;
        while ((argument = parser.getNextArg()) != null) {
            try {
                commandFactory.createCommand(argument.getCommandName()).doOperation(context, argument);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
