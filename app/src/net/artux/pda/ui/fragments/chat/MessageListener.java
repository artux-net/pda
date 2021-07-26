package net.artux.pda.ui.fragments.chat;

import net.artux.pdalib.Status;
import net.artux.pdalib.UserMessage;

import java.util.List;

public interface MessageListener {

    void newMessage(UserMessage message);
    void setDialogs(List<Dialog> message);
    void newStatus(Status status);

}
