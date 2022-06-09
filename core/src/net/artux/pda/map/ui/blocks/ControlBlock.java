package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class ControlBlock extends Table {

    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();

    public ControlBlock(){
        super();
        bottom();
        right();
        setSize(w / 3f, h / 2f);
    }

}
