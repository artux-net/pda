package net.artux.pdalib;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.List;

public class Summary {

    private final String title;
    private final List<UserMessage> messages;

    public Summary() {
        this.title = getCurrentId();
        messages = new ArrayList<>();
    }

    public static String getCurrentId(){
        DateTime dateTime = Instant.now().toDateTime();
        return dateTime.getDayOfMonth() + "." + dateTime.getMonthOfYear()+"."+dateTime.getYear();
    }

    public void addMessage(UserMessage message){
        messages.add(message);
    }

    public List<UserMessage> getMessages() {
        return messages;
    }

    public String getTitle() {
        return title;
    }
}
