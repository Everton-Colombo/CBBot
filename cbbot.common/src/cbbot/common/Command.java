package cbbot.common;

import javax.naming.NoPermissionException;
import java.io.Serializable;
import java.util.function.Function;

/**
 * <p>The {@link Command} class contains all the information regarding a command:</p>
 * <ul>
 *     <li>
 *         <b>Name:</b> The command's name. This must be unique to each command
 *    		as it is used to identify the command.
 *     </li>
 *     <li>
 *         <b>Usage:</b> A helper string. Contains instructions on how the command should
 *         be used. Usually, it also contains information regarding the command's parameters.
 *         No pattern is enforced, however, this one is recommended:
 *         <i>{COMMAND_NAME} <{PARAM1_NAME}> <PARAM2_NAME> ...</i>
 *     </li>
 *     <li>
 *         <b>Description:</b> A brief description of the command.
 *     </li>
 *     <li>
 *         <b>Function:</b> A {@link Function<Message, String>} that should process the message passed.
 *         The message is sure to contain a valid command call ({PRE_COMMAND_CHAR}{COMMAND_NAME}), but
 *         it may not have valid parameters, and thus the function should perform the checking.
 *         It should return a {@link String} as a response, but that's optional: should the function
 *         not have a response, it shall return null;
 *     </li>
 * </ul>
 * <p>
 *     The {@link Command} class is serializable.
 * </p>
 */
public class Command implements Serializable {
	private static final long serialVersionUID = 31513131144L;

	protected String name;
	protected String usage;
	protected String description;
	protected Function<Message, String> function;

	/**
	 * <p>
	 *     Static inner class of {@link Command}. Contains information regarding a command's response:
	 * </p>
	 * <ul>
	 *     <li>
	 *         <b>Command Executed:</b> A reference to the {@link Command} that generated the response.
	 *     </li>
	 *     <li>
	 *         <b>Response:</b> The {@link String} returned by the command's function. May be null;
	 *     </li>
	 *     <li>
	 *         <b>Agent Message:</b> The {@link Message} that called the command.
	 *     </li>
	 * </ul>
	 */
	public static class CommandResponse {
		private final Command commandExecuted;
		private final String response;
		private final Message agentMessage;

		public CommandResponse(Command commandExecuted, String response, Message agentMessage) {
			this.commandExecuted = commandExecuted;
			this.response = response;
			this.agentMessage = agentMessage;
		}

		public Command getCommandExecuted() { return commandExecuted; }
		public String getResponse() { return response; }
		public Message getAgentMessage() { return agentMessage; }
	}

	/**
	 * {@link Command}'s constructor.
	 * @param name: Command's name; <b>ATTENTION:</b> The command's name must be unique to it.
	 * @param usage: Instructions on how to use the command. Should contain information regarding the command's parameters and their types.
	 * @param description: A brief description of what the command does.
	 * @param function: A {@link Function<Message, String>} that processes the {@link Message} and returns a {@link String}.
	 */
	public Command(String name, String usage, String description, Function<Message, String> function) {
		this.name = name;
		this.usage = usage;
		this.description = description;
		this.function = function;
	}

	/**
	 * Applies the message to the command's function and returns a {@link CommandResponse}.
	 * <b>WARNING:</b> The message must be checked beforehand for validity!
	 *
	 * @param message: The message to be applied to the function. Must be checked beforehand.
	 * @return a {@link CommandResponse} containing the response.
	 * @throws NoPermissionException: if the user does not have permission to execute the command.
	 */
	public CommandResponse execute(Message message) throws NoPermissionException {
		return new CommandResponse(this, function.apply(message), message);
	}

	public String getName() { return name; }
	public String getUsage() { return usage; }
	public String getDescription() { return description; }
	public Function<Message, String> getFunction() { return function; }

	@Override
	public int hashCode() {
		return function.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Command)) return false;
		return ((Command) obj).getFunction().equals(function);
	}

	@Override
	public String toString() {
		return "Command{" +
				"name='" + name + '\'' +
				", usage='" + usage + '\'' +
				", description='" + description + '\'' +
				", function=" + function +
				'}';
	}
}
