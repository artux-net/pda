package net.artux.pda.flow

import net.artux.pda.model.quest.Stage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StageRulesTest {

    private fun stage(
        typeStage: Int = 0,
        actions: Map<String, List<String>> = emptyMap(),
        data: Map<String, String> = emptyMap()
    ) = Stage().apply {
        this.typeStage = typeStage
        this.actions = HashMap(actions)
        this.data = HashMap(data)
    }

    @Test
    fun `prologue checkpoint stage jumps back within the chapter`() {
        // Story 1, chapter 1, stage 10 on the dev backend: typeStage 5, no data, openStage 6.
        val jump = StageRules.jump(stage(typeStage = 5, actions = mapOf("openStage" to listOf("6"))))
        assertEquals(StageRules.Jump(chapterId = null, stageId = 6, sync = true), jump)
    }

    @Test
    fun `openStage accepts chapter-colon-stage and two-argument forms`() {
        assertEquals(
            StageRules.Jump(2, 5, sync = true),
            StageRules.jump(stage(actions = mapOf("openStage" to listOf("2:5"))))
        )
        assertEquals(
            StageRules.Jump(2, 5, sync = true),
            StageRules.jump(stage(actions = mapOf("openStage" to listOf("2", "5"))))
        )
    }

    @Test
    fun `processData stage jumps without syncing`() {
        val jump = StageRules.jump(stage(typeStage = 6, data = mapOf("chapter" to "3", "stage" to "0")))
        assertEquals(StageRules.Jump(3, 0, sync = false), jump)
    }

    @Test
    fun `ordinary stages and seller stages don't jump`() {
        assertNull(StageRules.jump(stage()))
        assertNull(StageRules.jump(stage(typeStage = 5, data = mapOf("seller" to "1"))))
    }

    @Test
    fun `finishStory ends the story`() {
        assertTrue(StageRules.exitsStory(stage(actions = mapOf("finishStory" to emptyList()))))
        assertFalse(StageRules.exitsStory(stage(actions = mapOf("add" to listOf("x")))))
    }
}
