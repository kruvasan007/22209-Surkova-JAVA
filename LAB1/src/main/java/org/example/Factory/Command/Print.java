package org.example.Factory.Command;

import org.example.Data.Argument;
import org.example.Data.Context;

public class Print implements Command {

    @Override
    public void doOperation(Context context, Argument args) {
        System.out.println(context.peek());
    }
}