package net.artux.pda.map.view.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.engine.ecs.components.Group;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.blocks.SlotTextButton;
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.user.Gang;

import javax.inject.Inject;

public class ControlPointDialog extends PDADialog {

    private final DataRepository dataRepository;
    private final UserInterface userInterface;
    private final LocaleBundle localeBundle;

    private final Label pointDescLabel;
    private final Label takenLabel;
    private final Label quantityLabel;
    private final Label strengthLabel;
    private final Table manageTable;

    @Inject
    public ControlPointDialog(FontManager fontManager, LocaleBundle localeBundle,
                              SlotTextButton updateLevelButton,
                              SlotTextButton contentButton,
                              SlotTextButton hireStalkerButton,
                              UserInterface userInterface, DataRepository dataRepository) {
        super("", userInterface.getSkin());
        this.userInterface = userInterface;
        this.localeBundle = localeBundle;
        this.dataRepository = dataRepository;

        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor);
        textButtonStyle.font = titleLabelStyle.font;
        textButtonStyle.fontColor = Color.WHITE;

        TextButton btnClose = new TextButton(localeBundle.get("main.close"), textButtonStyle);

        setModal(true);
        setMovable(true);
        setResizable(false);

        pointDescLabel = new Label("", titleLabelStyle);
        pointDescLabel.setWrap(true);

        takenLabel = new Label("", titleLabelStyle);
        quantityLabel = new Label("", titleLabelStyle);
        strengthLabel = new Label("", titleLabelStyle);

        btnClose.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                hide();
                cancel();
                remove();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.fill().expand().pad(10);
        ScrollPane scrollPane = new ScrollPane(verticalGroup);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setScrollbarsVisible(true);

        Container<Label> container = new Container<>(pointDescLabel)
                .fill().padBottom(20f);
        getContentTable().add(scrollPane).grow();
        verticalGroup.addActor(container);

        verticalGroup.addActor(takenLabel);
        verticalGroup.addActor(quantityLabel);
        verticalGroup.addActor(strengthLabel);

        updateLevelButton.setText(localeBundle.get("point.update"));
        hireStalkerButton.setText(localeBundle.get("point.stalker.hire"));
        contentButton.setText(localeBundle.get("point.stalker.content"));

        manageTable = new Table();

        manageTable.add(getButtonTable());

        manageTable.add(updateLevelButton);
        manageTable.add(hireStalkerButton);
        manageTable.row();
        manageTable.add(contentButton);

        verticalGroup.addActor(manageTable);

        getButtonTable().add(btnClose).grow();
    }

    public void update(Group group, SpawnComponent spawnComponent) {
        SpawnModel spawnModel = spawnComponent.getSpawnModel();
        getTitleLabel().setText(localeBundle.get("point", spawnModel.getTitle()));
        pointDescLabel.setText(spawnModel.getDescription());
        Gang playerGang = dataRepository.getStoryDataModel().getGang();

        if (group != null) {
            Gang gang = group.getGang();
            takenLabel.setText(localeBundle.get("point.taken", localeBundle.get(gang.getTitleId())));
            quantityLabel.setText(localeBundle.get("point.quantity", group.getEntities().size));
            strengthLabel.setText(localeBundle.get("point.strength", "WEAK"));

            manageTable.setVisible(gang == playerGang);
        } else {
            takenLabel.setText(localeBundle.get("point.taken", localeBundle.get("point.not.taken")));
            quantityLabel.setText("");
            strengthLabel.setText("");

            manageTable.setVisible(false);
        }

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
