package net.artux.pda.map.ui.blocks;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;

import net.artux.pda.map.engine.AssetsFinder;
import net.artux.pda.map.engine.ContentGenerator;
import net.artux.pda.map.engine.MessageGenerator;
import net.artux.pda.model.UserMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MessagesBlock extends ScrollPane implements Disposable {

    private final MessageGenerator messageGenerator = new MessageGenerator(new ContentGenerator());// TODO
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Timer timer = new Timer();
    private final BitmapFont font;
    int w = Gdx.graphics.getWidth();
    private AssetManager assetManager;
    private final Table table;

    public MessagesBlock(AssetsFinder finder) {
        super(new Table().left().bottom());
        table = (Table) getActor();
        this.assetManager = finder.getManager();
        font = finder.getFontManager().getFont(24);
        setScrollingDisabled(true, false);
        setSize(w / 3f, 300);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addMessage(messageGenerator.generateMessage());
            }
        }, MathUtils.random(60000, 120000), MathUtils.random(60000, 120000));

    }

    public Table getTable() {
        return table;
    }

    public void addMessage(UserMessage message) {
        addMessage(message.getAuthor().getAvatar(),
                message.getContent(),
                simpleDateFormat.format(new Date(message.getTimestamp().toEpochMilli())) + " " + message.getAuthor().getLogin());
    }

    public void addMessage(String icon, String content, String title) {
        final Table mainGroup = new Table().left();

        Image image;
        if (icon.matches("^\\d*$"))
            image = new Image(assetManager
                    .get("avatars/a" + (Integer.parseInt(icon)) + ".png", Texture.class));
        else
            image = new Image(assetManager
                    .get("avatars/a0.jpg", Texture.class));

        image.setScaling(Scaling.fillX);
        mainGroup.add(image).width(w / 9f);

        Table contentGroup = new Table().left().top();
        mainGroup.add(contentGroup);
        table.row();
        table.add(mainGroup).fill();

        Label titleLabel = new Label(title, getLabelStyle());
        titleLabel.setAlignment(Align.left);
        contentGroup.row();
        contentGroup.add(titleLabel).fill();

        Label contentContent = new Label(content, getLabelStyle());
        contentContent.setWrap(true);
        contentContent.setFontScale(0.8f);
        contentContent.setAlignment(Align.left);
        contentContent.setWidth(w / 4);
        contentGroup.row();
        contentGroup.add(contentContent).expandX().prefWidth(w / 3);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                removeCellWithActor(mainGroup);
            }
        }, 20000);
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

    @Override
    public void dispose() {
        timer.cancel();
        timer.purge();
    }
}
