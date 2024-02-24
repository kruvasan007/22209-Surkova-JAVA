package org.example;

import org.example.Data.Argument;
import org.example.Parser.Parser;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        ArrayList<Argument> argList = null;
        try {
            if (args.length > 0)
                argList = parser.parse(args[0]);
            else
                argList = parser.parse();
        } catch (Exception e) {
            System.err.println("Parse error: " + e.getMessage());
        }
        if (argList != null) {
            CalcIterator calcIterator = new CalcIterator(argList);
            calcIterator.calculation();
        }
    }
}
