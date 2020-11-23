package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

import net.artux.pda.map.GdxAdapter;

import static net.artux.pda.map.GdxAdapter.RUSSIAN_CHARACTERS;
import static net.artux.pda.map.GdxAdapter.RUSSIAN_FONT_NAME;

public class Text extends Actor {

    BitmapFont font;
    String text;
    Vector2 position;

    public Text(String text, Vector2 position){
        font = GdxAdapter.generateFont(RUSSIAN_FONT_NAME, RUSSIAN_CHARACTERS);
        this.position = position;
        this.text = text;
        System.out.println(text);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (text!=null)
            font.draw(batch, text, position.x, position.y);
    }
}