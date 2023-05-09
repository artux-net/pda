package net.artux.pda.map.utils.di.modules.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.ecs.systems.MapLoggerSystem;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerMovingSystem;
import net.artux.pda.map.view.DebugMenu;
import net.artux.pda.map.view.Logger;
import net.artux.pda.map.view.MissionMenu;
import net.artux.pda.map.view.UIFrame;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.model.map.GameMap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Properties;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;


@Module(includes = RootInterfaceModule.class)
public class HeaderInterfaceModule {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());

    @IntoSet
    @Provides
    public Actor initHeader(MissionMenu missionMenu, GameMap map,
                            @Named("gameZone") Group gameZone,
                            Label.LabelStyle labelStyle, UIFrame uiFrame,
                            UserInterface userInterface,
                            AssetManager assetManager, DataRepository dataRepository) {

        TextureRegionDrawable pauseDrawable = new TextureRegionDrawable(assetManager.get("textures/ui/exit.png", Texture.class));
        Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle(pauseDrawable, pauseDrawable, pauseDrawable);
        Button pauseButton = new Button(pauseButtonStyle);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                Gdx.app.debug("UserInterface", "touched pause - user interface");
                int lastIndex = userInterface.getStack().getChildren().size - 1;
                userInterface.getStack().removeActorAt(lastIndex, true);
            }
        });

        pauseButton.addAction(new Action() {
            @Override
            public boolean act(float delta) {
                pauseButton.setVisible(userInterface.getStack().getChildren().size > 1);
                return false;
            }
        });

        ImageButton.ImageButtonStyle occupationsButtonStyle = new ImageButton.ImageButtonStyle();
        occupationsButtonStyle.up = new TextureRegionDrawable(assetManager.get("textures/ui/burger.png", Texture.class));
        ImageButton missionsButton = new ImageButton(occupationsButtonStyle);
        missionsButton.getImage().setScaling(Scaling.fit);

        Group missionsContainer = new Group();
        missionsContainer.setHeight(gameZone.getHeight());
        missionsContainer.setWidth(gameZone.getWidth() / 4);
        missionsContainer.addActor(missionMenu);

        missionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (gameZone.getChildren().indexOf(missionsContainer, false) == -1)
                    gameZone.addActor(missionsContainer);
                else
                    gameZone.removeActor(missionsContainer);
                missionMenu.update(MissionMenu.Type.MISSIONS);
            }
        });

        Label timeLabel = new Label("00:00", labelStyle);
        uiFrame.getLeftHeaderTable()
                .add(timeLabel)
                .padLeft(20);

        uiFrame.getLeftHeaderTable()
                .add(new Label(map.getTitle(), labelStyle))
                .padRight(20);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                timeLabel.setText(timeFormatter.format(Instant.now()));
            }
        }, 0, 3);

        uiFrame.getLeftHeaderTable()
                .add(missionsButton)
                //.width(uiFrame.getLeftHeaderTable().getHeight())
                .uniform();

        uiFrame.getRightHeaderTable().add(pauseButton).uniform();

        return missionsButton;
    }

    @IntoSet
    @Provides
    public Actor initDebugMode(SoundsSystem soundsSystem, Logger logger, UIFrame uiFrame, UserInterface userInterface,
                               Skin skin, Label.LabelStyle labelStyle,
                               Engine engine, AssetManager assetManager, Properties properties) {
        if (properties.getProperty(PropertyFields.TESTER_MODE, "false").equals("true")) {
            Button.ButtonStyle bugStyle = new Button.ButtonStyle();
            bugStyle.up = new TextureRegionDrawable(assetManager.get("textures/ui/bug.png", Texture.class));

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
                    PlayerMovingSystem.Companion.setSpeedup(((CheckBox) actor).isChecked());
                }
            }, PlayerMovingSystem.Companion.getSpeedup());

            debugMenu.addCheckBox("Вечный бег", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PlayerMovingSystem.Companion.setAlwaysRun(((CheckBox) actor).isChecked());
                }
            }, PlayerMovingSystem.Companion.getAlwaysRun());

            debugMenu.addCheckBox("Отобразить стены", new ChangeListener() {
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

            debugMenu.addCheckBox("Отобразить все слои карты", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    engine.getSystem(MapLoggerSystem.class).debugTiledMap(((CheckBox) actor).isChecked());
                }
            }, false);
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

            debugMenu.addCheckBox("Звуки и музыка", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    soundsSystem.changeState(((CheckBox) actor).isChecked());
                }
            }, false);

            uiFrame.getLeftHeaderTable().add(debugButton);
            return debugButton;
        }
        return new Actor();
    }

}
