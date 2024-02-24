package org.example.Data;

import java.util.ArrayList;

public class Argument {
    private final String commandPath;
    private final ArrayList<String> args = new ArrayList<>();

    public Argument(String command) {
        this.commandPath = command;
    }

    public String getCommand() {
        return commandPath;
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
