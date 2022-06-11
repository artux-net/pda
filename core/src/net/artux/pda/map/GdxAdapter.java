package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PreloadState;

public class GdxAdapter extends ApplicationAdapter {

    private SpriteBatch batch;
    private final GameStateManager gsm;
    private long startHeap;

    public GdxAdapter(PlatformInterface platformInterface) {
        gsm = new GameStateManager(platformInterface);
    }

    public void put(String key, Object o) {
        gsm.put(key, o);
    }

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.log("GDX", "GDX load stared, version " + Gdx.app.getVersion());
        long loadMills = TimeUtils.millis();
        startHeap = Gdx.app.getNativeHeap();
        Gdx.app.debug("GDX", "Before load, heap " + startHeap);
        batch = new SpriteBatch();
        PreloadState preloadState = new PreloadState(gsm);
        gsm.push(preloadState);
        preloadState.startLoad(batch);
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
        gsm.render(batch);
    }

    private boolean disposed;

    @Override
    public void dispose() {
        Gdx.app.debug("GDX", "Disposing, heap " + Gdx.app.getNativeHeap());
        super.dispose();
        if (!disposed) {
            gsm.dispose();
            batch.dispose();
            disposed = true;
            System.gc();
        }
        long disposeHeap = Gdx.app.getNativeHeap();
        Gdx.app.debug("GDX", "Disposed, heap " + Gdx.app.getNativeHeap());
        Gdx.app.debug("Leak test", "Difference between start heap and after dispose " + (disposeHeap - startHeap));
        if ((disposeHeap - startHeap) > 1000)
            Gdx.app.error("LEAK", "WARNING! There must be leak!");
    }

}
