package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.di.AppModule;
import net.artux.pda.map.di.CoreComponent;
import net.artux.pda.map.di.DaggerCoreComponent;
import net.artux.pda.map.states.GameStateController;
import net.artux.pda.map.states.State;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.util.Properties;

public class GdxAdapter extends ApplicationAdapter {

    private final GameStateController gsc;
    private final CoreComponent coreComponent;
    private long startHeap;

    public GdxAdapter(PlatformInterface platformInterface) {
        coreComponent = DaggerCoreComponent.builder().appModule(new AppModule(platformInterface)).build();
        gsc = coreComponent.getGSC();
    }

    public DataRepository getDataRepository() {
        return coreComponent.getDataRepository();
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("GDX", "GDX load stared, version " + Gdx.app.getVersion());
        long loadMills = TimeUtils.millis();
        startHeap = Gdx.app.getNativeHeap();
        Gdx.app.debug("GDX", "Before load, heap " + startHeap);
        State firstState = coreComponent.getPreloadState();
        gsc.push(firstState);
        Gdx.app.debug("GDX", "Loaded, heap " + Gdx.app.getNativeHeap());
        Gdx.app.log("GDX", "GDX loading took " + (TimeUtils.millis() - loadMills) + " ms.");
        resume();
    }

    @Override
    public void resume() {
        super.resume();
        gsc.resume();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gsc.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsc.update(Gdx.graphics.getDeltaTime());
        gsc.render();
    }

    private boolean disposed;

    @Override
    public void dispose() {
        Gdx.app.debug("GDX", "Disposing, heap " + Gdx.app.getNativeHeap());
        super.dispose();
        if (!disposed) {
            coreComponent.getAssetsFinder().dispose();
            gsc.dispose();
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

        GdxAdapter gdxAdapter;

        public Builder(PlatformInterface platformInterface) {
            gdxAdapter = new GdxAdapter(platformInterface);
        }

        public Builder user(UserModel userModel) {
            gdxAdapter.coreComponent.getDataRepository().setUserModel(userModel);
            return this;
        }

        public Builder storyData(StoryDataModel dataModel) {
            gdxAdapter.coreComponent.getDataRepository().setStoryDataModel(dataModel);
            return this;
        }

        public Builder story(StoryModel dataModel) {
            gdxAdapter.coreComponent.getDataRepository().setStoryModel(dataModel);
            return this;
        }

        public Builder map(GameMap map) {
            gdxAdapter.coreComponent.getDataRepository().setGameMap(map);
            return this;
        }

        public Builder props(Properties properties) {
            gdxAdapter.coreComponent.getDataRepository().setProperties(properties);
            return this;
        }

        public ApplicationAdapter build() {
            return gdxAdapter;
        }
    }
}
