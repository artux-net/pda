package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.artux.pda.common.PropertyFields
import net.artux.pda.flow.FlowPlatformInterface
import net.artux.pda.flow.PdaFlowGame
import net.artux.pda.flow.network.dto.toModel
import net.artux.pda.map.GdxAdapter
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.items.ItemsContainerModel
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.quest.ChapterModel
import net.artux.pda.model.quest.Stage
import net.artux.pda.model.quest.Transfer
import net.artux.pda.model.quest.story.StoryDataModel
import java.util.Properties

/**
 * Dialogue/choice engine - the libGDX equivalent of Android's StageFragment/StageRootFragment
 * + QuestViewModel.setStage()'s default branch, driving through the same Stage/ChapterModel
 * (:model) data. typeStage 5/6 (seller/processData) aren't handled - out of scope, see plan;
 * only the default (dialogue) case and typeStage 4 (map hand-off) are.
 */
class StageScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val titleLabel = Label("", skin, "title")
    private val textLabel = TypewriterLabel(skin).apply { wrap = true }
    private val transfersTable = Table()
    private val status = errorLabel()

    private var chapter: ChapterModel? = null
    private var currentStage: Stage? = null
    private var transfersLocked = false

    init {
        root.add(titleLabel).padBottom(10f).row()

        val textScroll = ScrollPane(textLabel, skin)
        textScroll.setScrollingDisabled(true, false)
        root.add(textScroll).width(700f).height(220f).padBottom(20f).row()

        root.add(transfersTable).width(700f).row()
        root.add(status).width(700f).row()

        loadChapterAndShowStage()
    }

    private fun loadChapterAndShowStage() {
        val story = game.session.story
        if (story == null) {
            status.setText("Сюжет не выбран")
            return
        }
        val resolvedChapter = story.getChapter(game.session.currentChapterId.toString())
        if (resolvedChapter == null) {
            status.setText("Не удалось найти главу ${game.session.currentChapterId}")
            return
        }
        chapter = resolvedChapter
        val stage = resolvedChapter.getStage(game.session.currentStageId)
        if (stage == null) {
            status.setText("Не удалось найти стадию ${game.session.currentStageId}")
            return
        }
        showStage(stage)
    }

    private fun showStage(stage: Stage) {
        currentStage = stage
        transfersLocked = false
        game.session.currentStageId = stage.id
        status.setText("")
        titleLabel.setText(stage.title ?: "")

        val storyData = game.session.storyData
        val text = stage.texts
            .filter { storyData == null || QuestUtil.check(it.condition, storyData) }
            .firstOrNull()
            ?.text
            ?: "Недостижимые условия текста"
        textLabel.setFullText(text)

        transfersTable.clear()
        // Same fallback StageMapper.getTransfers() has: if no transfer's condition actually
        // passes, show every transfer anyway (marked "unreachable" on Android) rather than
        // stranding the player with no way forward.
        val passable = stage.transfers.filter { storyData == null || QuestUtil.check(it.condition, storyData) }
        val toShow = if (passable.isEmpty()) stage.transfers else passable
        toShow.forEach { transfer -> addTransferButton(transfer) }
    }

    private fun addTransferButton(transfer: Transfer) {
        val button = TextButton(transfer.text ?: "Далее", skin)
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) = onTransferChosen(transfer)
        })
        transfersTable.add(button).width(680f).height(50f).padBottom(8f).row()
    }

    private fun onTransferChosen(transfer: Transfer) {
        if (transfersLocked || textLabel.isRevealing) {
            if (textLabel.isRevealing) textLabel.skipToEnd()
            return
        }
        val stage = currentStage ?: return
        val chapterModel = chapter ?: return
        val nextStage = chapterModel.getStage(transfer.stage.toLong())
        if (nextStage == null) {
            status.setText("Не удалось найти стадию ${transfer.stage}")
            return
        }

        transfersLocked = true
        val (email, password) = game.session.credentialsOrThrow()
        status.setText("Синхронизация...")

        // Mirrors QuestViewModel.prepareSync()/chooseTransfer(): sync this stage's actions to
        // the server before moving on. The full local command pipeline (sound, notifications,
        // Lua scripts, the "which commands actually need a sync" filtering CommandController
        // does) isn't ported here - out of scope for this flow, see plan; every stage's actions
        // are synced unconditionally instead.
        runIO({ game.api.applyCommands(stage.actions ?: emptyMap(), email, password) }) { result ->
            result.onSuccess { dto ->
                game.session.storyData = dto.toModel()
                advanceTo(nextStage)
            }.onFailure {
                transfersLocked = false
                status.setText("Ошибка синхронизации: ${it.message}")
            }
        }
    }

    private fun advanceTo(stage: Stage) {
        if (stage.typeStage == 4) {
            loadMapAndHandOff(stage)
        } else {
            showStage(stage)
        }
    }

    private fun loadMapAndHandOff(stage: Stage) {
        val data = stage.data
        val mapId = data?.get("map")?.toLongOrNull()
        val story = game.session.story
        if (mapId == null || story == null) {
            status.setText("Указан тип стадии - карта, но id не задан")
            transfersLocked = false
            return
        }
        val (email, password) = game.session.credentialsOrThrow()
        status.setText("Загрузка карты...")

        runIO({ game.api.getMap(story.id, mapId, email, password) }) { result ->
            result.onSuccess { dto ->
                val map = dto.toModel()
                data["pos"]?.let { map.defPos = it }
                startMap(map, email, password)
            }.onFailure {
                transfersLocked = false
                status.setText("Не удалось загрузить карту: ${it.message}")
            }
        }
    }

    private fun startMap(map: GameMap, email: String, password: String) {
        val storyData: StoryDataModel = game.session.storyData ?: return
        val story = game.session.story ?: return

        val properties = Properties()
        // Same defaults MockDataFactory used, for the same reasons (RandomSpawnerHelper/
        // HeaderInterfaceModule read these unconditionally on map load - see its comment).
        properties[PropertyFields.TESTER_MODE] = "false"
        properties[PropertyFields.GROUP_BOT_FREQ] = "60"
        properties[PropertyFields.SINGLE_BOT_FREQ] = "30"

        val adapter = GdxAdapter.Builder(FlowPlatformInterface(game.api, email, password))
            .storyData(storyData)
            .story(story)
            .map(map)
            .items(ItemsContainerModel())
            .props(properties)
            // Gdx.app is already set by this point (unlike MockDataFactory, which builds its
            // GdxAdapter before "new IOSApplication(...)" runs) - no need for a standalone
            // console logger here.
            .logger(Gdx.app.applicationLogger)
            .build()

        game.goTo(GdxAdapterScreen(adapter))
    }
}
