package net.artux.pda.map.ui.bars;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.artux.pda.map.ui.UserInterface;

public class Slot extends TextButton {

    private Label secondLabel;

    public Slot(UserInterface userInterface, TextButtonStyle style) {
        super("", style);
        left();
        bottom();
        row();
        secondLabel = new Label("0", userInterface.getLabelStyle());
        add(secondLabel);
    }

    public Label getSecondLabel() {
        return secondLabel;
    }

    public void setLabelText(String text) {
        secondLabel.setText(text);
    }
}
