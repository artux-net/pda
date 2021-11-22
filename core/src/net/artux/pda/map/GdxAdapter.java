package net.artux.pda.map;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.ArenaState;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.states.PlayState;
import net.artux.pdalib.Member;
import net.artux.pdalib.arena.Connection;

import java.net.URISyntaxException;

public class GdxAdapter extends ApplicationAdapter {
	SpriteBatch batch;

	GameStateManager gsm;

	public final String SPANISH_FONT_NAME = "fonts/goodfish rg.ttf";
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
		gsm.push(new PlayState(gsm, batch));
		Gdx.app.debug("GDX", "Loaded, heap " + Gdx.app.getNativeHeap());
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		gsm.resize(width, height);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(130, 169, 130, 0.5f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render(batch);
	}

	@Override
	public void dispose () {
    	super.dispose();
		gsm.dispose();
		batch.dispose();
		Gdx.app.debug("GDX","Disposed, heap " + Gdx.app.getNativeHeap());
		System.gc();
	}


	public static BitmapFont generateFont(String fontName, String characters) {
		return generateFont(fontName, characters, 24);
	}

	private static BitmapFont generateFont(String fontName, String characters, int size) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = characters;
		parameter.size = size;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontName) );
		BitmapFont font = generator.generateFont(parameter);

		generator.dispose();
		return font;
	}
}
