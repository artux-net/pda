package net.artux.pda.map.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.ui.FontManager;
import net.artux.pda.map.utils.NetFileResolver;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

public class AssetsFinder implements Disposable {

    public static final String cachePath = "cache/";

    public AssetManager assetManager;
    public AssetManager remoteAssetManager;
    private final Map<String, Texture> textureMap;
    private final FontManager fontManager;

    @Inject
    public AssetsFinder(Properties properties) {
        fontManager = new FontManager();
        textureMap = new HashMap<>();
        remoteAssetManager = new AssetManager(new NetFileResolver(properties));
    }

    public AssetManager getManager() {
        long loadTime = TimeUtils.millis();
        ObjectMap<String, Object> fontsMap = new ObjectMap<>();
        fontsMap.put("font", fontManager.getDisposableFont(FontManager.LIBERAL_FONT, 24));
        fontsMap.put("title", fontManager.getDisposableFont(FontManager.IMPERIAL_FONT, 28));
        //Gdx.app.log("Assets", "Loading assets.");
        if (assetManager == null) {
            assetManager = new AssetManager();

            assetManager.load("avatars/a0.jpg", Texture.class);
            for (int i = 1; i < 31; i++) {
                assetManager.load("avatars/a" + i + ".png", Texture.class);
            }

            FileHandle ui = assetManager.getFileHandleResolver().resolve("ui");
            loadRecursively(assetManager, ui, true, Texture.class);

            assetManager.load("data/uiskin.atlas", TextureAtlas.class);
            assetManager.load("quest.png", Texture.class);
            assetManager.load("bullet.png", Texture.class);
            assetManager.load("seller.png", Texture.class);
            assetManager.load("quest1.png", Texture.class);
            assetManager.load("cache.png", Texture.class);
            assetManager.load("transfer.png", Texture.class);
            assetManager.load("gg.png", Texture.class);
            assetManager.load("red.png", Texture.class);
            assetManager.load("green.png", Texture.class);
            assetManager.load("yellow.png", Texture.class);
            assetManager.load("gray.png", Texture.class);
            assetManager.load("controlPoint.png", Texture.class);

            SkinLoader.SkinParameter skinParameter = new SkinLoader.SkinParameter("skins/cloud/cloud-form-ui.atlas", fontsMap);
            assetManager.load("skins/cloud/cloud-form-ui.json", Skin.class, skinParameter);

            FileHandle sounds = assetManager.getFileHandleResolver().resolve("sounds");
            loadRecursively(assetManager, sounds, true, Music.class);
            assetManager.finishLoading();
            //Gdx.app.log("Assets", "Loading took " + (TimeUtils.millis() - loadTime) + " ms.");
        }
        return assetManager;
    }

    public FontManager getFontManager() {
        return fontManager;
    }

    public Texture getLocal(String path) {
        if (textureMap.containsKey(path))
            return textureMap.get(path);
        if (path == null || path.equals(""))
            return null;
        Texture texture = new Texture(Gdx.files.local(cachePath + path));
        textureMap.put(path, texture);
        return texture;
    }

    private void loadRecursively(AssetManager assetManager, FileHandle fileHandle, boolean recursively, Class<? extends Disposable> clazz) {
        if (recursively)
            for (FileHandle f :
                    fileHandle.list()) {
                if (f.isDirectory())
                    loadRecursively(assetManager, f, recursively, clazz);
                else
                    assetManager.load(f.path(), clazz);
            }
        else
            for (FileHandle f :
                    fileHandle.list()) {
                assetManager.load(f.path(), clazz);
            }
    }

    @Override
    public void dispose() {
        for (Map.Entry<String, Texture> e :
                textureMap.entrySet()) {
            if (e.getValue() != null)
                e.getValue().dispose();
        }

        fontManager.dispose();
        assetManager.dispose();
        assetManager = null;
    }
}
