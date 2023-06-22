package net.artux.pda.map.view.dialog

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import net.artux.engine.ui.InputListener
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.utils.Colors
import net.artux.pda.map.view.FontManager
import net.artux.pda.map.view.UserInterface
import net.artux.pda.map.view.view.bars.Utils
import net.artux.pda.model.items.ItemModel
import javax.inject.Inject

class ThrowItemDialog
@Inject constructor(
    assetManager: AssetManager,
    fontManager: FontManager,
    val localeBundle: LocaleBundle,
    val dataRepository: DataRepository,
    userInterface: UserInterface
) : PDADialog("", userInterface.skin) {


    var weightLabel: Label
    var quantityLabel: Label
    var slider: Slider

    lateinit var itemModel: ItemModel

    fun update(itemModel: ItemModel){
        this.itemModel = itemModel

        weightLabel.setText(localeBundle.get("item.weight", itemModel.weight))
        quantityLabel.setText(localeBundle.get("item.quantity", itemModel.quantity))
        titleLabel.setText(localeBundle.get("item.throw", itemModel.title))

        slider.setRange(1f, itemModel.quantity.toFloat())
        slider.isVisible = itemModel.type.isCountable
    }

    init {
        val titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE)
        val textButtonStyle = TextButton.TextButtonStyle()
        textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor)
        textButtonStyle.font = titleLabelStyle.font
        textButtonStyle.fontColor = Color.WHITE
        val btnYes = TextButton(localeBundle.get("main.throw"), textButtonStyle)
        val btnNo = TextButton(localeBundle.get("main.cancel"), textButtonStyle)
        isModal = true
        isMovable = false
        isResizable = false
        slider = Slider(1f, 100f, 1f, false, skin)
        slider.style.knob.minWidth = 50f
        slider.style.knob.minHeight = 50f
        weightLabel = Label("", titleLabelStyle)
        quantityLabel = Label("", titleLabelStyle)
        slider.addListener(object : ChangeListener() {
            override fun changed(event: ChangeEvent?, actor: Actor?) {
                weightLabel.setText(localeBundle.get("item.weight", itemModel.weight * slider.value.toInt()))
                quantityLabel.setText(localeBundle.get("item.quantity", slider.value.toInt()))
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

                hide()
                cancel()
                remove()
                return true
            }
        })
        btnNo.addListener(object : InputListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                cancel()
                hide()
                return true
            }
        })
        val t = Table()
        t.defaults().space(20f)

        contentTable.add(quantityLabel).center().growX()
            .row()
        contentTable.add(slider).growX().row()
        contentTable.add(weightLabel).center().growX()
        t.add(btnYes).grow().uniform()
        t.add(btnNo).grow().uniform()
        buttonTable.add(t).grow()
    }

}