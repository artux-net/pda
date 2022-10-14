package net.artux.pda.map.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.ui.bars.Utils;

public class BackpackMenu extends Table implements Disposable {

    private Skin skin;

    private Label label;
    private Table content;

    public BackpackMenu(final UserInterface userInterface, Skin skin) {
        super();
        this.skin = skin;
        setSkin(skin);

        Table hudTable = userInterface.getAssistantBlock();
        hudTable.row();

        top();
        left();

        label = new Label("Рюкзак", userInterface.getLabelStyle());
        top();
        add(label);
        row();
        content = new Table();
        content.defaults().align(Align.left);
        content.left();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX();

        setBackground(Utils.getColoredDrawable(1, 1, Color.BLACK));
    }


    @Override
    public void dispose() {

    }
}
