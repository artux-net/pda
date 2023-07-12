package net.artux.pda.map.view.dialog;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.content.entities.StrengthUpdater;
import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.map.ecs.interactive.map.SpawnComponent;
import net.artux.pda.map.engine.EngineManager;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.blocks.SlotTextButton;
import net.artux.pda.map.view.template.PDADialog;
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.map.Strength;
import net.artux.pda.model.user.Gang;

import javax.inject.Inject;

public class ControlPointStalkersDialog extends PDADialog {

    private final UserInterface userInterface;
    private final LocaleBundle localeBundle;
    private final VerticalGroup verticalGroup;

    private SpawnComponent spawnComponent;

    @Inject
    public ControlPointStalkersDialog(FontManager fontManager, LocaleBundle localeBundle,
                                      EngineManager engineManager,
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
