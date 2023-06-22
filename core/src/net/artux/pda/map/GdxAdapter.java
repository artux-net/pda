package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.engine.scenes.SceneManager;
import net.artux.engine.scenes.Scene;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.map.utils.di.components.CoreComponent;
import net.artux.pda.map.utils.di.components.DaggerCoreComponent;
import net.artux.pda.map.utils.di.modules.AppModule;
import net.artux.pda.model.items.ItemsContainerModel;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.Properties;

public class GdxAdapter extends ApplicationAdapter {

    private final SceneManager gsc;
    private final CoreComponent coreComponent;
    private AssetManager assetManager;
    private long startHeap;

    public GdxAdapter(DataRepository dataRepository) {
        coreComponent = DaggerCoreComponent.builder().appModule(new AppModule(dataRepository)).build();
        gsc = coreComponent.getGSC();
    }

    public DataRepository getDataRepository() {
        return coreComponent.getDataRepository();
    }

    @Override
    public void create() {
        startHeap = Gdx.app.getNativeHeap();
        assetManager = coreComponent.getAssetsManager();

        gsc.clear();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.getApplicationLogger().log("GDX", "GDX load stared, version " + Gdx.app.getVersion());
        long loadMills = TimeUtils.millis();

        Gdx.app.debug("GDX", "Before load, heap " + startHeap);
        Scene firstScene = coreComponent.getPreloadState();
        gsc.push(firstScene);
        Gdx.app.debug("GDX", "Loaded, heap " + Gdx.app.getNativeHeap());
        Gdx.app.getApplicationLogger().log("GDX", "GDX loading took " + (TimeUtils.millis() - loadMills) + " ms.");
        resume();
    }

    @Override
    public void resume() {
        super.resume();
        gsc.resume();
    }

    @Override
    public void pause() {
        super.pause();
        gsc.pause();
    }

    @Override
    public void resize(int width, int height) {
        //super.resize(width, height);
        //gsc.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.086f, 0.09f, 0.098f, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (!assetManager.isFinished())
            assetManager.update(17);
        gsc.update(Gdx.graphics.getDeltaTime());
        gsc.render(Gdx.graphics.getDeltaTime());
    }

    private boolean disposed;

    public boolean isDisposed() {
        return disposed;
    }

    @Override
    public void dispose() {
        Gdx.app.debug("GDX", "Disposing, heap " + Gdx.app.getNativeHeap());
        super.dispose();
        if (!disposed) {
            gsc.clear();
            coreComponent.getAssetsFinder().dispose();
            disposed = true;
            System.gc();
        }
        long disposeHeap = Gdx.app.getNativeHeap();
        Gdx.app.debug("GDX", "Disposed, heap " + Gdx.app.getNativeHeap());
        Gdx.app.debug("Leak test", "Difference between start heap and after dispose " + (disposeHeap - startHeap));
        if ((disposeHeap - startHeap) > 1000)
            Gdx.app.error("LEAK", "WARNING! There must be leak!");
    }

    public static class Builder {

        private final DataRepository.Builder builder;

        public Builder(PlatformInterface platformInterface) {
            builder = new DataRepository.Builder();
            builder.platformInterface(platformInterface);
        }

        public Builder storyData(StoryDataModel dataModel) {
            builder.storyDataModel(dataModel);
            return this;
        }

        public Builder story(StoryModel dataModel) {
            builder.storyModel(dataModel);
            return this;
        }

        public Builder map(GameMap map) {
            builder.gameMap(map);
            return this;
        }

        public Builder props(Properties properties) {
            builder.properties(properties);
            return this;
        }

        public Builder items(ItemsContainerModel items) {
            builder.items(items);
            return this;
        }

        public ApplicationAdapter build() {
            return new GdxAdapter(builder.build());
        }
    }
}
