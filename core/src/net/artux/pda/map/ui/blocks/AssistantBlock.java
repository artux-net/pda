package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import net.artux.pda.map.ui.Logger;

public class AssistantBlock extends Table {

    int w = Gdx.graphics.getWidth();
    int h = Gdx.graphics.getHeight();
    private Table buttonsTable;

    public AssistantBlock(){
        super();
        top();
        right();
        setSize(w / 3, h / 2);

        buttonsTable = new Table();
        buttonsTable.defaults()
                .pad(10)
                .height(h / 10)
                .width(h / 10)
                .right()
                .top();
        add(buttonsTable).right();
        row();
    }

    public Table getButtonsTable() {
        return buttonsTable;
    }
}
