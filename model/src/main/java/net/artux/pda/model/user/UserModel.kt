package net.artux.pda.model.user

import java.io.Serializable
import java.time.Instant
import java.util.UUID

class UserModel : Serializable {
    var id: UUID? = null
    var login: String? = null
    var email: String? = null
    var name: String? = null
    var nickname: String? = null
    var avatar: String? = null
    var role: Role? = null
    var pdaId: Long? = null
    var gang: Gang? = null
    var xp = 0
    var registration: Instant? = null
    var lastLoginAt: Instant? = null

}