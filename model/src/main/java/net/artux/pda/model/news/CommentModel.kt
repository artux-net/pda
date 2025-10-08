package net.artux.pda.model.news

import net.artux.pda.model.user.SimpleUserModel
import java.security.cert.CertPathValidatorResult
import java.util.UUID

data class CommentModel(
    var id: UUID,
    val content: String = "",
    val author: SimpleUserModel,
    var likes: Int = 0
)
