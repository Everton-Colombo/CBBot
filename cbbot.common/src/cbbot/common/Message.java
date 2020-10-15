package cbbot.common;

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

	public boolean equals(Message obj) {
		if(obj == null)
			return false;

		return content.equals(obj.getContent()) && sender.equals(obj.getSender());
	}
}
