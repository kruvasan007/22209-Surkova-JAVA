package Factory.Command;

import Data.Argument;
import Data.Context;

public class Push implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        if (args.getSize() != 0) {
            context.push(args.getArgs(0));
        } else {
            System.out.println("Error: push Args is empty");
        }
    }
}
