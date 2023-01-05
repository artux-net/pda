package net.artux.pda.map.ui.units;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LazyImage extends Image {

    private final AssetManager assetManager;
    private final String filename;

    public LazyImage(AssetManager assetManager, String filename) {
        super();
        this.assetManager = assetManager;
        this.filename = filename;

        assetManager.load(filename, Texture.class);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getDrawable() == null && assetManager.isLoaded(filename)) {
            setDrawable(new TextureRegionDrawable(assetManager.get(filename, Texture.class)));
        }
    }
}
