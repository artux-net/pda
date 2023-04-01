package net.artux.pda.map.view;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class LevelBackgroundImage extends Actor {

    private final TextureRegion backgroundTextureRegion;

    private final int startX;
    private final int startY;

    public LevelBackgroundImage(Texture texture, Camera camera) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundTextureRegion = new TextureRegion(texture,
                (int) (/*MapInfo.mapWidth + */camera.viewportWidth),
                (int) (/*MapInfo.mapHeight +*/ camera.viewportHeight));

        startX = (int) (-camera.viewportWidth/2);
        startY = (int) (-camera.viewportHeight/2);

        //setWidth(MapInfo.mapWidth + camera.viewportWidth);
        //setHeight(MapInfo.mapHeight + camera.viewportHeight);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.draw(backgroundTextureRegion, startX, startY);
    }
}
