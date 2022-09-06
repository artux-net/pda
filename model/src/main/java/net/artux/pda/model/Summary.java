package net.artux.pda.model;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class Summary {

    private final String title;
    private final List<UserMessage> messages;

    public Summary() {
        this.title = getCurrentId();
        messages = new ArrayList<>();
    }

    public static String getCurrentId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                .withZone(ZoneId.systemDefault());
        return formatter.format(Instant.now());
    }

    public void addMessage(UserMessage message) {
        messages.add(message);
    }

}
