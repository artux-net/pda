package net.artux.pda.map.di.core.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.DebugMenu;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.MissionMenu;
import net.artux.pda.map.ui.UIFrame;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pda.model.map.GameMap;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@Module(includes = RootInterfaceModule.class)
public class HeaderInterfaceModule {

    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

    @IntoSet
    @Provides
    public Actor initHeader(Timer timer, MissionMenu missionMenu, GameMap map,
                            @Named("gameZone") Group gameZone,
                            Label.LabelStyle labelStyle, UIFrame uiFrame,
                            AssetManager assetManager, DataRepository dataRepository) {

        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(assetManager.get("ui/exit.png", Texture.class));
        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle(pauseDrawable, pauseDrawable, pauseDrawable);
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.clicked(event, x, y);
                Gdx.app.debug("UserInterface", "touched pause - user interface");
                dataRepository.applyActions(Collections.emptyMap());
                HashMap<String, String> data = new HashMap<>();
                data.put("openPda", "");
                dataRepository.getPlatformInterface().send(data);
            }
        });

        Button.ButtonStyle occupationsButtonStyle = new Button.ButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("ui/burger.png", Texture.class));
        Button menuButton = new Button(occupationsButtonStyle);

        Group missionsContainer = new Group();
        missionsContainer.setHeight(gameZone.getHeight());
        missionsContainer.setWidth(gameZone.getWidth()/4);
        missionsContainer.addActor(missionMenu);

        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (gameZone.getChildren().indexOf(missionsContainer, false) == -1)
                    gameZone.addActor(missionsContainer);
                else
                    gameZone.removeActor(missionsContainer);
            }
        });

        Label timeLabel = new Label("00:00", labelStyle);
        uiFrame.getLeftHeaderTable().add(timeLabel);
        uiFrame.getLeftHeaderTable().add(new Label(map.getTitle(), labelStyle));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeLabel.setText(simpleDateFormat.format(new Date())); // TODO eat memory every time
            }
        }, 0, 2000);

        uiFrame.getLeftHeaderTable().add(menuButton);
        uiFrame.getRightHeaderTable().add(pauseButton);

        return menuButton;
    }

    @IntoSet
    @Provides
    public Actor initDebugMode(@Named("gameZone") Group gameZone, @Named("assistantTable") Table assistantTable,
                               Logger logger, UIFrame uiFrame, UserInterface userInterface,
                               Skin skin, Label.LabelStyle labelStyle,
                               Engine engine, AssetManager assetManager, Properties properties) {
        if (properties.getProperty(PropertyFields.TESTER_MODE, "false").equals("true")) {
            Button.ButtonStyle bugStyle = new Button.ButtonStyle();
            bugStyle.up = new TextureRegionDrawable(assetManager.get("ui/bug.png", Texture.class));

            final Button debugButton = new Button(bugStyle);
            final DebugMenu debugMenu = new DebugMenu(skin, labelStyle);
            debugMenu.setFillParent(true);

            debugMenu.row();
            debugMenu.add(logger);

            debugButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Stack widget = userInterface.getStack();
                    if (widget.getChildren().indexOf(debugMenu, false) == -1)
                        widget.add(debugMenu);
                    else
                        widget.removeActor(debugMenu);
                }
            });

            debugMenu.addCheckBox("Ускорение движения", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.speedup = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.speedup);

            debugMenu.addCheckBox("Вечный бег", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.alwaysRun = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.alwaysRun);

            debugMenu.addCheckBox("Учитывать столкновения", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MovingSystem.playerWalls = ((CheckBox) actor).isChecked();
                }
            }, MovingSystem.playerWalls);
            debugMenu.addCheckBox("Отобразить стены игрока", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPlayerWalls);

            debugMenu.addCheckBox("Показать границы UI элементов", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    userInterface.setDebug(((CheckBox) actor).isChecked(), true);
                }
            }, false);

            debugMenu.addCheckBox("Отобразить карту ИИ", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showTiles = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showTiles);
            debugMenu.addCheckBox("Пути ИИ", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPaths);

            debugMenu.addCheckBox("Показывать все сущности", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RenderSystem.showAll = ((CheckBox) actor).isChecked();
                }
            }, RenderSystem.showAll);

            debugMenu.addCheckBox("Логгер", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Logger.visible = ((CheckBox) actor).isChecked();
                }
            }, Logger.visible);

            debugMenu.addCheckBox("Фоновая музыка", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (((CheckBox) actor).isChecked())
                        engine.getSystem(SoundsSystem.class).startBackgroundMusic();
                    else
                        engine.getSystem(SoundsSystem.class).stopMusic();
                }
            }, true);

            uiFrame.getLeftHeaderTable().add(debugButton);
            return debugButton;
        }
        return new Actor();
    }

}
