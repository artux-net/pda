package net.artux.pda.flow.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import net.artux.pda.common.PropertyFields
import net.artux.pda.flow.FlowPlatformInterface
import net.artux.pda.flow.PdaFlowGame
import net.artux.pda.flow.StageRules
import net.artux.pda.flow.network.FlowApiClient
import net.artux.pda.flow.network.dto.toModel
import net.artux.pda.map.GdxAdapter
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
 *
 * The stage it opens with is the player's saved position, which may itself be a map stage
 * (the usual state after the prologue, or after playing on Android) - that goes straight to
 * the map, like QuestViewModel.beginWithStage() -> setStage() does.
 */
class StageScreen(game: PdaFlowGame) : BaseFlowScreen(game) {

    private val titleLabel = Label("", skin, "title")
    private val textLabel = TypewriterLabel(skin).apply { wrap = true }
    private val transfersTable = Table()
    private val status = errorLabel()
    private val background = StageBackground(game.api)

    private var chapter: ChapterModel? = null
    private var currentStage: Stage? = null
    private var transfersLocked = false
    private var jumpsInARow = 0

    init {
        // Behind BaseFlowScreen's scrolled content.
        stage.root.addActorAt(0, background.image)

        root.add(titleLabel).padBottom(10f).row()

        // fragment_quest0's sceneText: the text sits on a black_overlay panel.
        val textScroll = ScrollPane(textLabel, skin, "panel")
        textScroll.setScrollingDisabled(true, false)
        // Short enough that 3-4 transfer buttons still fit below it on a landscape phone;
        // BaseFlowScreen scrolls the rest.
        root.add(textScroll).width(700f).height(150f).padBottom(15f).row()

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
        // Already the server's current position - no "state" sync needed to resume it.
        enter(stage)
    }

    private fun enter(stage: Stage) {
        game.session.currentStageId = stage.id
        if (StageRules.exitsStory(stage)) {
            // The server already applied finishStory/exitStory with this stage's "state".
            game.goTo(StorySelectionScreen(game))
            return
        }
        val jump = StageRules.jump(stage)
        if (jump != null) {
            jumpTo(jump)
            return
        }
        jumpsInARow = 0
        if (stage.typeStage == StageRules.TYPE_MAP) {
            loadMapAndHandOff(stage)
        } else {
            showStage(stage)
        }
    }

    /** Follows a stage's own redirect (see StageRules.jump), possibly into another chapter. */
    private fun jumpTo(jump: StageRules.Jump) {
        if (++jumpsInARow > MAX_JUMPS_IN_A_ROW) {
            status.setText("Стадии ссылаются друг на друга по кругу")
            return
        }
        val story = game.session.story ?: return
        val chapterId = jump.chapterId ?: game.session.currentChapterId
        val targetChapter = story.getChapter(chapterId.toString())
        val target = targetChapter?.getStage(jump.stageId)
        if (targetChapter == null || target == null) {
            status.setText("Не удалось найти стадию ${jump.stageId} в главе $chapterId")
            return
        }
        chapter = targetChapter
        game.session.currentChapterId = chapterId
        if (!jump.sync) {
            enter(target)
            return
        }
        val (email, password) = game.session.credentialsOrThrow()
        transfersTable.clear()
        status.setText("Синхронизация...")
        runIO({ game.api.enterStage(story.id, chapterId, target.id, email, password) }) { result ->
            result.onSuccess { dto ->
                game.session.storyData = dto.toModel()
                enter(target)
            }.onFailure {
                status.setText("Ошибка синхронизации: ${it.message}")
                addRetryButton { jumpTo(jump) }
            }
        }
    }

    private fun showStage(stage: Stage) {
        currentStage = stage
        transfersLocked = false
        status.setText("")
        titleLabel.setText(stage.title ?: "")
        background.show(stage.background)

        val storyData = game.session.storyData
        textLabel.setFullText(StageRules.visibleText(stage, storyData) ?: "Недостижимые условия текста")

        transfersTable.clear()
        val transfers = StageRules.visibleTransfers(stage, storyData)
        transfers.forEach { transfer -> addTransferButton(transfer) }
        background.prefetch(transfers.map { chapter?.getStage(it.stage.toLong())?.background })
    }

    private fun addTransferButton(transfer: Transfer) {
        val button = choiceButton(transfer.text ?: "Далее")
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) = onTransferChosen(transfer)
        })
        transfersTable.add(button).width(700f).padBottom(8f).row()
    }

    private fun onTransferChosen(transfer: Transfer) {
        if (transfersLocked || textLabel.isRevealing) {
            if (textLabel.isRevealing) textLabel.skipToEnd()
            return
        }
        if (currentStage == null) return
        val chapterModel = chapter ?: return
        val story = game.session.story ?: return
        val nextStage = chapterModel.getStage(transfer.stage.toLong())
        if (nextStage == null) {
            status.setText("Не удалось найти стадию ${transfer.stage}")
            return
        }

        transfersLocked = true
        val (email, password) = game.session.credentialsOrThrow()
        status.setText("Синхронизация...")

        // Mirrors QuestViewModel.chooseTransfer() -> prepareSync() -> syncNow(): record the
        // stage being entered as the current position; the server applies that stage's actions
        // itself. The local-only command pipeline (sound, notifications, Lua) isn't ported here.
        val chapterId = game.session.currentChapterId
        runIO({ game.api.enterStage(story.id, chapterId, nextStage.id, email, password) }) { result ->
            result.onSuccess { dto ->
                game.session.storyData = dto.toModel()
                enter(nextStage)
            }.onFailure {
                transfersLocked = false
                status.setText("Ошибка синхронизации: ${it.message}")
            }
        }
    }

    private fun loadMapAndHandOff(stage: Stage) {
        val target = StageRules.mapTarget(stage)
        val story = game.session.story
        if (target == null || story == null) {
            status.setText("Указан тип стадии - карта, но id не задан")
            transfersLocked = false
            return
        }
        val (email, password) = game.session.credentialsOrThrow()
        titleLabel.setText(stage.title ?: "")
        background.show(stage.background)
        textLabel.setFullText("")
        transfersTable.clear()
        status.setText("Загрузка карты...")

        runIO({ game.api.getMap(story.id, target.mapId, email, password) }) { result ->
            result.onSuccess { dto ->
                val map = dto.toModel()
                target.pos?.let { map.defPos = it }
                startMap(map, email, password)
            }.onFailure {
                transfersLocked = false
                status.setText("Не удалось загрузить карту: ${it.message}")
                // A map stage has no transfers of its own - without this there'd be no way on.
                addRetryButton { loadMapAndHandOff(stage) }
            }
        }
    }

    /** StageFragment's answer button: full width, start-aligned, wrapping text. */
    private fun choiceButton(text: String): TextButton = TextButton(text, skin, "choice").apply {
        label.setAlignment(Align.left)
        label.wrap = true
    }

    private fun addRetryButton(onRetry: () -> Unit) {
        val button = choiceButton("Повторить")
        button.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) = onRetry()
        })
        transfersTable.add(button).width(700f).padBottom(8f).row()
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
        // NetTextureAssetLoader's base for relative texture paths - unset, they resolved to "null...".
        properties[PropertyFields.RESOURCE_URL] = FlowApiClient.RESOURCE_URL

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

    override fun dispose() {
        background.dispose()
        super.dispose()
    }

    private companion object {
        // Redirect chains are one or two hops in practice; more means stages point at each other.
        const val MAX_JUMPS_IN_A_ROW = 10
    }
}
