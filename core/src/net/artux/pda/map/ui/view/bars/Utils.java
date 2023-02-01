package net.artux.pda.map.ui.view.bars;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Utils {

    public static Drawable getColoredDrawable(int width, int height, Color color) {
        return new TextureRegionDrawable(getColoredRegion(width, height, color));
    }

    public static TextureRegion getColoredRegion(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();

        TextureRegion region = new TextureRegion(new Texture(pixmap));

        pixmap.dispose();

        return region;
    }

}
