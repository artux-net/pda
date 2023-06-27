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
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.map.SpawnModel;
import net.artux.pda.model.map.Strength;
import net.artux.pda.model.user.Gang;

import javax.inject.Inject;

public class ControlPointStalkersDialog extends PDADialog {

    private final DataRepository dataRepository;
    private final UserInterface userInterface;
    private final LocaleBundle localeBundle;

    private final Label pointDescLabel;
    private final Label takenLabel;
    private final Label quantityLabel;
    private final Label strengthLabel;
    private final Table manageTable;

    private SpawnComponent spawnComponent;

    @Inject
    public ControlPointStalkersDialog(FontManager fontManager, LocaleBundle localeBundle,
                                      EngineManager engineManager,
                                      SlotTextButton updateLevelButton,
                                      SlotTextButton stalkersButton,
                                      SlotTextButton hireStalkerButton,
                                      StrengthUpdater strengthUpdater,
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

        updateLevelButton.setText(localeBundle.get("point.update"));
        hireStalkerButton.setText(localeBundle.get("point.stalker.hire"));
        stalkersButton.setText(localeBundle.get("point.stalkers"));

        manageTable = new Table();
        manageTable.add(stalkersButton);
        manageTable.add(updateLevelButton);
        updateLevelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (spawnComponent == null || spawnComponent.isEmpty())
                    return;

                //dataRepository.applyActions(Collections.singletonMap(Commands.MONEY, Collections.singletonList("")));

                // TODO
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

                engineManager.save();
                update(spawnComponent);
            }
        });

        verticalGroup.addActor(manageTable);
    }

    public void update(SpawnComponent spawnComponent) {
        this.spawnComponent = spawnComponent;
        SpawnModel spawnModel = spawnComponent.getSpawnModel();
        StalkerGroup stalkerGroup = spawnComponent.getStalkerGroup();
        getTitleLabel().setText(localeBundle.get("point", spawnModel.getTitle()));
        pointDescLabel.setText(spawnModel.getDescription());
        Gang playerGang = dataRepository.getInitDataModel().getGang();

        if (stalkerGroup != null) {
            Gang gang = stalkerGroup.getGang();
            takenLabel.setText(localeBundle.get("point.taken", localeBundle.get(gang.getTitleId())));
            quantityLabel.setText(localeBundle.get("point.quantity", stalkerGroup.getEntities().size));
            strengthLabel.setText(localeBundle.get("point.strength", localeBundle.get(stalkerGroup.getStrength().getTitleId())));

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
