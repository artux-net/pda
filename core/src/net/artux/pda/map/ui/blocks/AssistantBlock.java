package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.pda.map.ui.Logger;

public class AssistantBlock extends Table {

    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();
    private Logger logger;

    public AssistantBlock(){
        super();
        //logger = new Logger(engine);
        top().right();
        setSize(w / 3, h / 2);
    }

}
