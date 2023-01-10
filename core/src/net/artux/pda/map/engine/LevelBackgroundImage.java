package net.artux.pda.map.engine;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.artux.pda.map.engine.data.GlobalData;

public class LevelBackgroundImage {

    private final TextureRegion backgroundTextureRegion;

    private final int totalWidth;
    private final int totalHeight;

    private final int startX;
    private final int startY;

    public LevelBackgroundImage(Texture texture, Camera camera) {
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        backgroundTextureRegion = new TextureRegion(texture,
                (int) (GlobalData.mapWidth + camera.viewportWidth),
                (int) (GlobalData.mapHeight + camera.viewportHeight));

        startX = (int) (-camera.viewportWidth/2);
        startY = (int) (-camera.viewportHeight/2);

        totalWidth = (int) (GlobalData.mapWidth + camera.viewportWidth);
        totalHeight = (int) (GlobalData.mapHeight + camera.viewportHeight);
    }

    public void render(SpriteBatch spriteBatch) {
        spriteBatch.draw(backgroundTextureRegion, startX, startY, totalWidth, totalHeight);
    }
}
