package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

public class Min implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        try {
            var firstOp = Double.valueOf(context.pop());
            var secondOp = Double.valueOf(context.pop());
            var result = secondOp - firstOp;
            context.push(Double.toString(result));
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}
