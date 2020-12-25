package cbbot.core;

import cbbot.common.Command;
import cbbot.common.Message;

public interface BotEventListener {
    void onInitialized();
    void onOperate();
    void onTerminated();
    void onMessageReceived(Message messageReceived);
    void onCommandInvoked(String invokedCommandName);
    void onCommandExecute(Command command);
    void onUnknownCommandReceived(String invokedCommandName);
}
