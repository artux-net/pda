package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.artux.pda.flow.PdaFlowGame
import net.artux.pda.flow.network.dto.StoryDataDto
import net.artux.pda.flow.network.dto.StoryDto
import net.artux.pda.flow.network.dto.toModel

class StorySelectionScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val status = errorLabel()
    private val listTable = Table()

    init {
        root.add(Label("Выберите сюжет", skin, "title")).padBottom(20f).row()
        root.add(status).width(500f).row()

        val scrollPane = ScrollPane(listTable, skin)
        scrollPane.setScrollingDisabled(true, false)
        root.add(scrollPane).width(500f).height(400f).row()

        loadStories()
    }

    private fun loadStories() {
        val (email, password) = game.session.credentialsOrThrow()
        status.setText("Загрузка сюжетов...")

        runIO({ game.api.getStories(email, password) }) { result ->
            result.onSuccess { stories ->
                status.setText(if (stories.isEmpty()) "Нет доступных сюжетов" else "")
                listTable.clear()
                stories.forEach { dto ->
                    val item = dto.toModel()
                    val button = TextButton(item.title, skin)
                    button.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            selectStory(item.id.toLong())
                        }
                    })
                    listTable.add(button).width(480f).height(60f).padBottom(8f).row()
                }
            }.onFailure {
                status.setText("Не удалось загрузить список сюжетов: ${it.message}")
            }
        }
    }

    private fun selectStory(storyId: Long) {
        val (email, password) = game.session.credentialsOrThrow()
        status.setText("Загрузка сюжета...")

        runIO({ loadStoryAndData(storyId, email, password) }) { result ->
            result.onSuccess { (storyDto, storyDataDto) ->
                val story = storyDto.toModel()
                val storyData = storyDataDto.toModel()
                game.session.story = story
                game.session.storyData = storyData

                // Same resolution StoriesViewModel.selectStory does on Android: resume at the
                // saved position for this story if one exists, otherwise start at the default
                // entry point (chapter 1, stage 0 - matching QuestActivity's own intent defaults).
                val state = storyData.storyStates.firstOrNull { it.storyId == storyId.toInt() }
                game.session.currentChapterId = (state?.chapterId ?: 1).toLong()
                game.session.currentStageId = (state?.stageId ?: 0).toLong()

                game.goTo(StageScreen(game))
            }.onFailure {
                status.setText("Не удалось загрузить сюжет: ${it.message}")
            }
        }
    }

    private suspend fun loadStoryAndData(
        storyId: Long,
        email: String,
        password: String
    ): Result<Pair<StoryDto, StoryDataDto>> {
        val storyResult = game.api.getStory(storyId, email, password)
        val story = storyResult.getOrElse { return Result.failure(it) }
        val dataResult = game.api.getStoryData(email, password)
        val data = dataResult.getOrElse { return Result.failure(it) }
        return Result.success(story to data)
    }
}
