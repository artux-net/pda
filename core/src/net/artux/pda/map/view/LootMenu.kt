package net.artux.pda.map.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.DataRepository
import net.artux.pda.map.content.assets.AssetsFinder
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.utils.di.scope.PerGameMap
import net.artux.pda.map.view.blocks.MediaItem
import net.artux.pda.map.view.blocks.SlotTextButton
import net.artux.pda.map.view.view.ItemsTableView
import net.artux.pda.map.view.view.OnItemClickListener
import net.artux.pda.map.view.view.bars.Utils
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.ItemsHelper
import net.artux.pda.model.quest.story.StoryDataModel
import org.apache.commons.lang3.SerializationUtils
import javax.inject.Inject


@PerGameMap
class LootMenu @Inject constructor(
    textButton: SlotTextButton,
    assetsFinder: AssetsFinder,
    private val localeBundle: LocaleBundle,
    private val dataRepository: DataRepository,
    skin: Skin
) : Table() {

    private var assetManager: AssetManager
    private var fontManager: FontManager
    private var mainInfo: MediaItem
    private var mainItemsView: ItemsTableView

    private var botInfo: MediaItem
    private var botItemsView: ItemsTableView

    private var lastDataModel: StoryDataModel
    private var lastBotItems: MutableList<ItemModel> = mutableListOf()

    fun update(dataModel: StoryDataModel) {
        lastDataModel = dataModel

        Gdx.app.postRunnable {
            val models = dataModel.allItems
            mainItemsView.update(models)
        }
        botItemsView.update(lastBotItems)
    }

    fun updateBot(nickname: String, avatar: String, items: List<ItemModel>) {
        lastBotItems.clear()
        lastBotItems.addAll(items)
        Gdx.app.postRunnable {
            botInfo.setTitle(nickname)
            botInfo.setImage(avatar)
            botItemsView.update(items)
        }
    }


    init {
        assetManager = assetsFinder.manager
        top()
        setFillParent(true)
        defaults()
            .pad(10f)
            .space(20f)
        defaults().fill()
        lastDataModel = dataRepository.storyDataModel
        fontManager = assetsFinder.fontManager

        val titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE)
        val subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY)

        val leftTable = Table()
        leftTable.top().left()

        botInfo = MediaItem("", "", "", titleLabelStyle, subtitleStyle, assetManager)
        mainInfo = MediaItem(
            lastDataModel.avatar,
            lastDataModel.name + " " + lastDataModel.nickname,
            "",
            titleLabelStyle,
            subtitleStyle,
            assetManager
        )

        leftTable.add(botInfo).growX().uniformX()
        leftTable.add(mainInfo).growX().uniformX()

        leftTable.row()

        botItemsView = ItemsTableView(
            localeBundle["main.inventory"],
            titleLabelStyle,
            subtitleStyle,
            assetsFinder,
            skin
        )
        leftTable.add(botItemsView)
            .grow()
            .uniformY()

        mainItemsView = ItemsTableView(
            localeBundle["main.inventory"],
            titleLabelStyle,
            subtitleStyle,
            assetsFinder,
            skin
        )


        leftTable.add(mainItemsView)
            .grow()
            .uniformY()

        add(leftTable)
            .colspan(2)
            .grow()


        val stalkerItemClickListener = object : OnItemClickListener {
            override fun onTap(itemModel: ItemModel) {
                lastBotItems.remove(itemModel)
                lastDataModel.addItem(itemModel)
                dataRepository.update()
            }

            override fun onLongPress(itemModel: ItemModel) {

            }
        }

        val playerItemClickListener = object : OnItemClickListener {
            override fun onTap(itemModel: ItemModel) {
                ItemsHelper.add(lastBotItems, SerializationUtils.clone(itemModel))
                itemModel.quantity = 0
                dataRepository.update()
            }

            override fun onLongPress(itemModel: ItemModel) {

            }
        }
        mainItemsView.setOnClickListener(playerItemClickListener)

        botItemsView.setOnClickListener(stalkerItemClickListener)
        background = Utils.getColoredDrawable(1, 1, Colors.backgroundColor)
        touchable = Touchable.enabled
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                update(it)
            }
        }
    }


}