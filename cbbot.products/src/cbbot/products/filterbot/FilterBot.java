package cbbot.products.filterbot;

import cbbot.common.Command;
import cbbot.common.Hierarchy;
import cbbot.common.Message;
import cbbot.common.RestrictedCommand;
import cbbot.core.CBBot;
import cbbot.core.MessagingController;
import cbbot.util.MapUtils;

import javax.naming.NoPermissionException;
import java.io.*;
import java.text.Normalizer;
import java.util.*;

public class FilterBot extends CBBot {
	List<String> REGEXs;
	Map<String, Integer> faultCount;
	Hierarchy hierarchy;

	public FilterBot(MessagingController controller, String preCommandChar, String masterAgent) {
		super(controller, preCommandChar);
		this.REGEXs = new ArrayList<>();

		hierarchy = new Hierarchy(0);
		hierarchy.setAgentLevel(masterAgent, Integer.MAX_VALUE);

		addCommands(new RestrictedCommand("desligar", "desligar", "Para a execução", 3, hierarchy, this::kill),
					new Command("rank", "rank", "Mostra o rank de filtrados", this::rank),
					new RestrictedCommand("filtrar", "filtrar ! <REGEX>", "Adiciona a <REGEX> ao filtro", 1, hierarchy, this::blacklist),
					new RestrictedCommand("re-importar", "re-importar", "Re-importa arquivos", 1, hierarchy, this::reload),
					new RestrictedCommand("limpar-nome", "limpar-nome : <NOME>", "Limpa os registros de <NOME>", 2, hierarchy, this::clearName),
					new Command("hierarquia", "hierarquia", "Mostra a hierarquia", this::showHierarchy),
					new RestrictedCommand("promover", "promover : <NOME> : <NÍVEL>", "Promove <NOME> ao nível <NÍVEL>", 2, hierarchy, this::level),
					new Command("ajuda", "ajuda", "Mostra a lista de comandos", this::help));
	}

