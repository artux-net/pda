package net.artux.pda.model

data class StorySelector(
    val storyId: Int, val chapterId: Int, val stageId: Int, val isCurrent: Boolean
)