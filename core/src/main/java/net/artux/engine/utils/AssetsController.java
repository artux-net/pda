package net.artux.engine.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AssetsController implements Disposable {

    private final Map<Class, List<String>> resources = new HashMap<>();
    private final AssetManager assetManager;

    @Inject
    public AssetsController(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void initResources(AssetsUser assetsUser) {
        for (Map.Entry<String, Class> e : assetsUser.getAssets().entrySet()) {
            assetManager.load(e.getKey(), e.getValue());
        }
    }

    public void unload(AssetsUser assetsUser) {
        for (Map.Entry<String, Class> e : assetsUser.getAssets().entrySet()) {
            assetManager.unload(e.getKey());
        }
    }

    public <T> T get(String name) {
        return assetManager.get(name);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
    }
}
