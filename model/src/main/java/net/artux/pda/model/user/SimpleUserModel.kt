package net.artux.pda.model.user

import java.time.Instant
import java.util.UUID

class SimpleUserModel {
    var id: UUID? = null
    var login: String? = null
    var nickname: String? = null
    var avatar: String? = null
    var pdaId = 0
    var xp = 0
    var achievements = 0
    var gang: Gang? = null
    var registration: Instant? = null
}