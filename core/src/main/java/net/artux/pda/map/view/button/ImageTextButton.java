package net.artux.pda.map.view.button;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class ImageTextButton extends Table {

    public ImageTextButton(Image image, String text, Label.LabelStyle labelStyle) {
        super();
        left();
        add(image)
                .fill()
                .size(50, 50)
                .padRight(10);

        Label label = new Label(text, labelStyle);
        label.setAlignment(Align.left);
        label.setWrap(true);
        add(label)
                .grow();
    }

}
