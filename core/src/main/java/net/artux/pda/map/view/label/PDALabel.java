package net.artux.pda.map.view.label;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import javax.inject.Inject;

public class PDALabel extends Label {

    @Inject
    public PDALabel(Skin skin) {
        super("", skin);
    }
}
