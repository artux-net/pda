package net.artux.pda.map.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pda.map.engine.data.GlobalData;

public class LevelBackgroundImage extends Actor {

    private final TextureRegion backgroundTextureRegion;

    private final int startX;
    private final int startY;

    public LevelBackgroundImage(Texture texture, Camera camera) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundTextureRegion = new TextureRegion(texture,
                (int) (GlobalData.mapWidth + camera.viewportWidth),
                (int) (GlobalData.mapHeight + camera.viewportHeight));

        startX = (int) (-camera.viewportWidth/2);
        startY = (int) (-camera.viewportHeight/2);

        setWidth(GlobalData.mapWidth + camera.viewportWidth);
        setHeight(GlobalData.mapHeight + camera.viewportHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(backgroundTextureRegion, startX, startY);
    }
}
