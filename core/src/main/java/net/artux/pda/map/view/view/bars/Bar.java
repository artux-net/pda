package net.artux.pda.map.view.view.bars;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;

import net.artux.pda.map.view.Utils;

public class Bar extends ProgressBar {

    public Bar(Color color) {
        super(0f, 100f, 0.1f, false, new ProgressBarStyle());
        getStyle().background = Utils.getColoredDrawable(1, Gdx.graphics.getHeight() / 200, Color.GRAY);
        getStyle().knob = Utils.getColoredDrawable(0, Gdx.graphics.getHeight() / 200, color);
        getStyle().knobBefore = Utils.getColoredDrawable(1, Gdx.graphics.getHeight() / 200, color);
    }

    @Override
    public void setColor(Color color) {
        getStyle().knob = Utils.getColoredDrawable(0, Gdx.graphics.getHeight() / 200, color);
        getStyle().knobBefore = Utils.getColoredDrawable(1, Gdx.graphics.getHeight() / 200, color);
    }

    public void updateValue(float value) {
        setValue(value);
    }
}
