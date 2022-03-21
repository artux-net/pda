package net.artux.pda.map.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class AssetsFinder implements Disposable {

    private AssetManager assetManager;

    public AssetManager get() {
        assetManager = new AssetManager();

        assetManager.load("avatars/a0.jpg", Texture.class);
        for (int i = 1; i < 31; i++) {
            assetManager.load("avatars/a"+i+".png", Texture.class);
        }

        assetManager.load("dialog.png", Texture.class);
        assetManager.load("test.png", Texture.class);
        assetManager.load("beg2.png", Texture.class);
        assetManager.load("beg1.png", Texture.class);
        assetManager.load("pause.png", Texture.class);
        assetManager.load("quest.png", Texture.class);
        assetManager.load("seller.png", Texture.class);
        assetManager.load("quest1.png", Texture.class);
        assetManager.load("cache.png", Texture.class);
        assetManager.load("direction.png", Texture.class);
        assetManager.load("transfer.png", Texture.class);
        assetManager.load("gg.png", Texture.class);
        assetManager.load("red.png", Texture.class);
        assetManager.load("green.png", Texture.class);
        assetManager.load("yellow.png", Texture.class);
        assetManager.load("gray.png", Texture.class);
        assetManager.load("direction.png", Texture.class);
        assetManager.load("touchpad/knob.png", Texture.class);
        assetManager.load("touchpad/back.png", Texture.class);
        assetManager.load("occupations.png", Texture.class);
        assetManager.load("controlPoint.png", Texture.class);

        assetManager.load("contact_0.ogg", Music.class);
        assetManager.load("contact_1.ogg", Music.class);
        assetManager.load("ak74_shoot_0.ogg", Music.class);
        assetManager.load("ak74_shoot_1.ogg", Music.class);

        assetManager.finishLoading();

        return assetManager;
    }


    @Override
    public void dispose() {
        assetManager.dispose();
        assetManager = null;
    }
}
