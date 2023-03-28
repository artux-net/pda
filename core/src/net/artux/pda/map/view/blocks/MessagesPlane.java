package net.artux.pda.map.view.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.content.assets.AssetsFinder;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.FontManager;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;

@PerGameMap
public class MessagesPlane extends ScrollPane {

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault());
    private final AssetManager assetManager;
    private final Table table;
    private final Label.LabelStyle titleStyle;
    private final Label.LabelStyle subtitleStyle;

    int w = Gdx.graphics.getWidth();

    @Inject
    public MessagesPlane(AssetsFinder finder) {
        super(new Table().left().bottom());
        this.assetManager = finder.getManager();

        table = (Table) getActor();
        setScrollingDisabled(true, false);
        setSize(w / 3f, 300);

        titleStyle = new Label.LabelStyle(finder.getFontManager().getFont(24), Color.WHITE);
        subtitleStyle = new Label.LabelStyle(finder.getFontManager().getFont(FontManager.LIBERAL_FONT, 20), Color.WHITE);
    }

    public Table getTable() {
        return table;
    }

    public void addMessage(String icon, String title, String content, Length length) {
        Table mainGroup = new Table();
        mainGroup.left();

        Image image = new Image(assetManager.get(icon, Texture.class));
        image.setScaling(Scaling.fillX);
        mainGroup
                .add(image)
                .uniform();

        Table contentGroup = new Table();
        contentGroup.defaults()
                .left()
                .top();
        mainGroup.add(contentGroup)
                .growX();
        table.row();
        table.add(mainGroup)
                .pad(20)
                .growX();

        title = timeFormatter.format(Instant.now()) + " " + title;
        Label titleLabel = new Label(title, titleStyle);
        contentGroup.row();
        contentGroup.add(titleLabel)
                .left()
                .growX();

        Label contentContent = new Label(content, subtitleStyle);
        contentContent.setWrap(true);
        contentContent.setAlignment(Align.left);
        contentGroup.row();
        contentGroup
                .add(contentContent)
                .growX()
                .prefWidth(w / 3f);

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                removeCellWithActor(mainGroup);
            }
        }, length.secs);
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

    public enum Length {
        LONG(20),
        SHORT(13);

        private final int secs;

        Length(int secs) {
            this.secs = secs;
        }
    }

}
