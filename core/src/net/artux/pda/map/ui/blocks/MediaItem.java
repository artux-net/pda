package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

public class MediaItem extends Table {

    private final Label titleLabel;
    private final Label subtitleLabel;

    public MediaItem(Image image, String title, String subtitle, Label.LabelStyle titleStyle, Label.LabelStyle subtitleStyle) {
        super();

        image.setScaling(Scaling.fit);
        add(image)
                .fill();

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

    public void setTitle(String title){
        titleLabel.setText(title);
    }

    public void setSubtitle(String subtitle){
        subtitleLabel.setText(subtitle);
    }

}
