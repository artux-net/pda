package net.artux.pda.flow.network.dto

import com.google.gson.annotations.SerializedName

// Mirrors net.artux.pdanetwork.model.Profile (GET api/v1/profile) - only the fields
// StorySelectionScreen's profile panel shows (gang, rank/rating, days in game).
data class ProfileDto(
    @SerializedName("nickname") val nickname: String? = null,
    @SerializedName("xp") val xp: Int? = null,
    // Enum name ("BANDITS"), not Gang's numeric id - same wire format as StoryDataDto.gang.
    @SerializedName("gang") val gang: String? = null,
    @SerializedName("ratingPosition") val ratingPosition: Long? = null,
    // ISO-8601 ("2024-05-01T12:34:56.000Z") - java.time isn't available on RoboVM, so this is
    // parsed by hand (see ProfileInfo.daysSince) rather than as an Instant/OffsetDateTime.
    @SerializedName("registration") val registration: String? = null
)
