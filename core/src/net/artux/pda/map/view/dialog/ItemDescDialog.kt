package net.artux.pda.map.view.dialog

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle
import net.artux.engine.utils.LocaleBundle
import net.artux.pda.model.items.ArmorModel
import net.artux.pda.model.items.WeaponModel
import net.artux.pda.model.items.WearableModel
import javax.inject.Inject


class ItemDescDialog @Inject constructor(skin: Skin, val textButtonStyle: TextButtonStyle, val localeBundle: LocaleBundle) :
    PDADialog("", skin) {

    private val descLabel: Label = Label("", Label.LabelStyle(textButtonStyle.font, textButtonStyle.fontColor))

    init {
        contentTable.add(descLabel).left().grow()
    }

    fun update(wearableModel: WearableModel){
        titleLabel.setText(wearableModel.title)
        if (wearableModel is ArmorModel) {
            descLabel.setText(
                localeBundle.get(
                    "armor.desc",
                    wearableModel.thermalProtection,
                    wearableModel.electricProtection,
                    wearableModel.chemicalProtection,
                    wearableModel.radioProtection,
                    wearableModel.psyProtection,
                    wearableModel.damageProtection,
                    wearableModel.condition
                )
            )
        } else if (wearableModel is WeaponModel) {
            descLabel.setText(
                localeBundle.get(
                    "weapon.desc",
                    wearableModel.precision,
                    wearableModel.speed,
                    wearableModel.damage,
                    wearableModel.condition
                )
            )
        }
    }

}