package net.artux.pda.map.ui;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;

public class DebugMenu extends Table implements Disposable {

    private Logger logger;
    private Texture background;
    private Skin skin;

    public DebugMenu(final UserInterface userInterface, final Engine engine, Color color) {
        super();
        logger = new Logger(engine);
        skin = new Skin(Gdx.files.internal("data/assets/uiskin.json"));

        Table hudTable = userInterface.getAssistantBlock();
        hudTable.row();
        hudTable.add(logger);

        defaults().align(Align.left);

        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGB565);
        bgPixmap.setColor(color);
        bgPixmap.fill();

        background = new Texture(bgPixmap);
        bgPixmap.dispose();
        setBackground(new TextureRegionDrawable(new TextureRegion(background)));

        addCheckBox("Speedup", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.speedup = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.speedup);

        addCheckBox("Always run", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.running = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.running);

        addCheckBox("Player collision with walls", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MovingSystem.playerWalls = ((CheckBox) actor).isChecked();
            }
        }, MovingSystem.playerWalls);
        addCheckBox("Show Player walls", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPlayerWalls = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showPlayerWalls);

        addCheckBox("Show UI borders", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                userInterface.setDebug(((CheckBox) actor).isChecked(), true);
            }
        }, false);

        addCheckBox("Show AI tiles", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showTiles = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showTiles);
        addCheckBox("Show AI paths", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MapLoggerSystem.showPaths = ((CheckBox) actor).isChecked();
            }
        }, MapLoggerSystem.showPaths);

        addCheckBox("Logger", new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Logger.visible = ((CheckBox) actor).isChecked();
            }
        }, Logger.visible);

        addCheckBox("Background music", new ChangeListener() {
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
        row();
        add(checkBox);
        checkBox.getImageCell().size(50, 50);
        checkBox.addListener(changeListener);
        checkBox.setChecked(checked);
    }

    @Override
    public void dispose() {
        logger.dispose();
        background.dispose();
        skin.dispose();
    }
}
