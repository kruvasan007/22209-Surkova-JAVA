package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

public class Div implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws CommandException {
        var firstOp = context.pop();
        var secondOp = context.pop();
        double result = secondOp / firstOp;
        context.push(result);
    }
}
