package net.artux.pda.map.view.collection.table.item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.view.image.LazyImage;
import net.artux.pda.map.view.Utils;
import net.artux.pda.model.items.ArmorModel;
import net.artux.pda.model.items.ItemModel;
import net.artux.pda.model.items.WeaponModel;
import net.artux.pda.model.items.WearableModel;

import java.text.DecimalFormat;

public class ItemView extends Table {

    private final Image image;

    private static final DecimalFormat formater = new DecimalFormat("##.##");

    public ItemView(ItemModel itemModel, Label.LabelStyle titleStyle, Label.LabelStyle subtitleStyle, AssetManager assetManager) {
        super();
        String iconFilename = "textures/icons/items/" + itemModel.getIcon();

        pad(5);
        Label quantityLabel = new Label(itemModel.getQuantity() + "", subtitleStyle);

        if (itemModel instanceof WearableModel) {
            float condition = 100f;
            if (itemModel instanceof WeaponModel)
                condition = ((WeaponModel) itemModel).getCondition();
            else if (itemModel instanceof ArmorModel)
                condition = ((ArmorModel) itemModel).getCondition();
            if (((WearableModel) itemModel).isEquipped())
                quantityLabel.setText("<!>");

            Color baseColor = new Color(1, 1, 0, 1);
            float k = (condition - 75f) / 25f;
            if (k > 0)
                baseColor.r -= k;
            else
                baseColor.g -= k;
            add(new Image(Utils.getColoredRegion(10, 10, baseColor))).left().uniformX();
        } else add(new Image()).left().uniformX();



        quantityLabel.setAlignment(Align.right);
        add(quantityLabel)
                .right()
                .uniformX()
                .fill();

        row();
        image = new LazyImage(assetManager, iconFilename);
        image.setScaling(Scaling.fit);
        add(image)
                .grow()
                .colspan(2);
        row();

        Label title = new Label(itemModel.getTitle(), titleStyle);
        title.setWrap(true);
        title.setAlignment(Align.left);
        add(title)
                .fill()
                .growX()
                .colspan(2);
        row();

        Label subtitle = new Label(formater.format(itemModel.getWeight() * itemModel.getQuantity()) + " кг.", subtitleStyle);
        subtitle.setAlignment(Align.right);
        add(subtitle)
                .growX()
                .colspan(2);
        row();
    }

    public Image getImage() {
        return image;
    }
}
