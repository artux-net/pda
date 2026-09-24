package net.artux.pda.flow

import net.artux.pda.model.QuestUtil
import net.artux.pda.model.quest.Stage
import net.artux.pda.model.quest.Transfer
import net.artux.pda.model.quest.story.StoryDataModel

/**
 * Stage-level decisions StageScreen makes, kept free of Gdx so FlowToMapTest can drive the real
 * backend through exactly the same rules the UI uses.
 */
object StageRules {

    const val TYPE_MAP = 4

    data class MapTarget(val mapId: Long, val pos: String?)

    /** A jump elsewhere in the story, taken as soon as a stage is entered. */
    data class Jump(val chapterId: Long?, val stageId: Long, val sync: Boolean)

    /** First text whose condition passes, like StageMapper on Android. */
    fun visibleText(stage: Stage, storyData: StoryDataModel?): String? =
        stage.texts
            .firstOrNull { storyData == null || QuestUtil.check(it.condition, storyData) }
            ?.text

    /**
     * Transfers whose condition passes. Same fallback StageMapper.getTransfers() has: if none
     * pass, every transfer is returned (marked "unreachable" on Android) rather than stranding
     * the player with no way forward.
     */
    fun visibleTransfers(stage: Stage, storyData: StoryDataModel?): List<Transfer> {
        val passable = stage.transfers.filter { storyData == null || QuestUtil.check(it.condition, storyData) }
        return passable.ifEmpty { stage.transfers }
    }

    /** Where a typeStage 4 stage sends the player, or null for any other stage (or no map id). */
    fun mapTarget(stage: Stage): MapTarget? {
        if (stage.typeStage != TYPE_MAP) return null
        val data = stage.data ?: return null
        val mapId = data["map"]?.toLongOrNull() ?: return null
        return MapTarget(mapId, data["pos"])
    }

    /**
     * Where a stage immediately sends the player instead of showing itself - the local commands
     * Android runs on the device rather than the server (CommandController.process()), which
     * this flow otherwise never saw:
     *  - "openStage" in any of CommandController.openStage()'s shapes - ["6"], ["2:5"] or
     *    ["2", "5"] - e.g. the prologue's "back to the checkpoint" stage after dying in an
     *    anomaly. Android re-enters it through beginWithStage(), i.e. with a position sync.
     *  - typeStage 5/6 with data chapter + stage - QuestViewModel.processData(), no sync.
     * Null for anything else.
     */
    fun jump(stage: Stage): Jump? {
        val open = stage.actions?.get("openStage").orEmpty()
        when {
            open.size == 1 && ':' in open[0] -> {
                val (chapter, target) = open[0].split(':')
                return Jump(chapter.toLongOrNull(), target.toLongOrNull() ?: return null, sync = true)
            }
            open.size == 1 -> return Jump(null, open[0].toLongOrNull() ?: return null, sync = true)
            open.size == 2 -> return Jump(open[0].toLongOrNull(), open[1].toLongOrNull() ?: return null, sync = true)
        }
        if (stage.typeStage == 5 || stage.typeStage == 6) {
            val data = stage.data ?: return null
            val stageId = data["stage"]?.toLongOrNull() ?: return null
            val chapterId = data["chapter"]?.toLongOrNull() ?: return null
            return Jump(chapterId, stageId, sync = false)
        }
        return null
    }

    /** "finishStory"/"exitStory": Android syncs, then returns to the story list. */
    fun exitsStory(stage: Stage): Boolean =
        stage.actions?.let { "finishStory" in it || "exitStory" in it } == true
}
