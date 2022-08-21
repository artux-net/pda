package net.artux.pda.ui.fragments.chat;

import net.artux.pda.model.StatusModel;
import net.artux.pda.model.UserMessage;

import java.util.List;

public interface MessageListener {

    void newMessage(UserMessage message);
    void setDialogs(List<Dialog> message);
    void newStatus(StatusModel status);

}
