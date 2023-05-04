package net.artux.pda.model.profile

import java.io.Serializable
import java.time.Instant
import java.util.UUID

data class NoteModel(
    var id: UUID = UUID.randomUUID(),
    var title: String? = null,
    var content: String? = null,
    var time: Instant? = null
) : Serializable