package cbbot.core;

import cbbot.common.Message;
import org.openqa.selenium.WebDriver;


public interface MessagingController {
	class NotInitializedException extends Exception {
		@Override
		public String toString() {
			return super.toString() + " Messaging Page wasn't initialized.";
		}
	}

	class AlreadyInitializedException extends Exception {
		@Override
		public String toString() {
			return super.toString() + "Messaging Page has already been initialized.";
		}
	}

	WebDriver getDriver();
	void initialize(long waitTime) throws AlreadyInitializedException;
	void terminate() throws  NotInitializedException;
	void sendMessage(String message) throws NotInitializedException;
	Message getLatestMessage() throws NotInitializedException;
}
