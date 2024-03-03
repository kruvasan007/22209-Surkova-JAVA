package Factory;


import Factory.Command.Command;

public class CommandFactory {
    private final String PACKAGE_NAME = this.getClass().getPackageName() + ".Command.";

    public Command createCommand(CommandType comType) {
        Command command = null;
        try {
            command = (Command) Class.forName(PACKAGE_NAME + comType).newInstance();
        } catch (Exception e) {
            System.out.println("Error: created class");
        }
        return command;
    }
}
