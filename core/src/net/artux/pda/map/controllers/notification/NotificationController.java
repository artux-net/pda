package net.artux.pda.map.controllers.notification;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.repository.ContentGenerator;
import net.artux.pda.map.view.blocks.MessagesPlane;
import net.artux.pda.model.chat.UserMessage;

import java.util.EnumMap;

import javax.inject.Inject;

@PerGameMap
public class NotificationController {

    private final ContentGenerator contentGenerator;
    private final MessagesPlane messagesPlane;
    private final SoundsSystem soundsSystem;
    private final AssetManager assetManager;
    private final EnumMap<NotificationType, Sound> soundMap;

    @Inject
    public NotificationController(AssetManager assetManager, SoundsSystem soundsSystem, MessagesPlane messagesPlane, ContentGenerator contentGenerator) {
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

        soundMap = new EnumMap<>(NotificationType.class);
        for (NotificationType type : NotificationType.values()) {
            soundMap.put(type, assetManager.get(type.getSound()));
        }
    }

    public void addMessage(UserMessage message) {
        soundsSystem.playSound(assetManager.get("audio/sounds/pda/pda_tip.ogg"));
        messagesPlane.addMessage(message.getAuthor().getAvatar(),
                message.getAuthor().getLogin(),
                message.getContent(), MessagesPlane.Length.LONG);
    }

    public void notify(NotificationType type, String title, String content) {
        messagesPlane.addMessage(type.getIcon(),
                title,
                content, MessagesPlane.Length.SHORT);
        soundsSystem.playSound(soundMap.get(type));
    }

    public UserMessage generateMessage() {
        return new UserMessage(contentGenerator.generateName(),
                contentGenerator.generateMessageContent(),
                "avatars/a" + random(1, 30) + ".png");
    }

}
