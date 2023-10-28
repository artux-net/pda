package net.artux.pda.map.view.view.bars;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import javax.inject.Inject;

public class Slot extends TextButton {

    private final Label secondLabel;

    @Inject
    public Slot(TextButtonStyle style, Label.LabelStyle labelStyle) {
        super("", style);

        getLabel().setAlignment(Align.left);
        getLabelCell().grow();

        secondLabel = new Label("0", labelStyle);
        secondLabel.setAlignment(Align.right);
        row();
        add(secondLabel)
                .right()
                .growX();
    }

    public Label getSecondLabel() {
        return secondLabel;
    }

    public void setLabelText(String text) {
        secondLabel.setText(text);
    }
}
