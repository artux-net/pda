package net.artux.pda.map.ui.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.ui.units.LazyImage;
import net.artux.pda.model.items.ItemModel;

import java.text.DecimalFormat;

public class ItemView extends Table {

    private final String iconFilename;
    private final Image image;

    private static final DecimalFormat formater = new DecimalFormat("##.##");

    public ItemView(ItemModel itemModel, Label.LabelStyle titleStyle, Label.LabelStyle subtitleStyle, AssetManager assetManager) {
        super();
        iconFilename = "icons/items/" + itemModel.getIcon();


        //todo add counter
        image = new LazyImage(assetManager, iconFilename);
        image.setScaling(Scaling.fit);
        add(image)
                .grow();
        row();

        Label title = new Label(itemModel.getTitle(), titleStyle);
        title.setAlignment(Align.left);
        add(title)
                .growX();
        row();

        Label subtitle = new Label(formater.format(itemModel.getWeight() * itemModel.getQuantity()) + " кг.", subtitleStyle);
        subtitle.setAlignment(Align.right);
        add(subtitle)
                .growX();
        row();
    }

}
