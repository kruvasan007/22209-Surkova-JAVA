package org.example;

import org.example.Data.Argument;
import org.example.Parser.Parser;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        try {
            if (args.length > 0)
                parser.parse(args[0]);
            else
                parser.parse();
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        CalcIterator calcIterator = new CalcIterator(parser);
        calcIterator.calculation();
    }
}
