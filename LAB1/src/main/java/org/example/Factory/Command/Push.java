package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

public class Push implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws CommandException {
        try {
            if (context.checkParams(args.getArgs(0))) {
                context.pushArg(args.getArgs(0));
            } else
                if(!Character.isLetter(args.getArgs(0).toCharArray()[0])){
                    context.push(Double.parseDouble(args.getArgs(0)));
                } else {
                    context.pushArg(args.getArgs(0));
                }
        } catch (IndexOutOfBoundsException e) {
            throw new CommandException("Arguments array is empty");
        }
    }
}
