package net.artux.pda.model

import net.artux.pda.model.chat.UserMessage
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Summary {
    var title: String = currentId
    var messages: MutableList<UserMessage> = mutableListOf()

    fun addMessage(message: UserMessage) {
        messages.add(message)
    }

    companion object {
        val currentId: String
            get() {
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    .withZone(ZoneId.systemDefault())
                return formatter.format(Instant.now())
            }
    }
}