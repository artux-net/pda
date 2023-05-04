package net.artux.pda.model.chat

import java.time.Instant
import java.util.stream.Collectors

class ChatUpdate {
    var updates: List<UserMessage> = mutableListOf()
    var events: List<ChatEvent>? = null
    var timestamp: Instant? = null
    fun getUpdatesByType(type: UserMessage.Type): List<UserMessage> {
        return updates
            .stream()
            .filter { msg: UserMessage -> msg.type === type }
            .collect(Collectors.toList())
    }
}