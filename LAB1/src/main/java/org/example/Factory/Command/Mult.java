package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

import java.util.EmptyStackException;

public class Mult implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws CommandException {
        var firstOp = context.pop();
        var secondOp = context.pop();
        var result = firstOp * secondOp;
        context.push(result);
    }
}
