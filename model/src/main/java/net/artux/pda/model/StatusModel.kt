package net.artux.pda.model

import net.artux.pda.model.quest.story.StoryDataModel
import java.io.Serializable

class StatusModel : Serializable {
    var success = false
    var description: String? = null
    var storyDataModel: StoryDataModel? = null

    constructor() {}
    constructor(message: String?) {
        success = true
        description = message
    }

    constructor(throwable: Throwable) {
        success = false
        description = throwable.message
    }
}