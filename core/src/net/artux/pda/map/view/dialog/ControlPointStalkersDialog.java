package net.artux.pda.map.view.dialog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.ecs.interactive.map.SpawnComponent;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.root.FontManager;
import net.artux.pda.map.view.root.UserInterface;
import net.artux.pda.map.view.Utils;

import javax.inject.Inject;

public class ControlPointStalkersDialog extends PDADialog {

    private final UserInterface userInterface;
    private final LocaleBundle localeBundle;
    private final VerticalGroup verticalGroup;

    private SpawnComponent spawnComponent;

    @Inject
    public ControlPointStalkersDialog(FontManager fontManager, LocaleBundle localeBundle,
                                      UserInterface userInterface, DataRepository dataRepository) {
        super("", userInterface.getSkin());
        this.userInterface = userInterface;
        this.localeBundle = localeBundle;

        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor);
        textButtonStyle.font = titleLabelStyle.font;
        textButtonStyle.fontColor = Color.WHITE;

        setModal(true);
        setMovable(true);
        setResizable(false);

        verticalGroup = new VerticalGroup();
        verticalGroup.fill().expand().pad(10);
        ScrollPane scrollPane = new ScrollPane(verticalGroup);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setScrollbarsVisible(true);

        getContentTable().add(scrollPane).grow();
    }

    public void update(SpawnComponent spawnComponent) {
        if (spawnComponent.isEmpty())
            return;

        verticalGroup.clear();
        for (Entity entity : spawnComponent.getStalkerGroup().getEntities()){
            addStalker(entity);
        }
    }

    private void addStalker(Entity entity) {

    }

    @Override
    public float getPrefWidth() {
        return userInterface.getWidth() * 2 / 3f;
    }

    @Override
    public float getPrefHeight() {
        return userInterface.getHeight() * 2 / 3f;
    }

}
