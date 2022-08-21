package net.artux.pda.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Summary {

    private final String title;
    private final List<UserMessage> messages;

    public Summary() {
        this.title = getCurrentId();
        messages = new ArrayList<>();
    }

    public static String getCurrentId() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(new Date());
    }

    public void addMessage(UserMessage message) {
        messages.add(message);
    }

    public List<UserMessage> getMessages() {
        return messages;
    }

    public String getTitle() {
        return title;
    }
}
