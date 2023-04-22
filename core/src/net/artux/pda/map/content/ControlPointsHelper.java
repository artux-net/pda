package net.artux.pda.map.content;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import net.artux.engine.utils.LocaleBundle;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.GroupComponent;
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent;
import net.artux.pda.map.engine.ecs.systems.EntityProcessorSystem;
import net.artux.pda.map.utils.Colors;
import net.artux.pda.map.utils.di.components.MapComponent;
import net.artux.pda.map.view.FontManager;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.view.bars.Utils;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.SpawnModel;

public class ControlPointsHelper {

    public static void createControlPointsEntities(MapComponent mapComponent) {
        GameMap map = mapComponent.getDataRepository().getGameMap();
        EntityProcessorSystem processor = mapComponent.getEntityProcessor();
        LocaleBundle localeBundle = mapComponent.getLocaleBundle();
        UserInterface userInterface = mapComponent.getUserInterface();
        FontManager fontManager = mapComponent.getAssetsFinder().getFontManager();


        Label.LabelStyle titleLabelStyle = fontManager.getLabelStyle(38, Color.WHITE);

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = Utils.getColoredDrawable(1, 1, Colors.primaryColor);
        textButtonStyle.font = titleLabelStyle.font;
        textButtonStyle.fontColor = Color.WHITE;

        TextButton btnYes = new TextButton(localeBundle.get("main.close"), textButtonStyle);

        Skin skinDialog = userInterface.getSkin();
        Dialog dialog = new Dialog("", skinDialog) {
            @Override
            public float getPrefWidth() {
                return userInterface.getWidth() * 2 / 3f;
            }

            @Override
            public float getPrefHeight() {
                return userInterface.getHeight() * 2 / 3f;
            }
        };
        dialog.setModal(true);
        dialog.setMovable(true);
        dialog.setResizable(false);

        Label pointTitleLabel = new Label("", titleLabelStyle);
        Label pointDescLabel = new Label("", titleLabelStyle);
        pointDescLabel.setWrap(true);

        Label takenLabel = new Label("", titleLabelStyle);
        Label quantityLabel = new Label("", titleLabelStyle);
        Label strengthLabel = new Label("", titleLabelStyle);


        btnYes.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                dialog.hide();
                dialog.cancel();
                dialog.remove();
                return super.touchDown(event, x, y, pointer, button);
            }
        });


        Drawable drawable = Utils.getColoredDrawable(1, 1, Colors.backgroundColor);
        dialog.setBackground(drawable);

        VerticalGroup verticalGroup = new VerticalGroup();
        verticalGroup.fill().expand().pad(10);
        ScrollPane scrollPane = new ScrollPane(verticalGroup);
        scrollPane.setClamp(false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(false, false);
        scrollPane.setScrollbarsVisible(true);

        Container<Label> container = new Container<>(pointDescLabel)
                .fill().padBottom(20f);
        dialog.getContentTable().add(scrollPane).grow();
        pointTitleLabel.setAlignment(Align.center);
        verticalGroup.addActor(pointTitleLabel);
        verticalGroup.addActor(container);

        verticalGroup.addActor(takenLabel);
        verticalGroup.addActor(quantityLabel);
        verticalGroup.addActor(strengthLabel);

        dialog.getButtonTable().add(btnYes).grow();


        if (map.getSpawns() != null)
            for (int i = 0; i < map.getSpawns().size(); i++) {
                SpawnModel spawnModel = map.getSpawns().get(i);
                Entity entity = processor.generateSpawn(spawnModel);
                entity.add(new ClickComponent(spawnModel.getR(),
                        () -> {
                            pointTitleLabel.setText(localeBundle.get("point", spawnModel.getTitle()));
                            pointDescLabel.setText(spawnModel.getDescription());

                            SpawnComponent spawnComponent = (SpawnComponent) entity.getComponent(SpawnComponent.class);
                            GroupComponent groupComponent = spawnComponent.getGroupComponent();
                            if (groupComponent != null) {
                                takenLabel.setText(localeBundle.get("point.taken", groupComponent.getGang()));
                                quantityLabel.setText(localeBundle.get("point.quantity", groupComponent.getEntities().size()));
                                strengthLabel.setText(localeBundle.get("point.strength", "WEAK"));
                            } else {
                                takenLabel.setText(localeBundle.get("point.taken", localeBundle.get("point.not.taken")));
                                quantityLabel.setText("");
                                strengthLabel.setText("");
                            }


                            dialog.show(userInterface.getStage());
                        }));

            }
    }

}