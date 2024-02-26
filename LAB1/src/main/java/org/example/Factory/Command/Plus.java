package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

import java.util.EmptyStackException;

public class Plus implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        var firstOp = context.pop();
        var secondOp = context.pop();
        Double result = firstOp + secondOp;
        context.push(result);
    }
}
