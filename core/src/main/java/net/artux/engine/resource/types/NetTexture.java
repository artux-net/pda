package net.artux.engine.resource.types;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

public class NetTexture implements Disposable {

    public Texture file;

    public NetTexture(Texture file) {
        this.file = file;
    }

    @Override
    public void dispose() {
        if (file != null)
            file.dispose();
    }
}
