package net.artux.pda.map.view

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Slider
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.ui.InputListener
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.DataRepository
import net.artux.pda.map.content.assets.AssetsFinder
import net.artux.pda.map.engine.ecs.systems.SoundsSystem
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.utils.di.scope.PerGameMap
import net.artux.pda.map.view.blocks.SlotTextButton
import net.artux.pda.map.view.view.DetailItemView
import net.artux.pda.map.view.view.HUD
import net.artux.pda.map.view.view.ItemsTableView
import net.artux.pda.map.view.view.OnItemClickListener
import net.artux.pda.map.view.view.bars.Utils
import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.ItemModel
import net.artux.pda.model.items.ItemType
import net.artux.pda.model.items.MedicineModel
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.items.WearableModel
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject


@PerGameMap
class BackpackMenu @Inject constructor(
    userInterface: UserInterface,
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
    private var assetManager: AssetManager
    private var fontManager: FontManager
    private var infoLabel: Label
    private var mainItemsView: ItemsTableView
    private var armorView: DetailItemView
    private var rifleView: DetailItemView
    private var pistolView: DetailItemView
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

        val titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE)
        val subtitleStyle = fontManager.getLabelStyle(30, Color.GRAY)

        val leftTable = Table()
        leftTable.top().left()


        infoLabel = Label("", subtitleStyle)
        leftTable.add(hud)
        leftTable.add(infoLabel)
        leftTable.row()

        armorView = DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
        armorView.skin = userInterface.skin
        leftTable.add(armorView)
            .grow()

        val verticalGroup = Table()
        rifleView = DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
        rifleView.skin = userInterface.skin
        pistolView =
            DetailItemView(null, titleLabelStyle, subtitleStyle, localeBundle, assetManager)
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

        val onItemClickListener = object : OnItemClickListener {
            override fun onTap(itemModel: ItemModel) {
                if (itemModel is MedicineModel) {
                    if (itemModel.quantity > 0) {
                        itemModel.quantity = itemModel.quantity - 1
                        playerSystem.healthComponent.treat(itemModel)
                        soundsSystem.playSound(assetManager.get("audio/sounds/person/medicine.ogg"))
                    }
                    dataRepository.update()
                } else if (itemModel is WearableModel) {
                    lastDataModel.setCurrentWearable(itemModel as WearableModel?)
                    soundsSystem.playSound(assetManager.get("audio/sounds/person/equip.ogg"))
                    dataRepository.update()
                }
            }

            override fun onLongPress(itemModel: ItemModel) {
                val textButtonStyle = TextButtonStyle()
                textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor)
                textButtonStyle.font = titleLabelStyle.font
                textButtonStyle.fontColor = Color.WHITE

                val btnYes = TextButton(localeBundle.get("main.throw"), textButtonStyle)
                val btnNo = TextButton(localeBundle.get("main.cancel"), textButtonStyle)

                val skinDialog: Skin = userInterface.skin
                val dialog: Dialog = object : Dialog("", skinDialog) {}
                dialog.isModal = true
                dialog.isMovable = false
                dialog.isResizable = false

                val slider = Slider(1f, itemModel.quantity.toFloat(), 1f, false, skin)
                slider.style.knob.minWidth = 50f
                slider.style.knob.minHeight = 50f
                val weightLabel =
                    Label(localeBundle.get("item.weight", itemModel.weight), titleLabelStyle)

                val quantityLabel =
                    Label(localeBundle.get("item.quantity", 1), titleLabelStyle)

                slider.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent?, actor: Actor?) {
                        weightLabel.setText(
                            localeBundle.get(
                                "item.weight",
                                itemModel.weight * slider.value.toInt()
                            )
                        )
                        quantityLabel.setText(
                            localeBundle.get(
                                "item.quantity",
                                slider.value.toInt()
                            )
                        )
                    }
                })

                btnYes.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?, x: Float, y: Float,
                        pointer: Int, button: Int
                    ): Boolean {
                        val q = slider.value.toInt()
                        itemModel.quantity -= q
                        dataRepository.update()

                        dialog.hide()
                        dialog.cancel()
                        dialog.remove()
                        return true
                    }
                })

                btnNo.addListener(object : InputListener() {
                    override fun touchDown(
                        event: InputEvent?, x: Float, y: Float,
                        pointer: Int, button: Int
                    ): Boolean {
                        dialog.cancel()
                        dialog.hide()
                        return true
                    }
                })

                val drawable: Drawable = Utils.getColoredDrawable(1, 1, Colors.backgroundColor)
                dialog.background = drawable

                val t = Table()
                t.defaults().space(20f)

                dialog.text(localeBundle.get("item.throw", itemModel.title), titleLabelStyle)
                dialog.contentTable.row()
                dialog.contentTable.add(quantityLabel).center().growX()
                    .row()
                if (itemModel.type.isCountable)
                    dialog.contentTable.add(slider).growX().row()
                dialog.contentTable.add(weightLabel).center().growX()

                t.add(btnYes).grow().uniform()
                t.add(btnNo).grow().uniform()

                dialog.buttonTable.add(t).grow()
                dialog.show(stage)
            }

        }

        textButton.setText(localeBundle["main.openPda"])
        textButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                super.clicked(event, x, y)
                dataRepository.platformInterface.openPDA()
            }
        })
        adButton.setText(localeBundle["main.ad.rewarded"])
        adButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                platformInterface.rewardedAd()
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
        background = Utils.getColoredDrawable(1, 1, Colors.backgroundColor)
        touchable = Touchable.enabled
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                update(it)
            }
        }
    }


}