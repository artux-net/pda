package net.artux.pda.model.user

import java.io.Serializable
import java.time.Instant
import java.util.UUID

open class SimpleUserModel : Serializable {
    var id: UUID = UUID.randomUUID()
    var login: String? = null
    var name: String? = null
    var nickname: String? = null
    var avatar: String? = null
    var pdaId = 0
    var xp = 0
    var achievements = 0
    var gang: Gang? = null
    var registration: Instant? = null
}