package org.example;

import org.example.Data.Argument;
import org.example.Exception.FabricException;
import org.example.Parser.Parser;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        try (Parser parser = new Parser()) {
            if (args.length > 0)
                parser.create(args[0]);
            else
                parser.create();
            CalcIterator calcIterator = new CalcIterator(parser);
            calcIterator.calculation();
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
    }
}
