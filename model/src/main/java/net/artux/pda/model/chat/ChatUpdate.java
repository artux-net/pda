package net.artux.pda.model.chat;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class ChatUpdate {

    private List<UserMessage> updates;
    private List<ChatEvent> events;
    private Instant timestamp;

    public List<UserMessage> getUpdatesByType(UserMessage.Type type) {
        return getUpdates()
                .stream()
                .filter(msg -> msg.getType() == type)
                .collect(Collectors.toList());
    }

}
