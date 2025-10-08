package net.artux.pda.model

import java.io.Serializable

class ConversationModel : Serializable {
    var id = 0
    var type = 0
    var title: String? = null
    var lastMessage: String? = null
    var avatar: String? = null
}