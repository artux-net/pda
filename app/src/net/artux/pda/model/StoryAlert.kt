package net.artux.pda.model

import net.artux.pda.R

enum class StoryAlert(
    val titleId: Int, val messageId: Int
){
    OTHER_NOT_COMPLETE(R.string.no_access, R.string.early_story_alert),
    ALREADY_COMPLETE(R.string.no_access, R.string.complete_story_alert)
}