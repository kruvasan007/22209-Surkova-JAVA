package Factory.Command;

import Data.Argument;
import Data.Context;

public class Define implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
    if(args.getSize() >= 2){
            context.setParams(args.getArgs(0), Double.valueOf(args.getArgs(1)));
        } else {
            System.out.println("Error: define Args is empty");
        }
    }
}
