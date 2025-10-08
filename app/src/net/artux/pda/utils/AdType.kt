package net.artux.pda.utils

enum class AdType(
    val isRewarded: Boolean = false,
    val defaultProbability: Float = 1f,
    val adUnitId: String = ""
) {
    QUEST_SIMPLE(adUnitId = "R-M-2151056-3"),
    QUEST_VIDEO(adUnitId = "R-M-2151056-2"),
    TRANSFER_VIDEO(adUnitId = "R-M-2151056-4", defaultProbability = 0.5f),
}
