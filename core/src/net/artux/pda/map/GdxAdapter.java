package net.artux.pda.map;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import net.artux.pda.map.model.Map;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PlayState;

public class GdxAdapter extends ApplicationAdapter {
	SpriteBatch batch;

	GameStateManager gsm;

	public GdxAdapter(PlatformInterface platformInterface, Map map){
		gsm = new GameStateManager(platformInterface, map);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		gsm.push(new PlayState(gsm, batch));
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		gsm.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
