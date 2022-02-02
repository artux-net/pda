package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PreloadState;

public class GdxAdapter extends ApplicationAdapter {

	private SpriteBatch batch;
	private final GameStateManager gsm;

    public GdxAdapter(PlatformInterface platformInterface){
		gsm = new GameStateManager(platformInterface);
	}

	public void put(String key, Object o){
    	gsm.put(key, o);
	}

	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		Gdx.app.debug("GDX","Before load, heap " + Gdx.app.getNativeHeap());
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		PreloadState preloadState = new PreloadState(gsm);
		gsm.push(preloadState);
		preloadState.startLoad(batch);
		Gdx.app.debug("GDX", "Loaded, heap " + Gdx.app.getNativeHeap());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		gsm.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	private boolean disposed;

	@Override
	public void dispose () {
		Gdx.app.debug("GDX","Disposing GDX, heap " + Gdx.app.getNativeHeap());
    	super.dispose();
    	if (!disposed) {
			gsm.dispose();
			batch.dispose();
			System.gc();
			disposed = true;
		}
		Gdx.app.debug("GDX","GDX disposed, heap " + Gdx.app.getNativeHeap());
	}

}
