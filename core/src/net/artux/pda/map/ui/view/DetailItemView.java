package net.artux.pda.map.ui.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.ui.units.LazyImage;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;

import java.text.DecimalFormat;

public class DetailItemView extends Table {

    private final String iconFilename;
    private final Image image;

    private static final DecimalFormat formater = new DecimalFormat("##.##");

    public DetailItemView(WearableModel itemModel, Label.LabelStyle titleStyle, Label.LabelStyle descStyle, LocaleBundle localeBundle, AssetManager assetManager) {
        super();
        iconFilename = "icons/items/" + itemModel.getIcon();

        image = new LazyImage(assetManager, iconFilename);
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);

        Label title = new Label(itemModel.getTitle(), titleStyle);
        title.setAlignment(Align.center);
        add(title)
                .colspan(2)
                .row();

        add(image)
                .grow()
                .center();

        VerticalGroup detailRootView = new VerticalGroup();
        Label descLabel = new Label(itemModel.getTitle(), descStyle);
        descLabel.setAlignment(Align.left);
        descLabel.setText(getDesc(localeBundle, itemModel));
        detailRootView.addActor(descLabel);
        add(detailRootView)
                .fill();
    }

    private String getDesc(LocaleBundle localeBundle, WearableModel wearableModel) {
        if (wearableModel instanceof ArmorModel) {
            ArmorModel armorModel = (ArmorModel) wearableModel;
            return localeBundle.get("armor.desc", armorModel.getThermalProtection(), armorModel.getElectricProtection(), armorModel.getChemicalProtection(), armorModel.getRadioProtection(), armorModel.getPsyProtection(), armorModel.getDamageProtection(), armorModel.getCondition());
        }
        if (wearableModel instanceof WeaponModel) {
            WeaponModel weaponModel = (WeaponModel) wearableModel;
            return localeBundle.get("weapon.desc", weaponModel.getPrecision(), weaponModel.getSpeed(), weaponModel.getDamage(), weaponModel.getCondition());
        }
        return localeBundle.get("item.desc.empty");
    }
}
