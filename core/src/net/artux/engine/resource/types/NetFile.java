package net.artux.engine.resource.types;

import com.badlogic.gdx.utils.Disposable;

public class NetFile<T> implements Disposable {

    public T file;

    public NetFile(T file) {
        this.file = file;
    }

    @Override
    public void dispose() {
        if (file instanceof Disposable)
            ((Disposable) file).dispose();
    }
}
