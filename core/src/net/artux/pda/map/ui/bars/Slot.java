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
        this.secondLabel = new Label("0", userInterface.getLabelStyle());
        add(this.secondLabel);
    }

    public Slot(String text, UserInterface userInterface, TextButtonStyle style) {
        super(text, style);
        pad(30f);
        left();
        bottom();
        row();
    }

    public Label getSecondLabel() {
        return secondLabel;
    }

    public void setLabelText(String text) {
        secondLabel.setText(text);
    }
}
