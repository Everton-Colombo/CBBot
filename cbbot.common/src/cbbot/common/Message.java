package cbbot.common;

import java.util.Objects;

/**
 * <p>Defines a message and holds its contents:</p>
 * <ul>
 *     <li>
 *         <b>Content:</b> The message itself.
 *     </li>
 *     <li>
 *         <b>Sender:</b> A {@link String} that identifies the message's sender.
 *     </li>
 * </ul>
 */
public class Message {
	private String content;
	private String sender;

	public Message(String content, String sender) {
		this.content = content;
		this.sender = sender;
	}

	public String getContent() { return content; }
	public String getSender() { return sender; }

	@Override
	public String toString() {
		return String.format("<%s>: %s", sender, content);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof Message)) {
			return false;
		}
		Message msg = (Message) obj;
		return Objects.equals(content, msg.getContent()) && Objects.equals(sender, msg.sender);
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, sender);
	}
}
