package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

import static java.lang.Math.sqrt;

public class Sqrt implements Command {

    @Override
    public void doOperation(Context context, Argument args) throws Exception {
        try {
            var firstOp = context.pop();
            var result = sqrt(Double.parseDouble(firstOp));
            context.push(Double.toString(result));
        } catch (Exception e) {
            throw new Exception();
        }
    }
}
