package net.artux.pda.map.di.modules.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.common.PropertyFields;
import net.artux.pda.map.ecs.ai.statemachine.StatesSystem;
import net.artux.pda.map.ecs.characteristics.HealthSystem;
import net.artux.pda.map.ecs.logger.MapLoggerSystem;
import net.artux.pda.map.ecs.physics.PlayerMovingSystem;
import net.artux.pda.map.ecs.render.RenderSystem;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.view.debug.DebugWindow;
import net.artux.pda.map.view.sidebar.NavigationBar;
import net.artux.pda.map.view.sidebar.MissionList;
import net.artux.pda.map.view.root.UIFrame;
import net.artux.pda.map.view.root.UserInterface;
import net.artux.pda.map.view.debug.widgets.CheckBoxWidget;
import net.artux.pda.map.view.window.StatisticWindow;
import net.artux.pda.model.map.GameMap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public Actor initGameZone(UserInterface userInterface, @Named("uiStage") Stage stage) {
        stage.addActor(userInterface);
        return userInterface;
    }

    @IntoSet
    @Provides
    public Actor initHeader(MissionList missionList,
                            NavigationBar navigationBar,
                            StatisticWindow statisticWindow,
                            GameMap map,
                            @Named("gameZone") Group gameZone,
                            Label.LabelStyle labelStyle, UIFrame uiFrame,
                            UserInterface userInterface,
                            Skin skin) {
        Group stack = userInterface.getStack();
        ImageButton pauseButton = new ImageButton(skin.get("close", ImageButton.ImageButtonStyle.class));
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

        Label missionsButton = new Label("Задачи", userInterface.getLabelStyle());
        Label navigationButton = new Label("Навигация", userInterface.getLabelStyle());
        Label statisticButton = new Label("Статистика", userInterface.getLabelStyle());

        missionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (stack.getChildren().indexOf(missionList, false) == -1)
                    stack.addActor(missionList);
                else
                    stack.removeActor(missionList);
                missionList.update();
            }
        });

        navigationButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (stack.getChildren().indexOf(navigationBar, false) == -1)
                    stack.addActor(navigationBar);
                else
                    stack.removeActor(navigationBar);
                navigationBar.update();
            }
        });

        statisticButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (stack.getChildren().contains(statisticWindow, false))
                    stack.removeActor(statisticWindow);
                else {
                    stack.addActor(statisticWindow);
                }
            }
        });

        Label timeLabel = new Label("00:00", labelStyle);
        uiFrame.getMenu()
                .add(timeLabel)
                .padLeft(20);

        uiFrame.getMenu()
                .add(new Label(map.getTitle(), labelStyle))
                .padRight(20);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                timeLabel.setText(timeFormatter.format(Instant.now()));
            }
        }, 0, 3);

        uiFrame.getMenu()
                .add(missionsButton, true)
                //.width(uiFrame.getLeftHeaderTable().getHeight())
                .uniform();
        uiFrame.getMenu()
                .add(navigationButton, true)
                .uniform();

        uiFrame.getMenu()
                .add(statisticButton, true)
                .uniform();
        uiFrame.getRightHeaderTable().add(pauseButton).uniform();

        return missionsButton;
    }

    @IntoSet
    @Provides
    public Actor initDebugMode(AudioSystem audioSystem, UIFrame uiFrame, UserInterface userInterface,
                               DebugWindow debugWindow, CheckBoxWidget checkBoxWidget,
                               Engine engine, Properties properties) {
        if (properties.get(PropertyFields.TESTER_MODE).equals(true)) {
            Label testModeLabel = new Label("Тестирование", userInterface.getLabelStyle());
            testModeLabel.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Stack stack = userInterface.getStack();
                    if (stack.getChildren().indexOf(debugWindow, false) == -1)
                        stack.add(debugWindow);
                    else
                        stack.removeActor(debugWindow);
                }
            });

            checkBoxWidget.addCheckBox("Бессмертие", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    engine.getSystem(HealthSystem.class).setImmortalPlayer(((CheckBox) actor).isChecked());
                }
            }, false);

            checkBoxWidget.addCheckBox("Ускорение движения", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PlayerMovingSystem.Companion.setSpeedup(((CheckBox) actor).isChecked());
                }
            }, PlayerMovingSystem.Companion.getSpeedup());

            checkBoxWidget.addCheckBox("Вечный бег", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    PlayerMovingSystem.Companion.setAlwaysRun(((CheckBox) actor).isChecked());
                }
            }, PlayerMovingSystem.Companion.getAlwaysRun());

            checkBoxWidget.addCheckBox("Отобразить стены", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPlayerWalls);

            checkBoxWidget.addCheckBox("Показать границы UI элементов", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    userInterface.setDebug(((CheckBox) actor).isChecked(), true);
                }
            }, false);

            checkBoxWidget.addCheckBox("Отобразить все слои карты", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    engine.getSystem(MapLoggerSystem.class).debugTiledMap(((CheckBox) actor).isChecked());
                }
            }, false);

            checkBoxWidget.addCheckBox("Отобразить состояния сущностей", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    engine.getSystem(StatesSystem.class).setDebugStates(((CheckBox) actor).isChecked());
                }
            }, false);

            checkBoxWidget.addCheckBox("Пути ИИ", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
                }
            }, MapLoggerSystem.showPaths);

            checkBoxWidget.addCheckBox("Показывать все сущности", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    RenderSystem.showAll = ((CheckBox) actor).isChecked();
                }
            }, RenderSystem.showAll);

            checkBoxWidget.addCheckBox("Звуки и музыка", new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    audioSystem.changeState(((CheckBox) actor).isChecked());
                }
            }, false);

            uiFrame.getMenu().add(testModeLabel, true);
            return testModeLabel;
        }
        return new Actor();
    }

}
