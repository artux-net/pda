package net.artux.pda.map.view.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.engine.ui.ScalableLabel;
import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.view.units.LazyImage;
import net.artux.pda.map.view.view.bars.Bar;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;

public class DetailItemView extends Table {

    private final Label titleLabel;
    private final Label descLabel;
    private final Bar conditionBar;
    private final LocaleBundle localeBundle;
    private final LazyImage image;
    private WearableModel wearableModel;

    public DetailItemView(WearableModel itemModel, Label.LabelStyle titleStyle, Label.LabelStyle descStyle, LocaleBundle localeBundle, AssetManager assetManager) {
        super();
        this.localeBundle = localeBundle;

        image = new LazyImage(assetManager);
        image.setScaling(Scaling.fit);
        image.setAlign(Align.center);

        titleLabel = new ScalableLabel("", titleStyle);
        titleLabel.setWrap(true);
        titleLabel.setAlignment(Align.center);
        add(titleLabel)
                .colspan(2)
                .fill()
                .row();

        add(image)
                .grow()
                .center();

        VerticalGroup detailRootView = new VerticalGroup();
        descLabel = new Label("", descStyle);
        descLabel.setAlignment(Align.left);

        detailRootView.addActor(descLabel);
        add(detailRootView)
                .fill();
        row();

        conditionBar = new Bar(Color.GREEN);
        add(conditionBar)
                .fillX()
                .colspan(2);

        setWearableModel(itemModel);
    }

    public void disableDesc() {
        descLabel.remove();
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

    public void setWearableModel(WearableModel itemModel) {
        this.wearableModel = itemModel;
        if (wearableModel == null) {
            image.setVisible(false);
            titleLabel.setText(localeBundle.get("item.title.empty"));
            descLabel.setText("");
            conditionBar.setVisible(false);
        } else {
            float condition = 100f;
            if (itemModel instanceof WeaponModel)
                condition = ((WeaponModel) itemModel).getCondition();
            else if (itemModel instanceof ArmorModel)
                condition = ((ArmorModel) itemModel).getCondition();

            image.setFilename("icons/items/" + itemModel.getIcon());
            image.setVisible(true);
            titleLabel.setText(itemModel.getTitle());
            //descLabel.setText(getDesc(localeBundle, itemModel)); // todo there is a bug with new lines
            conditionBar.setVisible(true);
            Color baseColor = new Color(1, 1, 0, 1);
            float k = (condition - 75f) / 25f;
            if (k > 0)
                baseColor.r -= k;
            else
                baseColor.g -= k;

            conditionBar.setValue(condition);
            conditionBar.setColor(baseColor);
        }
    }
}
