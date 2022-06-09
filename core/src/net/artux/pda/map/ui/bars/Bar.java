package net.artux.pda.map.ui.bars;

import static net.artux.pdalib.Checker.isInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.ui.Fonts;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.TextureActor;
import net.artux.pda.map.ui.UserInterface;

public class Bar extends ProgressBar{

    public Bar(Color color) {
        super(0f, 100f, 0.1f, false, new ProgressBarStyle());
        getStyle().background = Utils.getColoredDrawable(1, Gdx.graphics.getHeight()/200, Color.GRAY);
        getStyle().knob = Utils.getColoredDrawable(0, Gdx.graphics.getHeight()/200, color);
        getStyle().knobBefore = Utils.getColoredDrawable(1, Gdx.graphics.getHeight()/200, color);
    }

    public void updateValue(float value){
        setValue(value);
    }
}
