package net.artux.pda.map.view.collection.list.item;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.view.image.LazyImage;

public class MediaItem extends Table {

    private final LazyImage image;
    private final Label titleLabel;
    private final Label subtitleLabel;

    public MediaItem(String imageFilename, String title, String subtitle,
                     Label.LabelStyle titleStyle, Label.LabelStyle subtitleStyle, AssetManager assetManager) {
        super();

        image = new LazyImage(assetManager);
        image.setFilename(imageFilename);
        image.setAlign(Align.left);
        image.setScaling(Scaling.fit);
        add(image)
                .left()
                .fill()
                .maxWidth(150)
                .maxHeight(100)
                .uniformY();

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.left();
        titleLabel = new Label(title, titleStyle);
        titleLabel.setAlignment(Align.left);
        verticalGroup.addActor(titleLabel);
        subtitleLabel = new Label(subtitle, subtitleStyle);
        subtitleLabel.setAlignment(Align.left);
        verticalGroup.addActor(subtitleLabel);
        add(verticalGroup)
                .left()
                .fill()
                .uniformY()
                .growX();
    }

    public void setImage(String imageFilename) {
        image.setFilename(imageFilename);
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

}
