package net.artux.pda.map.ui.blocks;

import static net.artux.pdalib.Checker.isInteger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.MessageGenerator;
import net.artux.pda.map.ui.DebugMenu;
import net.artux.pda.map.ui.Fonts;
import net.artux.pda.map.ui.TextureActor;
import net.artux.pdalib.UserMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MessagesBlock extends ScrollPane implements Disposable {

    private final MessageGenerator messageGenerator = new MessageGenerator();
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private final Timer timer = new Timer();
    private final BitmapFont font;
    int w = Gdx.graphics.getWidth();

    public MessagesBlock(final AssetManager assetManager){
        super(new Table().left().bottom());

        font = Fonts.generateFont(Fonts.Language.RUSSIAN, 24);
        setScrollingDisabled(true, false);
        setSize(w / 3f, 300);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addMessage(assetManager, messageGenerator.generateMessage());
            }
        }, MathUtils.random(60000, 120000), MathUtils.random(60000, 120000));

    }

    public void addMessage(final AssetManager assetManager, UserMessage message) {
        final Table messagesTable = (Table) getActor();
        final Table mainGroup = new Table().left();

        Texture image;
        if (isInteger(message.avatarId))
            image = assetManager
                    .get("avatars/a" + (Integer.parseInt(message.avatarId)) + ".png", Texture.class);
        else
            image = assetManager
                    .get("avatars/a0.jpg", Texture.class);
        mainGroup.add(new TextureActor(image));

        Table contentGroup = new Table().left().top();
        mainGroup.add(contentGroup);
        messagesTable.row();
        messagesTable.add(mainGroup).fill();

        Label title = new Label(simpleDateFormat.format(new Date(message.time)) + " " + message.senderLogin, getLabelStyle());
        title.setAlignment(Align.left);
        contentGroup.row();
        contentGroup.add(title).fill();

        Label content = new Label(message.message, getLabelStyle());
        content.setWrap(true);
        content.setFontScale(0.8f);
        content.setAlignment(Align.left);
        content.setWidth(w / 4);
        contentGroup.row();
        contentGroup.add(content).expandX().prefWidth(w / 3);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                messagesTable.removeActor(mainGroup);
            }
        }, 20000);
    }

    public Label.LabelStyle getLabelStyle() {
        return new Label.LabelStyle(font, Color.WHITE);
    }

    @Override
    public void dispose() {
        font.dispose();
        timer.cancel();
        timer.purge();
    }
}
