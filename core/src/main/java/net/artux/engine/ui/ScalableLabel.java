package net.artux.engine.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ScalableLabel extends Label {
    public ScalableLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public ScalableLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public ScalableLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public ScalableLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public ScalableLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    private int lastH = 0;

    @Override
    public void act(float delta) {
        super.act(delta);
        int h = (int) getStage().getViewport().getCamera().viewportHeight;
        if (h != lastH) {
            lastH = h;
            setFontScale(h / 1080f);
        }
    }
}
