package net.artux.pda.map;

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

	public static final String SPANISH_FONT_NAME = "fonts/goodfish rg.ttf";
	public static final String RUSSIAN_FONT_NAME = "fonts/Imperial Web.ttf";
	public static final String RUSSIAN_CHARACTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

	private Connection connection;
    public GdxAdapter(PlatformInterface platformInterface, Member member){
		gsm = new GameStateManager(platformInterface, member);
	}

	public GdxAdapter(PlatformInterface platformInterface, Member member, Connection connection){
		gsm = new GameStateManager(platformInterface, member);
		this.connection = connection;
	}


	public void put(String key, Object o){
    	gsm.put(key, o);
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		if (connection != null) {
			try {
				gsm.push(new ArenaState(gsm, batch, connection));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}else gsm.push(new PlayState(gsm, batch));
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
    	super.dispose();
		gsm.dispose();
		batch.dispose();
		System.gc();
	}

	public static BitmapFont generateFont(String fontName, String characters) {
		return generateFont(fontName, characters, 24);
	}

	public static BitmapFont generateFont(String fontName, String characters, int size) {
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.characters = characters;
		parameter.size = size;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(fontName) );
		BitmapFont font = generator.generateFont(parameter);

		generator.dispose();
		return font;
	}
}
