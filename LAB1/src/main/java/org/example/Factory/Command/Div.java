package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

public class Div implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        var firstOp = Double.valueOf(context.pop());
        var secondOp = Double.valueOf(context.pop());
        double result = secondOp / firstOp;
        context.push(Double.toString(result));
    }
}
