package net.artux.pda.map.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Fonts {

    public enum Language{
        RUSSIAN
    }

    public static final String IMPERIAL_FONT = "fonts/Imperial.ttf";
    public static final String ARIAL_FONT = "fonts/Arial.ttf";
    private static final String CHARACTERS = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяabcdefghijklmnopqrstuvwxyzАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789][_!$%#@|\\/?-+=()*&.;:,{}\"´`'<>";


    public static BitmapFont generateFont(Language language, int size) {
        return generateFont(language, IMPERIAL_FONT, size);
    }

    public static BitmapFont generateFont(Language language, String path, int size) {
        if (language == Language.RUSSIAN)
            return generateFont(path, CHARACTERS, size);
        else throw new RuntimeException("Not supported language.");
    }

    private static BitmapFont generateFont(String path, String characters, int size) {
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
