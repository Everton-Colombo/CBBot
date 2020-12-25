package cbbot.core;

import cbbot.common.Command;
import cbbot.common.Message;
import cbbot.common.exceptions.CommandNotFoundException;
import cbbot.common.exceptions.NotInitializedException;

import javax.naming.NoPermissionException;
import java.util.LinkedHashMap;

/**
 * The abstract definition of a ControllerBasedBot.
 */
public abstract class CBBot {

	protected MessagingController controller;
	protected String preCommandChar;
	protected LinkedHashMap<String, Command> commandList = new LinkedHashMap<>();

	protected Message latestMessage;

	protected BotEventListener eventListener;

	public CBBot(MessagingController controller, String preCommandChar) {
		this.controller = controller;
		this.preCommandChar = preCommandChar;
	}

	public CBBot(MessagingController controller, String preCommandChar, BotEventListener eventListener) {
		this(controller, preCommandChar);
		this.eventListener = eventListener;
	}

	protected void addCommands(Command ... commands) {
		for(Command command : commands) {
			commandList.put(command.getName(), command);
		}
	}

	public void initialize() {
		controller.initialize(10000);

		if(eventListener != null) { eventListener.onInitialized(); }
	}

	public void operate() {
		if(eventListener != null) { eventListener.onOperate(); }
	}

	public void terminate() {
		try {
			controller.terminate();
		} catch (NotInitializedException e) {
			e.printStackTrace();
		} finally {
			if(eventListener != null) { eventListener.onTerminated(); }
		}
	}

	/**
	 * <b>Helper Function:</b>
	 * <p>
	 *     Performs all the basic bot functions:
	 *     <ul>
	 *         <li>
	 *             Checks for new messages and updates the latest message property;
	 *         </li>
	 *         <li>
	 *             Checks if the latest received message is invoking a command, and if so, searches the bot's commandList for it and, if found, executes it;
	 *         </li>
	 *     </ul>
	 * </p>
	 * @return a {@link Command.CommandResponse} containing the {@link Command} object executed, it's response, and the {@link Message} object that generated the execution.
	 * @throws CommandNotFoundException: if the command invoked is not defined within the bot's commandList;
	 * @throws NoPermissionException: if the user who invoked the command does not have enough permission to execute it.
	 */
	public Command.CommandResponse checkForAndExecuteCommands() throws CommandNotFoundException, NoPermissionException {
		Message latestMessage = controller.getLatestMessage();
		if(this.latestMessage != latestMessage) {
			if(eventListener != null) { eventListener.onMessageReceived(latestMessage); }
			this.latestMessage = latestMessage;
		}

		if(latestMessage != null && latestMessage.getContent().startsWith(preCommandChar)) {
			String invokedCommandName = latestMessage.getContent().split(" ")[0].replace(preCommandChar, "");

			if(eventListener != null) { eventListener.onCommandInvoked(invokedCommandName); }

			Command command = commandList.get(invokedCommandName);
			if(command != null) {
				if(eventListener != null) { eventListener.onCommandExecute(command); }
				return command.execute(latestMessage);
			} else {
				if(eventListener != null) { eventListener.onUnknownCommandReceived(invokedCommandName); }
				throw new CommandNotFoundException(invokedCommandName);
			}
		}

		return null;
	}

	/**
	 * <b>Helper Function:</b>
	 * <p>Sends a message by calling the bot's {@link MessagingController}'s sendMessage() method.</p>
	 * <p>This functions exists purely to alienate bot developers from the {@link MessagingController} interface.</p>
	 * @param message: the message to be sent.
	 */
	public void sendMessage(String message) {
		this.controller.sendMessage(message);
	}

	/**
	 * <b>Helper Function:</b>
	 * <p>
	 *     Returns the latest message by calling the bot's {@link MessagingController}'s getLatestMessage() method and
	 *     updates the class' latestMessage property.
	 * </p>
	 * <p>This function exists purely to alienate bot developers from the {@link MessagingController} interface.</p>
	 * @return a {@link Message} object representing the latest message received by the bot's {@link MessagingController}.
	 */
	public Message getLatestMessage() {
		Message latestMessage = controller.getLatestMessage();
		if(this.latestMessage != latestMessage) {
			if(eventListener != null) { eventListener.onMessageReceived(latestMessage); }
			this.latestMessage = latestMessage;
		}
		return latestMessage;
	}

	public BotEventListener getEventListener() { return eventListener; }
	public void setEventListener(BotEventListener eventListener) { this.eventListener = eventListener; }
}
