import Data.Argument;
import Data.Context;
import Factory.CommandFactory;
import Parser.Parser;

import java.util.ArrayList;

public class CalcIterator {
    private ArrayList<Argument> argList;
    private Context context = new Context();

    public CalcIterator(ArrayList<Argument> argList) {
        this.argList = argList;
    }

    public void calculation(){
        var commandFactory = new CommandFactory();
        for (Argument argument : argList) {
            var command = commandFactory.createCommand(argument.getCommand());
            command.doOperation(context, argument);
        }
    }
}
