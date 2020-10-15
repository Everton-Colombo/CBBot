package cbbot.products.cuprumbot;

import cbbot.common.Command;
import cbbot.common.Hierarchy;
import cbbot.common.RestrictedCommand;
import cbbot.core.CBBot;
import cbbot.core.MessagingController;

import javax.naming.NoPermissionException;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;

public class CuprumBot extends CBBot {
	private final String nickName;
	private final String initializationMessage;
	private final int password;

	private Hierarchy hierarchy;

	ScriptEngineManager manager = new ScriptEngineManager();
	ScriptEngine engine = manager.getEngineByName("js");

	public CuprumBot(MessagingController controller, String preCommandChar, String nickName, String initializationMessage) {
		super(controller, preCommandChar);
		this.nickName = nickName;
		this.initializationMessage = initializationMessage;
		this.password = new Random().nextInt(9999);
		System.out.println("PASSWORD: " + password);

		hierarchy = new Hierarchy(0);
		hierarchy.setAgentLevel("+55 93 9902-7134", 0);

		Command ajuda = new Command("ajuda", "ajuda", "Exibe a lista de comandos", data -> {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("Todos os comandos devem ser precedidos por '%s'\n", preCommandChar));
			sb.append("*Lista de Comandos*\n");
			for(Command command : commandList.values()) {
				sb.append(String.format("==============\n*Nome:* %s\n*Modo de Uso:* %s\n*Descrição:* %s\n", command.getName(), command.getUsage(),
						command.getDescription()));
			}

			return sb.toString();
		});

		Command kill = new RestrictedCommand("kill", "kill <SENHA>", "Finaliza a execução do bot se a <SENHA> estiver correta.",
				1, hierarchy,
				message -> {
			String[] content = message.getContent().split(" ");

			try {
				if(Integer.parseInt(content[1]) == password) {
					say("Desligando...");
					System.exit(3);
					return null;
				} else {
					return "Senha Incorreta";
				}
			} catch (Exception e) {
				return "Erro";
			}
				});

		Command alea = new Command("alea", "alea <MAX>", "Gera um número aleatório entre 0 e <MAX>",
				message -> {
			String[] content = message.getContent().split(" ");

			try {
				return Integer.toString(new Random().nextInt(Integer.parseInt(content[1])));
			} catch (Exception e) {
				return e.toString();
			}
				});

		Command eval = new Command("eval", "eval \\ <SENTENÇA>", "Avalia a <SENTENÇA> e retorna o resultado",
				message -> {
			String[] content = message.getContent().split(" \\ ");

			try {
				return engine.eval(content[1]).toString();
			} catch (Exception e) {
				return e.toString();
			}
				});
		
		addCommands(ajuda, alea, eval, kill);
	}

	@Override
	public void initialize() throws MessagingController.AlreadyInitializedException {
		super.initialize();

		say(initializationMessage);
	}

	@Override
	public void operate() {
		while(true) {
			try {
				CBBot.CommandResponse result;
				if((result = checkForAndExecuteCommands()) != null) {
					say(result.getResponse());
				}
			} catch (MessagingController.NotInitializedException | CommandNotFoundException e) {
				e.printStackTrace();
				say("ERRO: " + e.toString());
			} catch (NoPermissionException e) {
				try {
					String sender = controller.getLatestMessage().getSender();
					say(String.format("@%s, você não tem permissão para executar esse comando!", sender));
				} catch (MessagingController.NotInitializedException notInitializedException) {
					notInitializedException.printStackTrace();
				}
			}
		}
	}

	@Override
	public void terminate() {
		say("Desligando...");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.terminate();
	}

	private void say(String message) {
		try {
			controller.sendMessage("["+nickName+"]" + " " + message);
		} catch (MessagingController.NotInitializedException e) {
			e.printStackTrace();
		}
	}
}
