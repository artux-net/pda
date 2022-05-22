package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;

public class DebugMenu extends Table {

    private Logger logger;

    public DebugMenu(final UserInterface userInterface, final Engine engine) {
        super();
        logger = new Logger(engine);

        Table hudTable = userInterface.getHudTable();
        hudTable.row();
        hudTable.add(logger);

        addCheckBox("Show UI borders", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (Gdx.app.getLogLevel() == Application.LOG_DEBUG)
                    userInterface.setDebug(((CheckBox) actor).isChecked(), true);
            }
        });
        addCheckBox("Show AI tiles", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showTiles = ((CheckBox) actor).isChecked();
            }
        });
        addCheckBox("Show AI paths", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
            }
        });
        addCheckBox("Logger", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                logger.visible = ((CheckBox) actor).isChecked();
            }
        });
        addCheckBox("Player collision with walls", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.playerWalls = ((CheckBox) actor).isChecked();
            }
        });
        addCheckBox("Show Player walls", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
            }
        });
        addCheckBox("Background music", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (((CheckBox) actor).isChecked())
                    engine.getSystem(SoundsSystem.class).startBackgroundMusic();
                else
                    engine.getSystem(SoundsSystem.class).stopMusic();
            }
        });
    }

    private void addCheckBox(String title, ChangeListener changeListener) {
        CheckBox checkBox = new CheckBox(title, new Skin(Gdx.files.internal("data/assets/uiskin.json")));
        checkBox.getImageCell().size(50, 50);
        checkBox.addListener(changeListener);
        row();
        add(checkBox);
    }

}
