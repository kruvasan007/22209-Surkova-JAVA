package org.example.Data;

import java.util.ArrayList;

public class Argument {
    private final String command;
    private final ArrayList<String> args = new ArrayList<>();

    public Argument(String command) {
        this.command = command;
    }

    public String getCommandName() {
        return command;
    }

    public int getSize() {
        return args.size();
    }

    public String getArgs(int i) {
        return args.get(i);
    }

    public void setArgs(String arg) {
        this.args.add(arg);
    }
}