	@Override
	public void initialize() throws MessagingController.AlreadyInitializedException {
		super.initialize();

		try (BufferedReader br = new BufferedReader(new FileReader(new File("wordlist.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				REGEXs.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (ObjectInputStream f = new ObjectInputStream(new FileInputStream(new File("faults.bin")))) {
			faultCount = (Map<String, Integer>) f.readObject();
		} catch (IOException | ClassNotFoundException e) {
			faultCount = new HashMap<>();
			e.printStackTrace();
		}

		try (ObjectInputStream f = new ObjectInputStream(new FileInputStream(new File("hierarchy.bin")))) {
			hierarchy = (Hierarchy) f.readObject();
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}

		say("<Filtro> Inicialização OK");
	}

	@Override
	public void operate() {
		while (true) {
			try {
				checkForAndExecuteCommands();

				Message latestMessage = controller.getLatestMessage();
				String content = Normalizer.normalize(latestMessage.getContent(), Normalizer.Form.NFD);
				content.replaceAll("[^\\p{ASCII}]", "");

				boolean found = false;
				for (String regex : REGEXs) {
					if (content.toLowerCase().matches(regex)) {
						found = true;
						break;
					}
				}

				if (found) {
					String sender = latestMessage.getSender();
					say(String.format("Nós condenamos esse vocabulário, @%s\t. " +
							"Essa é a %dᵃ mensagem inapropriada vinda de você. Adeque-se!", sender, addFault(sender)));
				}
			} catch (MessagingController.NotInitializedException e) {
				System.out.println("Controller not initialized");
			} catch (CommandNotFoundException e) {
				e.printStackTrace();
			} catch (NoPermissionException e) {
				say("Você não possui permissão para executar esse comando!");
			}
		}
	}

	@Override
	public void terminate() {
		super.terminate();

		try (ObjectOutputStream f = new ObjectOutputStream(new FileOutputStream(new File("faults.bin")))){
			f.writeObject(faultCount);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (ObjectOutputStream f = new ObjectOutputStream(new FileOutputStream(new File("hierarchy.bin")))){
			f.writeObject(hierarchy);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int addFault(String agent) {
		if(!faultCount.containsKey(agent)) {
			faultCount.put(agent, 0);
		}

		return faultCount.replace(agent, faultCount.get(agent) + 1) + 1;
	}

	public String kill(Message msg) {
		say("Desligando...");
		hold(1000);
		terminate();
		System.exit(3);

		return null;
	}

	public String rank(Message msg) {
		say("*RANKING*");
		for(Map.Entry<String, Integer> entry : MapUtils.sortByValue(faultCount).entrySet()) {
			say(String.format("@%s\t: %d mensagens inapropriadas;", entry.getKey(), entry.getValue()));
		}

		return null;
	}

	public String reload(Message msg) {
		REGEXs.clear();

		try (BufferedReader br = new BufferedReader(new FileReader(new File("wordlist.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				REGEXs.add(line);
			}

			say("Arquivos re-importados com sucesso!");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String blacklist(Message msg) {

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File("wordlist.txt")))) {
			String par = msg.getContent().split(" ! ")[1];
			if(!(par.startsWith("(^") && par.endsWith("$)"))) {
				say(String.format("ATENÇÃO: O filtro admite somente expressões regulares! Tente usar *%s* ao invés de *%s*",
						"(^" + par + "$)", par));

				return null;
			}

			REGEXs.add(par);

			for(String word : REGEXs) {
				bw.write(word);
				bw.newLine();
			}

			say("Palavra adicionada ao filtro com sucesso!");
		} catch (IndexOutOfBoundsException e) {
			say("Parâmetro Necessário...");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	public String clearName(Message msg) {
		try {
			String agent = msg.getContent().split(" : ")[1].replace("@", "").replace("\t", "");
			if(faultCount.remove(agent) != null) {
				say(String.format("O nome @%s\tfoi limpo com sucesso!", agent));
			} else {
				say(String.format("O nome @%s\tjá era limpo!", agent));
			}
		} catch (IndexOutOfBoundsException e) {
			say("Erro de Sintaxe!");
		}

		return null;
	}

	public String showHierarchy(Message msg) {
		for(Map.Entry<String, Integer> entry : MapUtils.sortByValue(hierarchy.getHierarchy()).entrySet()) {
			say(String.format("@%s\tpossui nível %d de acesso;", entry.getKey(), entry.getValue()));
		}

		return null;
	}

	public String help(Message msg) {
		say("*Lista de Comandos*");
		say(String.format("Todos os comandos devem ser precedidos por \"%s\"", preCommandChar));
		for(Map.Entry<String, Command> entry : commandList.entrySet()) {
			Command com = entry.getValue();
			say("===============");
			say(String.format("*Nome:* %s\n*Nível de Restrição:* %d\n" +
					"*Modo de Uso:* %s%s\n*Descrição:* %s",
					com.getName(),
					(com instanceof RestrictedCommand ? ((RestrictedCommand) com).getLevel() : hierarchy.getDefaultLevel()),
					preCommandChar, com.getUsage(), com.getDescription()));
		}

		return null;
	}

	public String level(Message msg) {
		try {
			String[] content = msg.getContent().split(" : ");
			String agent = content[1].replace("@", "").replace("\t", "");
			int newLevel = Integer.parseInt(content[2]);

			hierarchy.setAgentLevel(agent, newLevel);
			say(String.format("%s agora possui nível de acesso %d!", agent, newLevel));
		} catch (IndexOutOfBoundsException e) {
			say("Erro de Sintaxe");
		}

		try (ObjectOutputStream f = new ObjectOutputStream(new FileOutputStream(new File("hierarchy.bin")))){
			f.writeObject(hierarchy);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void say(String message) {
		try {
			controller.sendMessage(message);
		} catch (MessagingController.NotInitializedException e) {
			e.printStackTrace();
		}
	}

	private void hold(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
