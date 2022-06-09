package net.artux.pda.map.ui.bars;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.map.ui.UserInterface;

public class Slot extends ImageButton {

    private Label label;

    public Slot(UserInterface userInterface, ImageButtonStyle style) {
        super(style);
        TextureRegionDrawable textureRegionDrawable = (TextureRegionDrawable) style.imageUp;
        float pad = textureRegionDrawable.getRegion().getRegionHeight() * 0.15f;
        pad(pad);
        left();
        bottom();
        row();
        label = new Label("0", userInterface.getLabelStyle());
        add(label);
    }

    public void setText(String text){
        label.setText(text);
    }
}
