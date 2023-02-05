package net.artux.pda.map.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.engine.AssetsFinder
import net.artux.pda.map.engine.ecs.systems.SoundsSystem
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.view.blocks.MediaItem
import net.artux.pda.map.view.blocks.SlotTextButton
import net.artux.pda.map.view.view.ItemsTableView
import net.artux.pda.map.view.view.OnItemClickListener
import net.artux.pda.map.view.view.bars.Utils
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject


@PerGameMap
class LootMenu @Inject constructor(
    userInterface: UserInterface,
    platformInterface: PlatformInterface,
    textButton: SlotTextButton,
    assetsFinder: AssetsFinder,
    playerSystem: PlayerSystem,
    private val localeBundle: LocaleBundle,
    private val dataRepository: DataRepository,
    skin: Skin,
    soundsSystem: SoundsSystem
) : Table() {

    private var assetManager: AssetManager
    private var fontManager: FontManager
    private var mainInfo: MediaItem
    private var mainItemsView: ItemsTableView

    private var botInfo: MediaItem
    private var botItemsView: ItemsTableView

    private lateinit var lastDataModel: StoryDataModel

    fun update(dataModel: StoryDataModel) {
        lastDataModel = dataModel

        Gdx.app.postRunnable {
            val models = dataModel.allItems
            mainItemsView.update(models)
        }
    }

    fun updateBot(nickname: String, avatar: String, items: List<ItemModel>) {
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
        lastDataModel = dataRepository.currentStoryDataModel
        fontManager = assetsFinder.fontManager

        val titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE)
        val subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY)

        val leftTable = Table()
        leftTable.top().left()
        textButton.setText(localeBundle["main.close"])
        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                remove()
            }
        })

        add(textButton)
            .left()
            .row()

        botInfo = MediaItem("", "", "", titleLabelStyle, subtitleStyle, assetManager)
        mainInfo = MediaItem("avatar/1.png",
            lastDataModel.name + " " + lastDataModel.nickname, "", titleLabelStyle, subtitleStyle, assetManager)
        //TODO avatar
        leftTable.add(botInfo).growX().uniform()
        leftTable.add(mainInfo).growX().uniform()

        leftTable.row()

        botItemsView = ItemsTableView(
            localeBundle["main.inventory"],
            titleLabelStyle,
            subtitleStyle,
            assetsFinder,
            skin
        )
        leftTable.add(botItemsView)
            .uniform()
            .grow()

        mainItemsView = ItemsTableView(
            localeBundle["main.inventory"],
            titleLabelStyle,
            subtitleStyle,
            assetsFinder,
            skin
        )

        leftTable.add(mainItemsView)
            .uniform()
            .grow()


        add(leftTable)
            .grow()


        val onItemClickListener = object : OnItemClickListener {
            override fun onTap(itemModel: ItemModel) {
                /*if (itemModel is MedicineModel) {
                    val model = itemModel
                    if (model.quantity > 0) {
                        model.quantity = model.quantity - 1
                        playerSystem.healthComponent.treat(itemModel as MedicineModel?)
                        soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"))
                    }
                    dataRepository.update()
                } else if (itemModel is WearableModel) {
                    lastDataModel.setCurrentWearable(itemModel as WearableModel?)
                    soundsSystem.playSound(assetManager.get("audio/sounds/person/equip.ogg"))
                    dataRepository.update()
                }*/
            }

            override fun onLongPress(itemModel: ItemModel) {

            }

        }

        botItemsView.setOnClickListener(onItemClickListener)
        background = Utils.getColoredDrawable(1, 1, Colors.backgroundColor)
        touchable = Touchable.enabled
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                update(it)
            }
        }
    }


}