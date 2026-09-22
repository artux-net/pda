package net.artux.pda.flow

import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel

/**
 * Plain mutable state holder carried between screens of the pre-game flow (registration ->
 * login -> story selection -> stage dialogue -> map). No DI here on purpose: CoreComponent's
 * Dagger graph (used once the real map loads) requires a DataRepository/GameMap that only
 * exist after this flow has actually chosen a story and reached a map stage, so this flow runs
 * entirely outside that graph and hands off to it only at the very end (see StageScreen).
 */
class FlowSession {
    var email: String? = null
    var password: String? = null

    var story: StoryModel? = null
    var storyData: StoryDataModel? = null

    var currentChapterId: Long = 0
    var currentStageId: Long = 0

    fun credentialsOrThrow(): Pair<String, String> {
        val e = email ?: error("Not logged in: email missing")
        val p = password ?: error("Not logged in: password missing")
        return e to p
    }
}
