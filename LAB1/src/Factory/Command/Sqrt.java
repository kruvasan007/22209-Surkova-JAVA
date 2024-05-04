package Factory.Command;

import Data.Argument;
import Data.Context;

import static java.lang.Math.sqrt;

public class Sqrt implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        try {
            var firstOp = Double.valueOf(context.pop());
            var result = sqrt(firstOp);
            context.push(Double.toString(result));
        } catch (ArithmeticException e) {
            System.out.println("Error: ArithmeticException" + e.getLocalizedMessage());
        }
    }
}
