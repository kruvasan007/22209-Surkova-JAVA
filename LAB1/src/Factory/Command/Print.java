package Factory.Command;

import Data.Argument;
import Data.Context;

public class Print implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        System.out.println(context.peek());
    }
}