package Factory.Command;

import Data.Argument;
import Data.Context;

public class Min implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        try {
            var firstOp = Double.valueOf(context.pop());
            var secondOp = Double.valueOf(context.pop());
            var result = secondOp - firstOp;
            context.push(Double.toString(result));
        } catch (ArithmeticException e) {
            System.out.println("Error: ArithmeticException" + e.getLocalizedMessage());
        }
    }
}
