package org.example.Factory;

import org.example.CalcIterator;
import org.example.Exception.FabricException;
import org.example.Parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandFactoryTest {

    @Test
    void createCommandTest() {
        var comFactory = new CommandFactory("src/main/resources/config.config");
        try {
            comFactory.createCommand("org.example.Factory.Command.Pop");
        } catch (FabricException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createCommandTestException() {
        var comFactory = new CommandFactory("src/main/resources/config.config");
        try {
            comFactory.createCommand("org.example.Factory.Command");
        } catch (FabricException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testInput() {
        Parser parser = new Parser();
        try {
            parser.create("src/main/resources/input.txt");
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        CalcIterator calcIterator = new CalcIterator(parser);
        calcIterator.calculation();
    }

    @Test
    void testInputException() {
        Parser parser = new Parser();
        try {
            parser.create("src/main/resources/inputWrong.txt");
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        CalcIterator calcIterator = new CalcIterator(parser);
        calcIterator.calculation();
    }

    @Test
    void parseConfigException() {
        Assertions.assertThrows(RuntimeException.class, () -> new CommandFactory("src/main/resources/config.confi"));
    }
}