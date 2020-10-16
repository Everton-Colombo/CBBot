package cbbot.common;

import javax.naming.NoPermissionException;
import java.io.Serializable;
import java.util.Objects;
import java.util.function.Function;

public class Command implements Serializable {
	private static final long serialVersionUID = 31513131144L;

	protected String name;
	protected String usage;
	protected String description;
	protected Function<Message, String> function;

	public Command(String name, String usage, String description, Function<Message, String> function) {
		this.name = name;
		this.usage = usage;
		this.description = description;
		this.function = function;
	}


	public String execute(Message message) throws NoPermissionException {
		String[] sections = message.getContent().split(" ");
		return function.apply(message);
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
		return String.format("Command Name: %s\n\tUsage: \t%s\n\tDescription: \t%s", name, usage, description);
	}
}
