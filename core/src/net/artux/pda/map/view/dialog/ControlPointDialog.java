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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.commands.Commands;
import net.artux.pda.map.content.entities.StrengthUpdater;
import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.map.ecs.interactive.map.SpawnComponent;
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

import java.util.Collections;

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

    private final SlotTextButton updateLevelButton;

    private SpawnComponent spawnComponent;

    @Inject
    public ControlPointDialog(FontManager fontManager, LocaleBundle localeBundle,
                              MapComponent mapComponent,
                              ControlPointStalkersDialog controlPointStalkersDialog,
                              SlotTextButton updateLevelButton,
                              SlotTextButton stalkersButton,
                              SlotTextButton hireStalkerButton,
                              StrengthUpdater strengthUpdater,
                              UserInterface userInterface, DataRepository dataRepository) {
        super("", userInterface.getSkin());
        this.userInterface = userInterface;
        this.localeBundle = localeBundle;
        this.dataRepository = dataRepository;
        this.updateLevelButton = updateLevelButton;

        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor);
        textButtonStyle.font = titleLabelStyle.font;
        textButtonStyle.fontColor = Color.WHITE;

        setModal(true);
        setMovable(true);
        setResizable(false);

        pointDescLabel = new Label("", titleLabelStyle);
        pointDescLabel.setWrap(true);

        takenLabel = new Label("", titleLabelStyle);
        quantityLabel = new Label("", titleLabelStyle);
        strengthLabel = new Label("", titleLabelStyle);

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

        updateLevelButton.setText(localeBundle.get("point.update", 0));
        hireStalkerButton.setText(localeBundle.get("point.stalker.hire"));
        stalkersButton.setText(localeBundle.get("point.stalkers"));
        stalkersButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                controlPointStalkersDialog.update(spawnComponent);
                controlPointStalkersDialog.show(getStage());
            }
        });

        manageTable = new Table();
        manageTable.defaults().pad(10);
        manageTable.add(stalkersButton);
        manageTable.add(updateLevelButton);
        updateLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (updateLevelButton.isDisabled())
                    return;
                if (spawnComponent == null || spawnComponent.isEmpty())
                    return;

                StalkerGroup stalkerGroup = spawnComponent.getStalkerGroup();
                Strength currentStrength = stalkerGroup.getStrength();
                if (currentStrength.getNext() == currentStrength)
                    return;

                Strength nextStrength = currentStrength.getNext();
                stalkerGroup.setStrength(nextStrength);
                Array<Entity> stalkers = spawnComponent.getStalkerGroup().getEntities();
                for (int i = 0; i < stalkers.size; i++) {
                    Entity entity = stalkers.get(i);
                    strengthUpdater.updateStalker(entity, nextStrength);
                }
                dataRepository.applyActions(Collections
                        .singletonMap(Commands.MONEY, Collections.singletonList("-" + nextStrength.getPrice())));
                mapComponent.getManager().save();
                update(spawnComponent);
            }
        });

        verticalGroup.addActor(manageTable);

        Drawable background = Utils.getColoredDrawable(1, 1, Colors.backgroundAlphaColor);
        setBackground(background);
    }

    public void update(SpawnComponent spawnComponent) {
        this.spawnComponent = spawnComponent;
        SpawnModel spawnModel = spawnComponent.getSpawnModel();
        StalkerGroup stalkerGroup = spawnComponent.getStalkerGroup();
        getTitleLabel().setText(localeBundle.get("point", spawnModel.getTitle()));
        pointDescLabel.setText(spawnModel.getDescription());
        Gang playerGang = dataRepository.getCurrentStoryDataModel().getGang();

        if (stalkerGroup != null) {
            Gang gang = stalkerGroup.getGang();
            takenLabel.setText(localeBundle.get("point.taken", localeBundle.get(gang.getTitleId())));
            quantityLabel.setText(localeBundle.get("point.quantity", stalkerGroup.getEntities().size));
            strengthLabel.setText(localeBundle.get("point.strength", localeBundle.get(stalkerGroup.getStrength().getTitleId())));

            Strength nextStrength = stalkerGroup.getStrength().getNext();

            updateLevelButton.setDisabled(nextStrength == stalkerGroup.getStrength());
            int price = nextStrength.getPrice();
            updateLevelButton.setText(localeBundle.get("point.update", price));
            if (!updateLevelButton.isDisabled()) {
                updateLevelButton.setDisabled(dataRepository.getCurrentStoryDataModel().getMoney() < price);
            }
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
