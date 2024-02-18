package Data;

import Factory.CommandType;

import java.util.ArrayList;

public class Argument {
    private final CommandType command;
    private final ArrayList<String> args = new ArrayList<>();

    public Argument(CommandType command) {
        this.command = command;
    }

    public CommandType getCommand() {
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
