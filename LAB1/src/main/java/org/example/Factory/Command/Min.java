package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

public class Min implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws CommandException {
        try {
            var firstOp = context.pop();
            var secondOp = context.pop();
            var result = secondOp - firstOp;
            context.push(result);
        } catch (Exception e) {
            throw new CommandException(e.getMessage());
        }
    }
}
