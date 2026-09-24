package net.artux.pda.flow

import kotlinx.coroutines.runBlocking
import net.artux.pda.flow.network.FlowApiClient
import net.artux.pda.flow.network.dto.toModel
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.model.quest.Stage
import net.artux.pda.model.quest.StoryModel
import net.artux.pda.model.quest.story.StoryDataModel
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.ArrayDeque

/**
 * Plays every story from a fresh account's entry point (chapter 1, stage 0 - what
 * StorySelectionScreen opens with no saved state) until a map stage, against the real dev
 * backend and through the same StageRules/enterStage() calls StageScreen makes, then checks
 * the map loads and that resuming lands back on that map stage.
 *
 * Transfers are picked greedily by distance to the nearest map stage in the chapter graph;
 * conditions are still enforced by StageRules.visibleTransfers(), so a story whose conditions
 * block every route fails here rather than stranding a player in the app.
 *
 * Requires network access to dev.artux.net, like FlowApiClientTest.
 */
class FlowToMapTest {

    private val api = FlowApiClient()
    private val timestamp = System.currentTimeMillis()
    private val email = "flow-map-test-$timestamp@artux.net"
    private val password = "FlowMap$timestamp!"
    private var registered = false

    @Before
    fun register() = runBlocking {
        val result = api.register("FlowMapTest", email, password)
        assertTrue("register() failed: ${result.exceptionOrNull()}", result.isSuccess)
        registered = true
    }

    @After
    fun cleanup() = runBlocking {
        if (registered) api.deleteAccount(email, password)
    }

    @Test
    fun `every story reaches a map from a fresh account, and resuming returns to it`() = runBlocking {
        val stories = api.getStories(email, password).getOrThrow()
        assertFalse("no stories on the backend", stories.isEmpty())

        for (info in stories) {
            val storyId = info.id ?: continue
            val story = api.getStory(storyId, email, password).getOrThrow().toModel()
            var storyData = api.getStoryData(email, password).getOrThrow().toModel()

            val chapterId = 1L
            val chapter = story.getChapter(chapterId.toString())
            assertNotNull("story $storyId has no chapter $chapterId", chapter)
            val path = walkToMap(story, chapter!!, chapterId, storyData) { storyData = it }
            val mapStage = chapter.getStage(path.last())!!

            val target = StageRules.mapTarget(mapStage)
            assertNotNull("story $storyId stage ${mapStage.id} is a map stage without a map id", target)
            val map = api.getMap(storyId, target!!.mapId, email, password).getOrThrow()
            assertFalse("story $storyId map ${target.mapId} has no tmx", map.tmx.isNullOrBlank())

            // What StorySelectionScreen resolves on the next launch.
            val resumed = api.getStoryData(email, password).getOrThrow().toModel()
                .storyStates.firstOrNull { it.storyId == storyId.toInt() }
            assertNotNull("story $storyId: position wasn't saved", resumed)
            assertEquals("story $storyId: resume chapter", chapterId.toInt(), resumed!!.chapterId)
            assertEquals("story $storyId: resume stage", mapStage.id.toInt(), resumed.stageId)

            println("story $storyId '${story.title}': map ${target.mapId} via stages $path")
        }
    }

    private suspend fun walkToMap(
        story: StoryModel,
        chapter: ChapterModel,
        chapterId: Long,
        initialData: StoryDataModel,
        onData: (StoryDataModel) -> Unit
    ): List<Long> {
        val distance = distancesToMap(chapter)
        var storyData = initialData
        var stage: Stage = chapter.getStage(0)
            ?: throw AssertionError("story ${story.id} chapter $chapterId has no stage 0")
        val path = mutableListOf(stage.id)

        repeat(MAX_STEPS) {
            if (stage.typeStage == StageRules.TYPE_MAP) return path
            val next = StageRules.visibleTransfers(stage, storyData)
                .mapNotNull { chapter.getStage(it.stage.toLong()) }
                .minByOrNull { distance[it.id] ?: Int.MAX_VALUE }
                ?: throw AssertionError("story ${story.id}: stuck at stage ${stage.id}, path $path")

            storyData = api.enterStage(story.id, chapterId, next.id, email, password).getOrThrow().toModel()
            onData(storyData)
            stage = next
            path += stage.id
        }
        throw AssertionError("story ${story.id}: no map after $MAX_STEPS steps, path $path")
    }

    /** Reverse BFS: each stage's transfer count to the nearest map stage, ignoring conditions. */
    private fun distancesToMap(chapter: ChapterModel): Map<Long, Int> {
        val incoming = HashMap<Long, MutableList<Long>>()
        chapter.stages.values.forEach { from ->
            from.transfers.forEach { incoming.getOrPut(it.stage.toLong()) { mutableListOf() } += from.id }
        }
        val distance = HashMap<Long, Int>()
        val queue = ArrayDeque<Long>()
        chapter.stages.values.filter { it.typeStage == StageRules.TYPE_MAP }.forEach {
            distance[it.id] = 0
            queue += it.id
        }
        while (queue.isNotEmpty()) {
            val id = queue.poll()
            incoming[id].orEmpty().forEach { prev ->
                if (prev !in distance) {
                    distance[prev] = distance.getValue(id) + 1
                    queue += prev
                }
            }
        }
        return distance
    }

    private companion object {
        const val MAX_STEPS = 60
    }
}
