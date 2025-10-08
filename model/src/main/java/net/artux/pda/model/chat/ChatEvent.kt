package net.artux.pda.model.chat

class ChatEvent(val content: String) {
    companion object {
        fun of(content: String): ChatEvent {
            return ChatEvent(content)
        }
    }
}