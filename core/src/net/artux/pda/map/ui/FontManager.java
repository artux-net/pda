package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;

public class FontManager implements Disposable {

    @Override
    public void dispose() {
        for (BitmapFont f : fontHashMap.values()) {
            f.dispose();
        }
    }

    public static final String folder = "fonts/";

    public static final String IMPERIAL_FONT = folder + "Imperial.ttf";
    public static final String LIBERAL_FONT = folder + "LiberationSans.ttf";
    private static final String SYMBOLS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";

    private final HashMap<String, BitmapFont> fontHashMap = new HashMap<>();

    public BitmapFont getFont(int size) {
        return getFont(IMPERIAL_FONT, size);
    }

    public BitmapFont getFont(String font, int size) {
        String key = font + size;
        if (!fontHashMap.containsKey(key)) {
            BitmapFont bitmapFont = getDisposableFont(font, size);
            fontHashMap.put(key, bitmapFont);
        }
        return fontHashMap.get(key);
    }

    public BitmapFont getDisposableFont(String font, int size){
        return getFont(font, SYMBOLS, size);
    }

    private BitmapFont getFont(String path, String characters, int size) {
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = characters;
        parameter.size = size;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal(path));
        BitmapFont font = generator.generateFont(parameter);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        generator.dispose();
        return font;
    }
}
