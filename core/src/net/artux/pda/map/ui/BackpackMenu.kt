package net.artux.pda.map.ui

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import kotlinx.coroutines.runBlocking
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.engine.AssetsFinder
import net.artux.pda.map.engine.systems.SoundsSystem
import net.artux.pda.map.engine.systems.player.PlayerSystem
import net.artux.pda.map.ui.blocks.SlotTextButton
import net.artux.pda.map.ui.view.DetailItemView
import net.artux.pda.map.ui.view.HUD
import net.artux.pda.map.ui.view.ItemsTableView
import net.artux.pda.map.ui.view.OnItemClickListener
import net.artux.pda.map.ui.view.bars.Utils
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.model.items.*
import javax.inject.Inject

@PerGameMap
class BackpackMenu @Inject constructor(
    userInterface: UserInterface?,
    platformInterface: PlatformInterface,
    hud: HUD,
    textButton: SlotTextButton,
    adButton: SlotTextButton,
    assetsFinder: AssetsFinder,
    playerSystem: PlayerSystem,
    private val localeBundle: LocaleBundle,
    private val dataRepository: DataRepository,
    skin: Skin?,
    soundsSystem: SoundsSystem
) : Table() {
    private lateinit var assetManager: AssetManager
    private lateinit var fontManager: FontManager
    private lateinit var infoLabel: Label
    private lateinit var mainItemsView: ItemsTableView
    private lateinit var armorView: DetailItemView
    private lateinit var rifleView: DetailItemView
    private lateinit var pistolView: DetailItemView
    fun update() {
        val dataModel = dataRepository.storyDataModelFlow.value
        val models = dataModel.allItems
        mainItemsView.update(models)
        infoLabel.setText(localeBundle["user.info", dataModel.money, dataModel.xp])
        updateWearableInfo()
    }

    fun updateWearableInfo() {
        val currentData = dataRepository.storyDataModelFlow.value

        val armorModel = currentData.getEquippedWearable(ItemType.ARMOR) as ArmorModel
        armorView.setWearableModel(armorModel)

        var weaponModel = currentData.getEquippedWearable(ItemType.RIFLE) as WeaponModel
        rifleView.setWearableModel(weaponModel)

        weaponModel = currentData.getEquippedWearable(ItemType.PISTOL) as WeaponModel
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
        adButton.setText(localeBundle["main.ad.rewarded"])
        adButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                platformInterface.rewardedAd()
            }
        })
        infoLabel = Label("", subtitleStyle)
        val verticalGroup = VerticalGroup()
        var buttonsTable = Table()
        buttonsTable.center()
        buttonsTable.defaults().center().space(20f)
        buttonsTable.add(textButton).uniformX()
        buttonsTable.add(adButton).uniformX()
        verticalGroup.addActor(buttonsTable)
        buttonsTable = Table()
        buttonsTable.center()
        buttonsTable.defaults().space(20f).center()
        buttonsTable.add(hud)
        buttonsTable.add(infoLabel)
        buttonsTable.row()
        verticalGroup.addActor(buttonsTable)
        leftTable.add(verticalGroup)
            .left().top().row()
        armorView = DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
        leftTable.add(armorView)
            .fill()
        val horizontalGroup = Table()
        horizontalGroup.center()
        rifleView = DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
        pistolView =
            DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
        pistolView.disableDesc()
        rifleView.disableDesc()
        armorView.disableDesc()
        horizontalGroup.add(rifleView).fill().uniform()
        horizontalGroup.add(pistolView).fill().uniform()
        leftTable.row()
        leftTable.add(horizontalGroup).growX().center()
        add(leftTable)
            .colspan(1)
            .left()
            .uniformX().fill()
        mainItemsView = ItemsTableView(
            localeBundle["main.inventory"],
            titleLabelStyle,
            subtitleStyle,
            assetsFinder,
            skin
        )
        add(mainItemsView)
            .left()
            .colspan(2)
            .growX()
        val onItemClickListener = OnItemClickListener { itemModel: ItemModel? ->
            if (itemModel is MedicineModel) {
                val model = itemModel
                if (model.quantity > 0) {
                    model.quantity = model.quantity - 1
                    playerSystem.healthComponent.treat(itemModel as MedicineModel?)
                    soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"))
                }
                update()
            } else if (itemModel is WearableModel) {
                val current = dataRepository.storyDataModelFlow.value
                current.setCurrentWearable(itemModel as WearableModel?)
                dataRepository.storyDataModelFlow.value = current
                soundsSystem.playSound(assetManager.get("audio/sounds/person/equip.ogg"))
            }
        }
        mainItemsView.setOnClickListener(onItemClickListener)
        background = Utils.getColoredDrawable(1, 1, Colors.backgroundColor)
        touchable = Touchable.enabled
        runBlocking {
            dataRepository.storyDataModelFlow.collect {
                update()
            }
        }
    }
}