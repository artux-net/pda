package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public class LevelBackground implements Disposable {

    private final Texture texture;
    private final Camera camera;

    int w,h;

    public LevelBackground(Texture texture, Camera camera) {
        this.texture = texture;
        this.camera = camera;

        w = (int) (camera.viewportWidth/texture.getWidth());
        h = (int) (camera.viewportHeight/texture.getHeight());
    }

    public void render(SpriteBatch spriteBatch)
    {
        int initX = (int) (camera.position.x - camera.viewportWidth/2);
        int initY = (int) (camera.position.y - camera.viewportHeight/2);

        initX = ((initX) / texture.getWidth())*texture.getWidth();
        initY = ((initY) / texture.getHeight())*texture.getHeight();

        for (int i = -1; i < w+1; i++) {
            int addX = i * texture.getWidth();
            for (int j = -1; j < h + 1; j++) {
                int addY = j * texture.getHeight();
                spriteBatch.draw(texture, initX + addX ,initY + addY);
            }
        }
    }

    @Override
    public void dispose() {
        texture.dispose();
    }
}
