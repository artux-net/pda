package net.artux.pda.map.managers.notification;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.content.ContentGenerator;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.view.UserInterface;
import net.artux.pda.map.view.blocks.MessagesPlane;
import net.artux.pda.model.chat.UserMessage;

import java.util.EnumMap;

import javax.inject.Inject;

@PerGameMap
public class NotificationController {

    private final ContentGenerator contentGenerator;
    private final MessagesPlane messagesPlane;
    private final AudioSystem audioSystem;
    private final EnumMap<NotificationType, Sound> soundMap;

    private final Label titleLabel;
    private final Timer.Task delayTitleTask = new Timer.Task() {
        @Override
        public void run() {
            titleLabel.setVisible(false);
        }
    };

    @Inject
    public NotificationController(UserInterface userInterface, AssetManager assetManager, AudioSystem audioSystem, MessagesPlane messagesPlane, ContentGenerator contentGenerator) {
        this.contentGenerator = contentGenerator;
        this.audioSystem = audioSystem;
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
        Group gameZone = userInterface.getGameZone();

        titleLabel = new Label("test", userInterface.getLabelStyle());
        titleLabel.setTouchable(Touchable.disabled);
        titleLabel.setFillParent(true);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(0, gameZone.getHeight() / 4);
        titleLabel.setVisible(false);
        gameZone.addActor(titleLabel);
    }

    /**
     * Добавление сообщения на экран без звука
     *
     * @param icon    ссылка на ресурс для изображения
     * @param title   заголовок
     * @param content содержимое
     * @param length  длительность отображения
     */
    public void addSilentMessage(String icon, String title, String content, MessagesPlane.Length length) {
        messagesPlane.addMessage(icon, title, content, length);
    }

    /**
     * Добавление сообщения на экран со звуком подсказки, работает как {@link #addSilentMessage(String, String, String, MessagesPlane.Length)}}
     */
    public void addMessage(String icon, String title, String content, MessagesPlane.Length length) {
        audioSystem.playBySoundId("audio/sounds/pda/pda_tip.ogg");
        addSilentMessage(icon, title, content, length);
    }

    /**
     * Добавление сообщения на экран со звуком
     *
     * @param sound ссылка на звук
     */
    public void addMessage(String sound, String icon, String title, String content, MessagesPlane.Length length) {
        audioSystem.playBySoundId(sound);
        addSilentMessage(icon, title, content, length);
    }

    public void notify(NotificationType type, String title, String content) {
        addSilentMessage(type.getIcon(),
                title,
                content, MessagesPlane.Length.SHORT);
        audioSystem.playSound(soundMap.get(type));
    }

    /**
     * Отображение сообщения со звуком
     *
     * @param message сообщение для показа
     */
    public void addMessage(UserMessage message) {
        addSilentMessage(message.getAuthor().getAvatar(),
                message.getAuthor().getLogin(),
                message.getContent(), MessagesPlane.Length.LONG);
    }

    public void addMessage(String message) {
        addMessage(generateMessage(message));
    }

    public UserMessage generateMessage(String msg) {
        return contentGenerator.generateMessage(msg);
    }

    public UserMessage generateMessage() {
        return contentGenerator.generateMessage();
    }

    /**
     * Устанавливает заголовок по центру экрана на три секунды
     *
     * @param titleText текст заголовка
     */
    public void setTitle(String titleText) {
        titleLabel.setText(titleText);
        titleLabel.setVisible(true);

        if (delayTitleTask.isScheduled())
            delayTitleTask.cancel();

        Timer.schedule(delayTitleTask, 3);
    }

}
