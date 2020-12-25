package cbbot.common.exceptions;

public class CommandNotFoundException extends Exception {
	public CommandNotFoundException(String commandName) {
		super(String.format("No such command: %s", commandName));
	}
}
