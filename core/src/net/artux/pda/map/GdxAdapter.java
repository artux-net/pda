package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PlayState;
import net.artux.pda.map.states.PreloadState;

public class GdxAdapter extends ApplicationAdapter {
	SpriteBatch batch;

	GameStateManager gsm;

	public final String RUSSIAN_FONT_NAME = "fonts/Imperial Web.ttf";
	public final String RUSSIAN_CHARACTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";



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
		gsm.setRussianFont(generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS));
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
		Gdx.app.debug("GDX","Disposing started, heap " + Gdx.app.getNativeHeap());
    	super.dispose();
    	if (!disposed) {
			gsm.dispose();
			batch.dispose();
			System.gc();
			disposed = true;
		}
		Gdx.app.debug("GDX","Disposed, heap " + Gdx.app.getNativeHeap());
	}


	public static BitmapFont generateFont(String fontName, String characters) {
		return generateFont(fontName, characters, 28);
	}

	private static BitmapFont generateFont(String fontName, String characters, int size) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = characters;
		parameter.size = size;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontName) );
		BitmapFont font = generator.generateFont(parameter);
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		generator.dispose();
		return font;
	}
}
