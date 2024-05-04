package Factory.Command;

import Data.Argument;
import Data.Context;

import java.util.EmptyStackException;

public class Pop implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        try {
            if (args.getSize() != 0) {
                context.setParams(args.getArgs(0), Double.valueOf(context.pop()));
            } else {
                context.pop();
            }
        } catch (EmptyStackException e) {
            System.out.println("Stack err: " + e.getLocalizedMessage());
        }
    }
}
