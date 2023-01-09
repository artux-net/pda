package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.AssetsFinder;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class MessagesPlane extends ScrollPane {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private final AssetManager assetManager;
    private final BitmapFont font;
    private final Table table;
    private final Timer timer;

    int w = Gdx.graphics.getWidth();

    @Inject
    public MessagesPlane(Timer timer, AssetsFinder finder) {
        super(new Table().left().bottom());
        this.assetManager = finder.getManager();
        this.timer = timer;

        table = (Table) getActor();
        font = finder.getFontManager().getFont(24);
        setScrollingDisabled(true, false);
        setSize(w / 3f, 300);
    }

    public Table getTable() {
        return table;
    }

    public void addMessage(String icon, String title, String content, Length length) {
        Table mainGroup = new Table().left();

        Image image = new Image(assetManager
                .get(icon, Texture.class));

        image.setScaling(Scaling.fillX);
        mainGroup
                .add(image)
                .uniform();

        Table contentGroup = new Table().left().top();
        mainGroup.add(contentGroup);
        table.row();
        table.add(mainGroup).fill();

        title = timeFormatter.format(Instant.now()) + " " + title;
        Label titleLabel = new Label(title, getLabelStyle());
        titleLabel.setAlignment(Align.left);
        contentGroup.row();
        contentGroup.add(titleLabel).fill();

        Label contentContent = new Label(content, getLabelStyle());
        contentContent.setWrap(true);
        contentContent.setAlignment(Align.left);
        contentGroup.row();
        contentGroup
                .add(contentContent)
                .growX();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeCellWithActor(mainGroup);
            }
        }, length.millis);
    }

    private void removeCellWithActor(Actor remove) {
        for (Actor actor : table.getChildren()) {
            if (actor == remove) {
                Cell<Actor> cell = table.getCell(actor);
                actor.remove();
                // remove cell from table
                table.getCells().removeValue(cell, true);
                table.invalidate();
            }
        }
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    public enum Length {
        LONG(20000),
        SHORT(13000);

        private final int millis;

        Length(int millis) {
            this.millis = millis;
        }
    }

}
