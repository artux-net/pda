package net.artux.pda.map.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Text extends Actor {

    BitmapFont font;
    String text;

    public Text(String text, BitmapFont font){
        this.font = font;
        this.text = text;
        GlyphLayout glyphLayout = new GlyphLayout(font, text);

        setSize(glyphLayout.width, font.getCapHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        if (text!=null)
            font.draw(batch, text, getX(), getY());
    }

    @Override
    public String toString() {
        return text;
    }

}