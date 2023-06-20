package net.artux.pda.map.managers.notification;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.engine.ecs.systems.SoundsSystem;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.blocks.MessagesPlane;
import net.artux.pda.model.chat.ChatEvent;
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

    private final Label titleLabel;
    private final Group gameZone;
    private final Timer.Task delayTitleTask = new Timer.Task() {
        @Override
        public void run() {
            titleLabel.setVisible(false);
        }
    };

    @Inject
    public NotificationController(UserInterface userInterface, AssetManager assetManager, SoundsSystem soundsSystem, MessagesPlane messagesPlane, ContentGenerator contentGenerator) {
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
        gameZone = userInterface.getGameZone();

        titleLabel = new Label("test", userInterface.getLabelStyle());
        titleLabel.setTouchable(Touchable.disabled);
        titleLabel.setFillParent(true);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(0, gameZone.getHeight() / 4);
        titleLabel.setVisible(false);
        gameZone.addActor(titleLabel);
    }

    public void addMessage(UserMessage message) {
        soundsSystem.playSound(assetManager.get("audio/sounds/pda/pda_tip.ogg"));
        messagesPlane.addMessage(message.getAuthor().getAvatar(),
                message.getAuthor().getLogin(),
                message.getContent(), MessagesPlane.Length.LONG);
    }

    public void addMessage(String message) {
        addMessage(generateMessage(message));
    }

    public void notify(NotificationType type, String title, String content) {
        messagesPlane.addMessage(type.getIcon(),
                title,
                content, MessagesPlane.Length.SHORT);
        soundsSystem.playSound(soundMap.get(type));
    }

    public void msg(String message) {
        addMessage(UserMessage.event(ChatEvent.Companion.of(message)));
    }

    public UserMessage generateMessage(String msg) {
        return new UserMessage(contentGenerator.generateName(), msg,
                "textures/avatars/a" + random(1, 30) + ".png");
    }

    public UserMessage generateMessage() {
        return new UserMessage(contentGenerator.generateName(),
                contentGenerator.generateMessageContent(),
                "textures/avatars/a" + random(1, 30) + ".png");
    }

    public void setTitle(String titleText) {
        if (!titleLabel.getText().equals(titleText))
            titleLabel.setText(titleText);
        titleLabel.setVisible(true);

        if (delayTitleTask.isScheduled())
            delayTitleTask.cancel();

        Timer.schedule(delayTitleTask, 3);
    }

}
