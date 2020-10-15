package cbbot.products.wppmstlinkerbot;

import cbbot.common.Command;
import cbbot.common.Message;
import cbbot.core.DualCBBot;
import cbbot.core.MessagingController;

import javax.naming.NoPermissionException;
import java.util.Random;

public class WPPMSTLinkerBot extends DualCBBot {

	public enum LinkMode {
		ONE_WAY_GOING, 	// From teams to wpp only
		ONE_WAY_COMING,	// From wpp to teams only
		DUAL_WAY		// Bothways
	}

	protected LinkMode linkMode;

	protected int password;

	protected Message lastFromWPP;
	protected Message lastToWPP;
	protected Message lastFromMST;
	protected Message lastToMST;

	public WPPMSTLinkerBot(MessagingController MSTeams, MessagingController WPP, String preCommandChar, LinkMode linkMode) {
		super(MSTeams, WPP, preCommandChar);
		this.linkMode = linkMode;

		password = new Random().nextInt(9999);
		System.out.println("Password: " + password);

		addCommands(new Command("finalizar", "finalizar <SENHA>", "Finaliza o entrelaçamento", this::killCommand));
	}

	@Override
	public void initialize() throws MessagingController.AlreadyInitializedException {
		super.initialize();
	}

	@Override
	public void operate() {
		while(true) {
			try {
				Object[] result = checkForAndExecuteCommands(controller2);
				String a;
				if(result != null && (a = (String) result[1]) != null && a.equals("err")) {
					lastToMST = (Message) result[0];
				}
			} catch (MessagingController.NotInitializedException | NoPermissionException | CommandNotFoundException e) {
				e.printStackTrace();
			}

			try {
				lastFromMST = controller.getLatestMessage();
				lastFromWPP = controller2.getLatestMessage();
			} catch (MessagingController.NotInitializedException e) {
				e.printStackTrace();
				continue;
			}

			if((linkMode == LinkMode.ONE_WAY_GOING || linkMode == LinkMode.DUAL_WAY)
			&& lastFromMST != null && !lastFromMST.equals(lastToWPP)
			&& !lastFromMST.getContent().isEmpty()
			&& !(lastFromMST.getSender().equals("") && lastFromMST.getContent().startsWith("["))) {

				lastToWPP = lastFromMST;
				send(controller2, String.format("[_TEAMS_] <*%s*>: %s",
						lastFromMST.getSender(), lastFromMST.getContent()));
			}

			if((linkMode == LinkMode.ONE_WAY_COMING || linkMode == LinkMode.DUAL_WAY)
			&& lastFromWPP != null && !lastFromWPP.equals(lastToMST)) {
				lastToMST = lastFromWPP;
				send(controller, String.format("[WHATSAPP] <%s>: %s",
						lastFromWPP.getSender(), lastFromWPP.getContent()));
			}
		}
	}

	@Override
	public void terminate() {
		super.terminate();
	}

	private void send(MessagingController controller, String msg) {
		new Thread(() -> {
			try {
				controller.sendMessage(msg);
			} catch (MessagingController.NotInitializedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	private String killCommand(Message msg) {
		if(Integer.parseInt(msg.getContent().split(" ")[1]) != password)
			return "err";

		send(controller2, "Entrelaçamento Finalizado!");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		terminate();

		return null;
	}
}
