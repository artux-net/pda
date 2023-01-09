package net.artux.pda.map.engine;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;

import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.blocks.MessagesPlane;
import net.artux.pda.model.UserMessage;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class StalkerMessageGenerator {

    private final ContentGenerator contentGenerator;
    private final MessagesPlane messagesPlane;
    private final SoundsSystem soundsSystem;
    private final AssetManager assetManager;

    @Inject
    public StalkerMessageGenerator(AssetManager assetManager, SoundsSystem soundsSystem, MessagesPlane messagesPlane, Timer timer, ContentGenerator contentGenerator) {
        this.contentGenerator = contentGenerator;
        this.assetManager = assetManager;
        this.soundsSystem = soundsSystem;
        this.messagesPlane = messagesPlane;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                addMessage(generateMessage());
            }
        }, MathUtils.random(60000, 120000), MathUtils.random(60000, 120000));
    }

    public void addMessage(UserMessage message) {
        soundsSystem.playSound(assetManager.get("audio/sounds/pda/pda_tip.ogg"));
        messagesPlane.addMessage(message.getAuthor().getAvatar(),
                message.getAuthor().getLogin(),
                message.getContent(), MessagesPlane.Length.LONG);
    }

    public UserMessage generateMessage() {
        return new UserMessage(contentGenerator.generateName(), contentGenerator.generateMessageContent(), String.valueOf(random(1, 30)));
    }
}
