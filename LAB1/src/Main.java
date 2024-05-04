import Data.Argument;
import Parser.Parser;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Parser parser = new Parser();
        ArrayList<Argument> argList;
        if (args.length > 0) {
            argList = parser.parse(args[0]);
        } else {
            argList = parser.parse();
        }
        CalcIterator calcIterator = new CalcIterator(argList);
        calcIterator.calculation();
    }
}
