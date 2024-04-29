package org.example.Factory.Command;


import org.example.Data.Argument;
import org.example.Data.Context;
import org.example.Exception.CommandException;

public interface Command {
    void doOperation(Context context, Argument args) throws CommandException;
}
