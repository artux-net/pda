package net.artux.pda.model

import net.artux.pda.model.quest.story.StoryDataModel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


internal class QuestUtilTest {

    val storyDataModel = StoryDataModel()
        .apply {
            money = 100
        }

    @Test
    fun checkEqual() {
        Assertions.assertTrue(QuestUtil.check(mapOf(Pair("money==", listOf("100"))), storyDataModel))
    }

    @Test
    fun checkMore() {
        Assertions.assertFalse(QuestUtil.check(mapOf(Pair("money<", listOf("99"))), storyDataModel))
    }

    @Test
    fun checkMoreIfEqual() {
        Assertions.assertFalse(QuestUtil.check(mapOf(Pair("money<", listOf("100"))), storyDataModel))
    }

    @Test
    fun checkMoreEqual() {
        Assertions.assertTrue(QuestUtil.check(mapOf(Pair("money>=", listOf("80"))), storyDataModel))
    }
}