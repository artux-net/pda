package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.bars.Utils;

public class DebugMenu extends Table implements Disposable {

    private Logger logger;
    private Skin skin;

    private Label label;
    private Table content;

    public DebugMenu(final UserInterface userInterface, final Engine engine, Skin skin) {
        super();
        this.skin = skin;
        setSkin(skin);
        logger = new Logger(engine, skin);

        Table hudTable = userInterface.getAssistantBlock();
        hudTable.row();
        hudTable.add(logger).fillX();

        top();
        left();

        label = new Label("Режим тестирования", userInterface.getLabelStyle());
        top();
        add(label);
        row();
        content = new Table();
        content.defaults().align(Align.left);
        content.left();
        ScrollPane scrollPane = new ScrollPane(content, skin);
        scrollPane.setScrollingDisabled(true, false);
        add(scrollPane).growX();


        setBackground(Utils.getColoredDrawable(1, 1, Color.BLACK));

        addCheckBox("Ускорение движения", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.speedup = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.speedup);

        addCheckBox("Вечный бег", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.alwaysRun = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.alwaysRun);

        addCheckBox("Учитывать столкновения", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.playerWalls = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.playerWalls);
        addCheckBox("Отобразить стены игрока", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showPlayerWalls);

        addCheckBox("Показать границы UI элементов", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                userInterface.setDebug(((CheckBox) actor).isChecked(), true);
            }
        }, false);

        addCheckBox("Отобразить карту ИИ", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showTiles = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showTiles);
        addCheckBox("Пути ИИ", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showPaths);

        addCheckBox("Показывать все сущности", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                RenderSystem.showAll = ((CheckBox) actor).isChecked();
            }
        }, RenderSystem.showAll);

        addCheckBox("Логгер", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.visible = ((CheckBox) actor).isChecked();
            }
        }, Logger.visible);

        addCheckBox("Фоновая музыка", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((CheckBox) actor).isChecked())
                    engine.getSystem(SoundsSystem.class).startBackgroundMusic();
                else
                    engine.getSystem(SoundsSystem.class).stopMusic();
            }
        }, true);
    }

    private void addCheckBox(String title, ChangeListener changeListener, boolean checked) {
        CheckBox checkBox = new CheckBox(title, skin);
        //checkBox.getStyle().fontColor = Color.WHITE;
        //.getStyle().font = Fonts.getFont(Fonts.Language.RUSSIAN, 25);
        content.row();
        content.add(checkBox);
        checkBox.getImage().setScaling(Scaling.fit);
        checkBox.getImageCell().size(50, 50);
        checkBox.addListener(changeListener);
        checkBox.setChecked(checked);
    }

    @Override
    public void dispose() {
        logger.dispose();
    }
}
