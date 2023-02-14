package net.artux.pda.map.repository;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.view.blocks.MessagesPlane;
import net.artux.pda.model.chat.UserMessage;

import javax.inject.Inject;

public class StalkerMessageGenerator {

    private final ContentGenerator contentGenerator;
    private final MessagesPlane messagesPlane;
    private final SoundsSystem soundsSystem;
    private final AssetManager assetManager;

    @Inject
    public StalkerMessageGenerator(AssetManager assetManager, SoundsSystem soundsSystem, MessagesPlane messagesPlane, ContentGenerator contentGenerator) {
        this.contentGenerator = contentGenerator;
        this.assetManager = assetManager;
        this.soundsSystem = soundsSystem;
        this.messagesPlane = messagesPlane;
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                addMessage(generateMessage());
            }
        }, MathUtils.random(60, 120), MathUtils.random(60, 120));
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
