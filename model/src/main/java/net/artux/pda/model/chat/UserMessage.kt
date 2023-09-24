package net.artux.pda.model.chat

import net.artux.pda.model.quest.story.StoryDataModel
import net.artux.pda.model.user.Role
import net.artux.pda.model.user.UserModel
import java.io.Serializable
import java.time.Instant
import java.util.UUID

data class UserMessage(
    var id: UUID,
    var type: Type,
    var content: String,
    var timestamp: Instant,
) : Serializable {

    lateinit var author: UserModel

    constructor(userModel: UserModel, message: String) : this(
        UUID.randomUUID(),
        Type.NEW,
        message,
        Instant.now()
    ) {
        author = userModel
    }

    constructor(storyDataModel: StoryDataModel, message: String) : this(
        UUID.randomUUID(),
        Type.NEW,
        message,
        Instant.now()
    ) {
        author = UserModel()
        author.login = storyDataModel.login
        author.avatar = storyDataModel.avatar
        author.pdaId = storyDataModel.pdaId
        author.nickname = storyDataModel.nickname
        author.gang = storyDataModel.gang
    }

    constructor(senderLogin: String?, message: String, avatarId: String?) : this(
        UUID.randomUUID(),
        Type.NEW,
        message,
        Instant.now()
    ) {
        author = UserModel()
        author.login = senderLogin
        author.avatar = avatarId
        author.name = senderLogin
        author.nickname = ""
        author.role = Role.ADMIN
        author.pdaId = (-1L).toInt()
    }

    enum class Type {
        OLD, NEW, UPDATE, DELETE
    }

    companion object {
        @JvmStatic
        fun event(chatEvent: ChatEvent): UserMessage {
            return UserMessage("System", chatEvent.content, "0")
        }
    }
}