package net.artux.pda.map.view.units;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.engine.resource.types.NetFile;

public class LazyImage extends Image {

    private final AssetManager assetManager;
    private String filename;
    private boolean netFile = false;

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
        } else {
            if (filename.contains("http")) {
                netFile = true;
                assetManager.load(filename, NetFile.class);
            }else {
                FileHandle fileHandle = assetManager.getFileHandleResolver().resolve(filename);
                if (fileHandle.exists()) {
                    assetManager.load(filename, Texture.class);
                    this.filename = filename;
                }
            }
        }
        setDrawable(null);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (getDrawable() == null && assetManager.isLoaded(filename)) {
            if (!netFile)
                setDrawable(new TextureRegionDrawable(assetManager.get(filename, Texture.class)));
            else
                setDrawable(new TextureRegionDrawable((Texture) assetManager.get(filename, NetFile.class).file));
        }
    }
}
