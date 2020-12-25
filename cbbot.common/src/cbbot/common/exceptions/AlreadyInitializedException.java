package cbbot.common.exceptions;

public class AlreadyInitializedException extends RuntimeException {
	public AlreadyInitializedException(String component) {
		super(String.format("%s has already been initialized!", component));
	}
}
