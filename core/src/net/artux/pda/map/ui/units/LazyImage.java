package net.artux.pda.map.ui.units;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class LazyImage extends Image {

    private final AssetManager assetManager;
    private String filename;

    public LazyImage(AssetManager assetManager) {
        this(assetManager, null);
    }

    public LazyImage(AssetManager assetManager, String filename) {
        super();
        this.assetManager = assetManager;
        setFilename(filename);
    }

    public void setFilename(String filename) {
        if (filename == null || filename.isEmpty())
            return;

        if (assetManager.contains(filename)) {
            this.filename = filename;
        }else {
            FileHandle fileHandle = assetManager.getFileHandleResolver().resolve(filename);
            if (fileHandle.exists()) {
                assetManager.load(filename, Texture.class);
                this.filename = filename;
            }
        }
        setDrawable(null);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getDrawable() == null && assetManager.isLoaded(filename)) {
            setDrawable(new TextureRegionDrawable(assetManager.get(filename, Texture.class)));
        }
    }
}
