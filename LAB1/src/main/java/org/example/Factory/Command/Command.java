package org.example.Factory.Command;


import org.example.Data.Argument;
import org.example.Data.Context;

public interface Command {
    void doOperation(Context context, Argument args) throws Exception;
}
