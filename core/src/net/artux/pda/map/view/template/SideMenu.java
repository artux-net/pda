package net.artux.pda.map.view.template;

import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.view.bars.Utils;

public abstract class SideMenu extends Table {

    private final VerticalGroup contentGroup;

    public SideMenu(String title, Skin skin) {
        super(skin);

        setFillParent(true);
        top();

        defaults().pad(10);
        float h = add(new Label(title, skin.get("title", Label.LabelStyle.class))).growX().getActorHeight();
        /*ImageButton closeButton = new ImageButton(skin.get("close", ImageButton.ImageButtonStyle.class));
        closeButton.getImage().setScaling(Scaling.fit);
        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                getParent().removeActor(SideMenu.this);
                //TODO костыль
            }
        });
        add(closeButton).fillY().height(h);*/

        contentGroup = new VerticalGroup();
        contentGroup
                .expand()
                .fill();
        row();
        ScrollPane scrollPane = new ScrollPane(contentGroup, skin);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setScrollbarsVisible(true);

        add(scrollPane)
                .top()
                .fill()
                .expand()
                .colspan(2);

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public VerticalGroup getContent() {
        return contentGroup;
    }

    public abstract void update();

    @Override
    public float getWidth() {
        return getParent().getWidth() / 3;
    }

    @Override
    public float getPrefHeight() {
        return getParent().getHeight();
    }
}
