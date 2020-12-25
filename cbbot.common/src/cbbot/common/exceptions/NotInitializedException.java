package cbbot.common.exceptions;

public class NotInitializedException extends RuntimeException {
	public NotInitializedException(String component) {
		super(String.format("%s hasn't been initialized yet!", component));
	}
}
