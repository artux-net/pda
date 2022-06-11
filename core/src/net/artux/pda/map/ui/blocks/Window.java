package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.artux.pda.map.ui.UserInterface;

public class Window extends Table {

    private Label label;

    public Window(UserInterface userInterface, AssetManager assetManager){
        super();
        label = new Label("Title", userInterface.getLabelStyle());
        add(label);
        //Skin skin = assetManager.get("skins/foam/")
        //add()
    }

}
