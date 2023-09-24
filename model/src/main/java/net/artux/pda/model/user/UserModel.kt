package net.artux.pda.model.user

import java.io.Serializable
import java.time.Instant

class UserModel : SimpleUserModel(), Serializable {
    var email: String? = null
    var role: Role? = null
    var lastLoginAt: Instant? = null
}