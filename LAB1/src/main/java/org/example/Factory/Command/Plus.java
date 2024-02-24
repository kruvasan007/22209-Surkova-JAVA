package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

import java.util.EmptyStackException;

public class Plus implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        var firstOp = Double.valueOf(context.pop());
        var secondOp = Double.parseDouble(context.pop());
        double result = firstOp + secondOp;
        context.push(Double.toString(result));
    }
}
