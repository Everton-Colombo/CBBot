package cbbot.core.controllers;


import cbbot.common.Message;
import cbbot.core.MessagingController;
import org.openqa.selenium.*;

import java.util.List;
import java.util.Scanner;

public class MSTeamsPageController implements MessagingController {
	private boolean initialized;
	private final WebDriver driver;
	private final String chatURL;

	private WebElement messageBar;

	public MSTeamsPageController(WebDriver driver, String chatURL) {
		this.driver = driver;
		this.chatURL = chatURL;
	}

	@Override
	public WebDriver getDriver() {
		return this.driver;
	}

	@Override
	public void initialize(long waitTime) throws AlreadyInitializedException {
		if(initialized)
			throw new AlreadyInitializedException();

		driver.get(chatURL);
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			System.out.println("Unable to wait: Interrupted");
		}

		new Scanner(System.in).nextLine();		// wait

		System.out.println("Setting up essential elements...");
		messageBar = driver.findElement(By.xpath("//div[@role='textbox']"));
		messageBar.clear();

		initialized = true;
		System.out.println("Done Initializing!");
	}

	@Override
	public void terminate() throws NotInitializedException {
		if(!initialized)
			throw new NotInitializedException();

		driver.close();
	}

	@Override
	public void sendMessage(String message) throws NotInitializedException {
		if(!initialized)
			throw new NotInitializedException();

		messageBar.sendKeys(message);
		messageBar.sendKeys(Keys.RETURN);
	}

	@Override
	public Message getLatestMessage() throws NotInitializedException {
		if(!initialized)
			throw new NotInitializedException();

		List<WebElement> senders = driver.findElements(By.xpath("//div[@data-tid='threadBodyDisplayName']"));
		WebElement latestSender = senders.get(senders.size() - 1);

		List<WebElement> textMessages = driver.findElements(By.xpath("//div[@data-tid='messageBodyContent']"));
		WebElement latestMessage = textMessages.get(textMessages.size() - 1);

		try {
			return new Message(latestMessage.getText(), latestSender.getText());
		} catch (StaleElementReferenceException e) {
			System.out.println("Stale");
			return null;
		}
	}

	public boolean isInitialized() { return initialized; }
	public String getChatURL() { return chatURL; }
}
