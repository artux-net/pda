package net.artux.pda.ui.fragments.chat;

import net.artux.pda.models.Status;
import net.artux.pda.models.UserMessage;

import java.util.List;

public interface MessageListener {

    void newMessage(UserMessage message);
    void setDialogs(List<Dialog> message);
    void newStatus(Status status);

}
