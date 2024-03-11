package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

public class Define implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws CommandException {
        try {
            context.setParams(args.getArgs(0), Double.valueOf(args.getArgs(1)));
        } catch (Exception e){
            throw new CommandException("Arguments array is empty");
        }
    }
}
