package cbbot.core;

import cbbot.common.Message;
import cbbot.common.exceptions.AlreadyInitializedException;
import cbbot.common.exceptions.NotInitializedException;
import org.openqa.selenium.WebDriver;


public interface MessagingController {
	WebDriver getDriver();
	void initialize(long waitTime) throws AlreadyInitializedException;
	void terminate() throws NotInitializedException;
	void sendMessage(String message) throws NotInitializedException;
	Message getLatestMessage() throws NotInitializedException;
}
