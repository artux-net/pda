package net.artux.pda.map.view.sidebar;

import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Window;

import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.Utils;

public abstract class Sidebar extends Window {

    private final VerticalGroup contentGroup;

    public Sidebar(String title, Skin skin) {
        super("", skin);

        setFillParent(true);
        top();

        defaults().pad(10);
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
                .expand();

        setBackground(Utils.getColoredDrawable(1, 1, Colors.backgroundColor));
    }

    public VerticalGroup getContent() {
        return contentGroup;
    }

    @Override
    public float getWidth() {
        return getParent().getWidth() / 3;
    }

    @Override
    public float getPrefHeight() {
        return getParent().getHeight();
    }

    public abstract void update();
}
