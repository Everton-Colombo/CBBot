package cbbot.common;

import javax.naming.NoPermissionException;
import java.util.function.Function;

/**
 * <p>
 *     A variation of {@link Command} that can only be executed by an agent
 *     with a level equal to or grater than the {@link RestrictedCommand}'s level.
 * </p>
 * <p>
 *     The {@link RestrictedCommand} must be related to a {@link Hierarchy} object in order
 *     to have access to the agents' levels.
 * </p>
 */
public class RestrictedCommand extends Command {
	private static final long serialVersionUID = 1831513131144L;

	protected int level;
	protected Hierarchy hierarchy;

	public RestrictedCommand(String name, String usage, String description, int level, Hierarchy hierarchy,
							 Function<Message, String> function) {
		super(name, usage, description, function);

		this.level = level;
		this.hierarchy = hierarchy;
	}

	@Override
	public CommandResponse execute(Message message) throws NoPermissionException {
		if(hierarchy.getLevelOrSetToDefault(message.getSender()) >= level) { return super.execute(message); }
		else { throw new NoPermissionException(String.format("Caller \"%s\" is a level %d agent. Required level: %d",
				message.getSender(), hierarchy.getLevelOrSetToDefault(message.getSender()), level));
		}
	}

	public int getLevel() { return level; }
	public void setLevel(int level) { this.level = level; }

	public Hierarchy getHierarchy() { return hierarchy; }
	public void setHierarchy(Hierarchy hierarchy) { this.hierarchy = hierarchy; }

	@Override
	public String toString() {
		return super.toString() + "\n\tRestriction Level: " + level;
	}
}
