package cbbot.core;

import cbbot.common.Command;
import cbbot.common.Message;

import javax.naming.NoPermissionException;

public abstract class DualCBBot extends CBBot {

	protected MessagingController controller2;

	public DualCBBot(MessagingController controller1, MessagingController controller2, String preCommandChar) {
		super(controller1, preCommandChar);
		this.controller2 = controller2;
	}

	@Override
	public void initialize() throws MessagingController.AlreadyInitializedException {
		super.initialize();
//		System.out.println("Controller 1 initialized, press enter to initialize controller 2!");
		controller2.initialize(10000);
	}

	@Override
	public void terminate() {
		super.terminate();

		try {
			controller2.terminate();
		} catch (MessagingController.NotInitializedException e) {
			e.printStackTrace();
		}
	}

	public CommandResponse checkForAndExecuteCommands(MessagingController controller) throws MessagingController.NotInitializedException, CommandNotFoundException, NoPermissionException {
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
