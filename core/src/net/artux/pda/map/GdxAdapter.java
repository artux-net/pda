package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PreloadState;
import net.artux.pda.map.utils.PlatformInterface;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

public class GdxAdapter extends ApplicationAdapter {

    private final GameStateManager gsm;
    private final DataRepository dataRepository;
    private long startHeap;

    public GdxAdapter(PlatformInterface platformInterface) {
        gsm = new GameStateManager(platformInterface);
        dataRepository = new DataRepository(platformInterface);
    }

    public DataRepository getDataRepository() {
        return dataRepository;
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("GDX", "GDX load stared, version " + Gdx.app.getVersion());
        long loadMills = TimeUtils.millis();
        startHeap = Gdx.app.getNativeHeap();
        Gdx.app.debug("GDX", "Before load, heap " + startHeap);
        PreloadState preloadState = new PreloadState(gsm, dataRepository);
        gsm.push(preloadState);
        preloadState.startLoad();
        Gdx.app.debug("GDX", "Loaded, heap " + Gdx.app.getNativeHeap());

        Gdx.app.log("GDX", "GDX loading took " + (TimeUtils.millis() - loadMills) + " ms.");
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        gsm.resize(width, height);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0.5f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gsm.update(Gdx.graphics.getDeltaTime());
        gsm.render();
    }

    private boolean disposed;

    @Override
    public void dispose() {
        Gdx.app.debug("GDX", "Disposing, heap " + Gdx.app.getNativeHeap());
        super.dispose();
        if (!disposed) {
            gsm.dispose();
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
            gdxAdapter.dataRepository.setUserModel(userModel);
            return this;
        }

        public Builder storyData(StoryDataModel dataModel) {
            gdxAdapter.dataRepository.setStoryDataModel(dataModel);
            return this;
        }

        public Builder story(StoryModel dataModel) {
            gdxAdapter.dataRepository.setStoryModel(dataModel);
            return this;
        }

        public Builder map(GameMap map) {
            gdxAdapter.dataRepository.setGameMap(map);
            return this;
        }

        public ApplicationAdapter build() {
            return gdxAdapter;
        }
    }
}
