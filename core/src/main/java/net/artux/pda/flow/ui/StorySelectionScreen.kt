package net.artux.pda.flow.ui

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Value
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.artux.pda.flow.PdaFlowGame
import net.artux.pda.flow.network.dto.ProfileInfo
import net.artux.pda.flow.network.dto.StoryDataDto
import net.artux.pda.flow.network.dto.StoryDto
import net.artux.pda.flow.network.dto.toInfo
import net.artux.pda.flow.network.dto.toModel
import net.artux.pda.model.user.Gang

/**
 * activity_main.xml's two-panel PDA device chrome (MainActivity - see FlowSkin's "pda-*"
 * drawables, ported from app/res/drawable), repurposed here: the left panel is the story list
 * (MainActivity's containerView), the right one a read-only profile summary (its profileButton
 * tab), since this flow has no equivalent of MainActivity's other tabs (news/messages/notes).
 */
class StorySelectionScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val status = errorLabel()
    private val listTable = Table()
    private val profileTable = Table()

    init {
        val content = Table()

        val storiesPanel = pdaPanel(
            title = "Сюжеты",
            mainBg = "pda-main-bg",
            titleBg = "pda-title-bg",
            bottomBg = "pda-bottom-bg"
        ) { body ->
            body.add(status).growX().padBottom(8f).row()
            val scrollPane = ScrollPane(listTable, skin)
            scrollPane.setScrollingDisabled(true, false)
            body.add(scrollPane).grow().row()
        }

        val profilePanel = pdaPanel(
            title = "Профиль",
            mainBg = "pda-side-bg",
            titleBg = "pda-side-title-bg",
            bottomBg = "pda-side-bottom-bg"
        ) { body ->
            body.add(profileTable).grow().top().row()
        }

        content.add(storiesPanel).grow().width(Value.percentWidth(0.6f, content)).padRight(10f)
        content.add(profilePanel).grow().width(Value.percentWidth(0.4f, content))

        root.add(content).grow().pad(10f)

        loadStories()
        loadProfile()
    }

    /** One PDA panel: main_background behind a title_background strip, body, bottom_background. */
    private fun pdaPanel(
        title: String,
        mainBg: String,
        titleBg: String,
        bottomBg: String,
        body: (Table) -> Unit
    ): Table {
        val panel = Table()
        panel.background = skin.getDrawable(mainBg)

        val titleBar = Table()
        titleBar.background = skin.getDrawable(titleBg)
        titleBar.add(Label(title, skin, "hint")).left().pad(2f, 10f, 2f, 10f)
        panel.add(titleBar).growX().height(26f).pad(10f, 12f, 6f, 12f).row()

        val bodyTable = Table()
        body(bodyTable)
        panel.add(bodyTable).grow().pad(0f, 16f, 6f, 16f).row()

        val bottomBar = Table()
        bottomBar.background = skin.getDrawable(bottomBg)
        panel.add(bottomBar).growX().height(30f).pad(0f, 12f, 10f, 12f)

        return panel
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
                    val card = storyCard(item.title, item.desc)
                    card.addListener(object : ClickListener() {
                        override fun clicked(event: InputEvent?, x: Float, y: Float) {
                            selectStory(item.id.toLong())
                        }
                    })
                    listTable.add(card).width(CARD_WIDTH).padBottom(8f).row()
                }
            }.onFailure {
                status.setText("Не удалось загрузить список сюжетов: ${it.message}")
            }
        }
    }

    private fun loadProfile() {
        val (email, password) = game.session.credentialsOrThrow()
        profileTable.clear()
        profileTable.add(Label("Загрузка...", skin, "hint")).left()

        runIO({ game.api.getProfile(email, password) }) { result ->
            result.onSuccess { showProfile(it.toInfo()) }
                .onFailure {
                    profileTable.clear()
                    profileTable.add(
                        Label("Не удалось загрузить профиль", skin, "hint").apply { wrap = true }
                    ).growX()
                }
        }
    }

    private fun showProfile(info: ProfileInfo) {
        profileTable.clear()
        profileTable.defaults().left().padBottom(10f)

        if (info.nickname.isNotBlank())
            profileTable.add(Label(info.nickname, skin, "card-title").apply { wrap = true }).growX().row()

        profileTable.add(Label(GANG_NAMES[info.gang] ?: info.gang.name, skin, "default")).row()
        profileTable.add(Label("Ранг: " + RANG_NAMES.getOrElse(info.rang.id) { info.rang.name }, skin, "hint")).row()
        profileTable.add(Label("Рейтинг: #${info.ratingPosition}", skin, "hint")).row()
        info.daysInGame?.let { days ->
            profileTable.add(Label("В игре: $days ${daysWord(days)}", skin, "hint")).row()
        }
    }

    private fun daysWord(days: Int): String {
        val mod100 = days % 100
        val mod10 = days % 10
        return when {
            mod100 in 11..14 -> "дней"
            mod10 == 1 -> "день"
            mod10 in 2..4 -> "дня"
            else -> "дней"
        }
    }

    /** item_story.xml's title + description (minus the icon), on a choice-style block. */
    private fun storyCard(title: String, desc: String): Button {
        val card = Button(skin, "choice")
        card.add(Label(title, skin, "card-title").apply { wrap = true }).growX().row()
        if (desc.isNotBlank()) {
            card.add(Label(desc, skin, "hint").apply { wrap = true }).growX().padTop(4f)
        }
        return card
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

    private companion object {
        const val CARD_WIDTH = 420f

        // app/res/values/strings.xml's groups/rang string-arrays - the flow has no LocaleBundle
        // of its own (see FlowSkin's doc comment), so these are copied over directly.
        val GANG_NAMES = mapOf(
            Gang.LONERS to "Одиночки",
            Gang.BANDITS to "Бандиты",
            Gang.MILITARY to "Военные",
            Gang.LIBERTY to "Свобода",
            Gang.DUTY to "Долг",
            Gang.MONOLITH to "Монолит",
            Gang.MERCENARIES to "Наёмники",
            Gang.SCIENTISTS to "Учёные",
            Gang.CLEAR_SKY to "Чистое небо"
        )
        val RANG_NAMES = listOf("Отмычка", "Новичок", "Сталкер", "Опытный", "Ветеран", "Мастер", "Легенда")
    }
}
