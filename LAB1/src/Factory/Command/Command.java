package Factory.Command;


import Data.Argument;
import Data.Context;

public interface Command {
    void doOperation(Context context, Argument args);
}
