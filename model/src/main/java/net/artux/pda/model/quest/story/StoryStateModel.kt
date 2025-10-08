package net.artux.pda.model.quest.story

import java.io.Serializable

class StoryStateModel : Serializable {
    var current = false
    var over = false
    var storyId = 0
    var chapterId = 0
    var stageId = 0

    override fun toString(): String {
        return "StoryStateModel{" +
                "current=" + current +
                ", over=" + over +
                ", storyId=" + storyId +
                ", chapterId=" + chapterId +
                ", stageId=" + stageId +
                '}'
    }
}