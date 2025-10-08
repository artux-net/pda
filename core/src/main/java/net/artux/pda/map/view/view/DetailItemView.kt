package net.artux.pda.map.view.view

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Scaling
import net.artux.engine.ui.ScalableLabel
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.map.view.dialog.ItemDescDialog
import net.artux.pda.map.view.image.LazyImage
import net.artux.pda.map.view.view.bars.Bar
import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.items.WearableModel
import javax.inject.Inject
import javax.inject.Named

class DetailItemView @Inject constructor(
    @Named("titleStyle") titleStyle: LabelStyle,
    @Named("descStyle") descStyle: LabelStyle,
    private val localeBundle: LocaleBundle,
    assetManager: AssetManager,
    private val dialog: ItemDescDialog
) : Table() {
    private val titleLabel: Label
    private val descLabel: Label
    private val conditionBar: Bar
    private val image: LazyImage
    private var wearableModel: WearableModel? = null
    private var lastCondition = 0f
    private val baseColor = Color(1f, 1f, 0f, 1f)

    init {
        image = LazyImage(assetManager)
        image.setScaling(Scaling.fit)
        image.align = Align.center
        titleLabel = ScalableLabel("", titleStyle)
        titleLabel.wrap = true
        titleLabel.setAlignment(Align.center)
        add<Label>(titleLabel)
            .colspan(2)
            .fill()
            .row()
        add(image)
            .grow()
            .center()
        val detailRootView = VerticalGroup()
        descLabel = Label("", descStyle)
        descLabel.setAlignment(Align.left)
        detailRootView.addActor(descLabel)
        add(detailRootView)
            .fill()
        row()
        conditionBar = Bar(Color.GREEN)
        add(conditionBar)
            .fillX()
            .colspan(2)
        addListener(object : ActorGestureListener() {
            override fun tap(event: InputEvent, x: Float, y: Float, count: Int, button: Int) {
                super.tap(event, x, y, count, button)
                if (wearableModel == null)
                    return
                dialog.update(wearableModel!!)
                dialog.show(stage)
            }
        })
    }

    fun disableDesc() {
        descLabel.remove()
    }

    private fun getDesc(localeBundle: LocaleBundle, wearableModel: WearableModel): String {
        if (wearableModel is ArmorModel) {
            val (thermalProtection, electricProtection, chemicalProtection, radioProtection, psyProtection, damageProtection, condition) = wearableModel
            return localeBundle["armor.desc", thermalProtection, electricProtection, chemicalProtection, radioProtection, psyProtection, damageProtection, condition]
        }
        if (wearableModel is WeaponModel) {
            val (precision, speed, damage, condition) = wearableModel
            return localeBundle["weapon.desc", precision, speed, damage, condition]
        }
        return localeBundle["item.desc.empty"]
    }

    fun setWearableModel(itemModel: WearableModel?) {
        wearableModel = itemModel
        if (itemModel == null) {
            image.isVisible = false
            titleLabel.setText(localeBundle["item.title.empty"])
            descLabel.setText("")
            conditionBar.isVisible = false
        } else {
            var condition = 100f
            if (itemModel is WeaponModel) condition =
                itemModel.condition else if (itemModel is ArmorModel) condition =
                itemModel.condition
            image.setFilename("textures/icons/items/" + itemModel.icon)
            image.isVisible = true
            titleLabel.setText(itemModel.title)
            //descLabel.setText(getDesc(localeBundle, itemModel)); // todo there is a bug with new lines
            conditionBar.isVisible = true
            baseColor[1f, 1f, 0f] = 1f
            val k = (condition - 75f) / 25f
            if (k > 0) baseColor.r -= k else baseColor.g -= k
            conditionBar.value = condition
            conditionBar.color = baseColor
        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        var condition = 100f
        if (wearableModel is WeaponModel) condition =
            (wearableModel as WeaponModel).condition else if (wearableModel is ArmorModel) condition =
            (wearableModel as ArmorModel).condition
        if (condition != lastCondition) {
            lastCondition = condition
            val k = (condition - 75f) / 25f
            if (k > 0) baseColor.r -= k else baseColor.g -= k
            conditionBar.value = condition
            conditionBar.color = baseColor
        }
    }
}