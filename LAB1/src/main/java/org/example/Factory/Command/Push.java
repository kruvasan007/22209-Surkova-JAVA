package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

public class Push implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        try {
            if (context.checkParams(args.getArgs(0))) {
                context.pushArg(args.getArgs(0));
            } else
                if(!Character.isLetter(args.getArgs(0).toCharArray()[0])){
                    context.push(Double.parseDouble(args.getArgs(0)));
                } else {
                    context.pushArg(args.getArgs(0));
                }
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Exception("Arguments array is empty");
        }
    }
}
