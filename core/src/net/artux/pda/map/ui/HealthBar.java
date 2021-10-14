package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.model.Player;

public class HealthBar extends Actor implements Disposable {

    private final TextureAtlas skinAtlas;
    private final NinePatchDrawable loadingBarBackground;
    private final NinePatchDrawable loadingBar;
    private final Player player;

    public HealthBar(Player player) {
        this.player = player;
        skinAtlas = new TextureAtlas(Gdx.files.internal("data/uiskin.atlas"));
        NinePatch loadingBarBackgroundPatch = new NinePatch(skinAtlas.findRegion("default-round"), 5, 5, 4, 4);
        NinePatch loadingBarPatch = new NinePatch(skinAtlas.findRegion("default-round-down"), 5, 5, 4, 4);
        loadingBar = new NinePatchDrawable(loadingBarPatch);
        loadingBarBackground = new NinePatchDrawable(loadingBarBackgroundPatch);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (player!=null) {
            float progress = (float) player.health / 100;
            if (progress < 0 && progress > 1) progress = 0;

            loadingBarBackground.draw(batch, getX(), getY(), getWidth() * getScaleX(), getHeight() * getScaleY());
            loadingBar.draw(batch, getX(), getY(), progress * getWidth() * getScaleX(), getHeight() * getScaleY());
        }

    }

    @Override
    public void dispose() {
        skinAtlas.dispose();
    }
}
