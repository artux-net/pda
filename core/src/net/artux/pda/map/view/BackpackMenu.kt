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
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.content.assets.AssetsFinder
import net.artux.pda.map.ecs.sound.AudioSystem
import net.artux.pda.map.ecs.player.PlayerSystem
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.view.blocks.SlotTextButton
import net.artux.pda.map.view.dialog.AdsDialog
import net.artux.pda.map.view.dialog.ThrowItemDialog
import net.artux.pda.map.view.template.PDAWindow
import net.artux.pda.map.view.view.DetailItemView
import net.artux.pda.map.view.view.HUD
import net.artux.pda.map.view.view.ScrollItemsTableView
import net.artux.pda.map.view.view.OnItemClickListener
import net.artux.pda.map.view.view.bars.Utils
import net.artux.pda.model.items.*
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject


@PerGameMap
class BackpackMenu @Inject constructor(
    private val localeBundle: LocaleBundle,
    private val dataRepository: DataRepository,
    private val armorView: DetailItemView,
    private val rifleView: DetailItemView,
    private val pistolView: DetailItemView,

    skin: Skin,
    userInterface: UserInterface,
    textButton: SlotTextButton,
    adButton: SlotTextButton,
    assetsFinder: AssetsFinder,
    playerSystem: PlayerSystem,
    soundsSystem: AudioSystem,
    throwItemDialog: ThrowItemDialog,
    adsDialog: AdsDialog,
    var mainItemsView: ScrollItemsTableView,
    hud: HUD
) : PDAWindow(skin) {
    private var assetManager: AssetManager
    private var fontManager: FontManager
    private var infoLabel: Label
    private lateinit var lastDataModel: StoryDataModel

    fun update(dataModel: StoryDataModel) {
        lastDataModel = dataModel

        Gdx.app.postRunnable {
            val models = dataModel.allItems
            mainItemsView.update(models)
            infoLabel.setText(localeBundle["user.info", dataModel.money, dataModel.xp])
            updateWearableInfo(dataModel)
        }
    }

    private fun updateWearableInfo(dataModel: StoryDataModel) {
        val armorModel = dataModel.getEquippedWearable(ItemType.ARMOR) as ArmorModel?
        armorView.setWearableModel(armorModel)

        var weaponModel = dataModel.getEquippedWearable(ItemType.RIFLE) as WeaponModel?
        rifleView.setWearableModel(weaponModel)

        weaponModel = dataModel.getEquippedWearable(ItemType.PISTOL) as WeaponModel?
        pistolView.setWearableModel(weaponModel)
    }

    init {
        assetManager = assetsFinder.manager
        top()
        setFillParent(true)
        defaults()
            .pad(10f)
            .space(20f)
        defaults().fill()
        fontManager = assetsFinder.fontManager

        val subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY)

        val leftTable = Table()
        leftTable.top().left()


        infoLabel = Label("", subtitleStyle)
        leftTable.add(hud)
        leftTable.add(infoLabel)
        leftTable.row()

        armorView.skin = userInterface.skin
        leftTable.add(armorView)
            .grow()

        val verticalGroup = Table()
        rifleView.skin = userInterface.skin
        pistolView.skin = userInterface.skin
        pistolView.disableDesc()
        rifleView.disableDesc()
        armorView.disableDesc()

        verticalGroup.add(rifleView).fill().uniform()
        verticalGroup.row()
        verticalGroup.add(pistolView).fill().uniform()
        leftTable.add(verticalGroup).grow()
        leftTable.row()
        add(leftTable)
            .colspan(1)
            .left()
            .uniformX().fill()
        mainItemsView.setTitle(localeBundle["main.inventory"])
        add(mainItemsView)
            .left()
            .colspan(2)
            .growX()

        val onItemClickListener = object : OnItemClickListener {
            override fun onTap(itemModel: ItemModel) {
                if (itemModel is MedicineModel) {
                    if (itemModel.quantity > 0) {
                        itemModel.quantity = itemModel.quantity - 1
                        playerSystem.healthComponent.treat(itemModel)
                        soundsSystem.playBySoundId("audio/sounds/person/medicine.ogg")
                    }
                    dataRepository.update()
                } else if (itemModel is WearableModel) {
                    lastDataModel.setCurrentWearable(itemModel as WearableModel?)
                    soundsSystem.playBySoundId("audio/sounds/person/equip.ogg")
                    dataRepository.update()
                }
            }

            override fun onLongPress(itemModel: ItemModel) {
                throwItemDialog.update(itemModel)
                throwItemDialog.show(stage)
            }

        }

        textButton.setText(localeBundle["main.openPda"])
        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                dataRepository.platformInterface.exit()
            }
        })
        adButton.setText(localeBundle["main.ad.rewarded"])
        adButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                adsDialog.show(stage)
            }
        })

        hud.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                remove()
            }
        })

        //buttonsTable.defaults().center().space(20f)
        leftTable.row().space(10f)
        leftTable.add(textButton).uniformX()
        leftTable.add(adButton).uniformX()


        mainItemsView.setOnClickListener(onItemClickListener)
        touchable = Touchable.enabled
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                update(it)
            }
        }
    }


}