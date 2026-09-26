package net.artux.pda.model.chat

import java.time.Instant

class ChatUpdate {
    var updates: List<UserMessage> = mutableListOf()
    var events: List<ChatEvent>? = null
    var timestamp: Instant? = null
    fun getUpdatesByType(type: UserMessage.Type): List<UserMessage> {
        return updates
            .filter { msg: UserMessage -> msg.type === type }
    }
}