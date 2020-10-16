package cbbot.core.controllers;

import cbbot.common.Message;
import cbbot.core.MessagingController;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class WhatsappPageController implements MessagingController {

	public enum ChatType {

		INDIVIDUAL(0, 2), GROUP(2, 4);

		private final int senderIndex;
		private final int messageIndex;

		ChatType(int senderIndex, int messageIndex) {
			this.senderIndex = senderIndex;
			this.messageIndex = messageIndex;
		}

		public int getSenderIndex() { return senderIndex; }
		public int getMessageIndex() { return messageIndex; }
	}

	private final WebDriver driver;
	private final String targetChat;
	private final ChatType chatType;

	private boolean initialized = false;

	private WebElement messageBar;

	public WhatsappPageController(WebDriver driver, String targetChat, ChatType chatType) {
		this.driver = driver;
		this.targetChat = targetChat;
		this.chatType = chatType;
	}

	@Override
	public void initialize(long waitTime) throws AlreadyInitializedException {
		if(initialized)
			throw new AlreadyInitializedException();

		driver.get("https://web.whatsapp.com/");
		System.out.println("Please scan the QR code...");
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException e) {
			System.out.println("Unable to wait: Interrupted");
		}

		System.out.println("Setting up essential elements...");
		WebElement contactsBar = driver.findElement(By.xpath("//div[@data-tab='3']"));
		contactsBar.clear();
		contactsBar.sendKeys(targetChat);
		contactsBar.sendKeys(Keys.RETURN);
		messageBar = driver.findElement(By.xpath("//div[@data-tab='1']"));
		messageBar.clear();

		initialized = true;
		System.out.println("Done Initializing!");
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
		/*
		This method is NOT RELIABLE. The latest message holder found by the xpath query below
		also displays the target's status, therefore, if the target is typing, the latest message
		holder will not be holding the latest message, but, instead, the typing status, making this
		method return null. This also makes it difficult for the implementation of reliable
		timestamps.

		This does not mean this method is unusable: if the target is an individual chat, it is very
		likely that no issues will present; with group chats, as long as the message traffic is somewhat low
		(no users typing simultaneously)
		 */
		if(!initialized)
			throw new NotInitializedException();


		List<WebElement> l = driver.findElements(By.xpath("//div[@tabindex='0']"));
		WebElement latestMessageHolder = l.get(0);
//		WebElement latestMessageHolder = driver.findElement(By.cssSelector("div[class *= '_2hq0q'][tabindex = '0']"));
		String[] data = latestMessageHolder.getText().split("\n");


//		while(data.length < chatType.messageIndex + 1) {
//			latestMessageHolder = driver.findElement(By.xpath("//div[@tabindex='0']"));
//			data = latestMessageHolder.getText().split("\n");
//		}
		if(data.length < chatType.messageIndex + 1)
			return null;

		return new Message(data[chatType.getMessageIndex()], data[chatType.getSenderIndex()]);
	}

	@Override
	public void terminate() throws NotInitializedException {
		if(!initialized)
			throw new NotInitializedException();

		driver.close();
	}

	@Override
	public WebDriver getDriver() { return driver; }
	public String getTargetChat() { return targetChat; }
	public ChatType getChatType() { return chatType; }
	public boolean isInitialized() { return initialized; }
}
