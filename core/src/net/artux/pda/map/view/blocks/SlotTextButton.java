package net.artux.pda.map.view.blocks;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import javax.inject.Inject;

public class SlotTextButton extends TextButton {

    @Inject
    public SlotTextButton(TextButton.TextButtonStyle style) {
        this("", style);
    }

    public SlotTextButton(String text, TextButton.TextButtonStyle style) {
        super(text, style);
        pad(20);
    }

}
