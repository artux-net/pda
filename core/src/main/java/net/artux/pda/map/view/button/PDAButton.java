package net.artux.pda.map.view.button;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import javax.inject.Inject;

public class PDAButton extends TextButton {

    @Inject
    public PDAButton(TextButton.TextButtonStyle style) {
        this("", style);
    }

    public PDAButton(String text, TextButton.TextButtonStyle style) {
        super(text, style);
        pad(20);
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        super.setDisabled(isDisabled);
        if (isDisabled)
            getStyle().fontColor = Color.GRAY;
        else
            getStyle().fontColor = Color.WHITE;
    }
}
