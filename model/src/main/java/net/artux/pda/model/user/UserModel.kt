package net.artux.pda.model.user

import java.io.Serializable
import java.time.Instant

data class UserModel(
    var email: String,
    var role: Role? = null,
    var lastLoginAt: Instant? = null
) : SimpleUserModel(), Serializable