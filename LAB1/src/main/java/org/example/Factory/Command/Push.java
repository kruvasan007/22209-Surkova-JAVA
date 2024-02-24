package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

public class Push implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        try {
            context.push(args.getArgs(0));
        } catch (Exception e) {
            throw new Exception("Arguments array is empty");
        }
    }
}
