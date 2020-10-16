package cbbot.core;

import javax.naming.NoPermissionException;

import cbbot.common.Message;
import cbbot.core.MessagingController.*;
import cbbot.common.Command;

import java.util.LinkedHashMap;

public abstract class CBBot {
	public static class CommandNotFoundException extends Exception {}

	public static class CommandResponse {
		private final Command commandExecuted;
		private final String response;
		private final Message agentMessage;

		public CommandResponse(Command commandExecuted, String answer, Message agentMessage) {
			this.commandExecuted = commandExecuted;
			this.response = answer;
			this.agentMessage = agentMessage;
		}

		public Command getCommandExecuted() { return commandExecuted; }
		public String getResponse() { return response; }
		public Message getAgentMessage() { return agentMessage; }
	}

	protected MessagingController controller;
	protected String preCommandChar;
	protected LinkedHashMap<String, Command> commandList = new LinkedHashMap<>();

	public CBBot(MessagingController controller, String preCommandChar) {
		this.controller = controller;
		this.preCommandChar = preCommandChar;
	}

	protected void addCommands(Command ... commands) {
		for(Command command : commands) {
			commandList.put(command.getName(), command);
		}
	}

	public void initialize() throws MessagingController.AlreadyInitializedException {
		controller.initialize(10000);
	}

	public abstract void operate();

	public void terminate() {
		try {
			controller.terminate();
		} catch (MessagingController.NotInitializedException e) {
			e.printStackTrace();
		}
	}

	public CommandResponse checkForAndExecuteCommands() throws NotInitializedException, CommandNotFoundException, NoPermissionException {
		Message latestMessage = controller.getLatestMessage();
		if(latestMessage != null && latestMessage.getContent().startsWith(preCommandChar)) {
			Command command;
			if((command = commandList.get(latestMessage.getContent().split(" ")[0].replace(preCommandChar, ""))) != null) {
				return new CommandResponse(command, command.execute(latestMessage), latestMessage);
			} else
				throw new CommandNotFoundException();
		}
		return null;
	}
}
