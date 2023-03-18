package net.artux.pda.map.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectMap;

import net.artux.engine.resource.loaders.LocaleBundleLoader;
import net.artux.engine.resource.loaders.NetTextureAssetLoader;
import net.artux.engine.resource.types.NetFile;
import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.view.FontManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

public class AssetsFinder implements Disposable {

    public static final String cachePath = "cache/";

    public AssetManager assetManager;
    private final Map<String, Texture> textureMap;
    private final FontManager fontManager;
    private final Properties properties;

    @Inject
    public AssetsFinder(Properties properties) {
        fontManager = new FontManager();
        textureMap = new HashMap<>();
        this.properties = properties;
    }

    public AssetManager getManager() {
        ObjectMap<String, Object> fontsMap = new ObjectMap<>();
        fontsMap.put("font", fontManager.getDisposableFont(FontManager.LIBERAL_FONT, 24));
        fontsMap.put("title", fontManager.getDisposableFont(FontManager.IMPERIAL_FONT, 28));
        Gdx.app.log("Assets", "Setup assets.");
        if (assetManager == null) {
            assetManager = new AssetManager();

            for (int i = 0; i < 31; i++) {
                assetManager.load("avatars/a" + i + ".png", Texture.class);
            }

            FileHandle ui = assetManager.getFileHandleResolver().resolve("ui");
            loadRecursively(assetManager, ui, true,  Texture.class);

            FileHandle shaders = assetManager.getFileHandleResolver().resolve("shaders");
            loadRecursively(assetManager, shaders, true, ShaderProgram.class);

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

            FileHandle sounds = assetManager.getFileHandleResolver().resolve("audio/sounds");
            loadRecursively(assetManager, sounds, true, Sound.class);

            FileHandle textures = assetManager.getFileHandleResolver().resolve("textures");
            loadRecursively(assetManager, textures, true, Texture.class);

            sounds = assetManager.getFileHandleResolver().resolve("audio/music");
            loadRecursively(assetManager, sounds, true, Music.class);

            assetManager.setLoader(NetFile.class, new NetTextureAssetLoader(properties));
            assetManager.setLoader(LocaleBundle.class, new LocaleBundleLoader(assetManager.getFileHandleResolver()));
            assetManager.load("locale/ui.properties", LocaleBundle.class);
        }
        return assetManager;
    }

    public LocaleBundle getLocaleBundle() {
        return assetManager.get("locale/ui.properties");
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
