package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.SoundComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class SoundsSystem extends BaseSystem {

    private final List<Sound> detections = new ArrayList<>();
    private final List<Music> backgrounds = new ArrayList<>();

    private final Random random = new Random();
    private final AssetManager assetManager;
    private static float VOLUME = 1f;

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);

    @Inject
    public SoundsSystem(AssetManager assetManager) {
        super(Family.all(SoundComponent.class).get());
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        detections.add(assetManager.get("audio/sounds/pda/contact_0.ogg", Sound.class));
        detections.add(assetManager.get("audio/sounds/pda/contact_1.ogg", Sound.class));


        backgrounds.add(assetManager.get("audio/music/1.ogg", Music.class));
        backgrounds.add(assetManager.get("audio/music/2.ogg", Music.class));
        backgrounds.add(assetManager.get("audio/music/3.ogg", Music.class));
        for (Music m : backgrounds) {
            m.setVolume(0.71f);
        }
        startBackgroundMusic();
    }

    public void startBackgroundMusic() {
        stopMusic();
        Music m = backgrounds.get(random.nextInt(backgrounds.size()));
        m.setOnCompletionListener(music -> startBackgroundMusic());
        m.play();
    }

    public void stopMusic() {
        for (Music m : backgrounds) {
            m.stop();
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public void changeState(boolean mute) {
        if (!mute)
            VOLUME = 1f;
        else
            VOLUME = 0f;

        for (Music m : backgrounds) {
            m.setVolume(0.71f * VOLUME);
        }
    }

    public boolean isMuted() {
        return VOLUME == 0f;
    }

    public void playStalkerDetection() {
        detections.get(random.nextInt(detections.size())).play(VOLUME);
    }

    public void playSoundAtDistance(Sound sound, Vector2 position) {
        if (sound != null) {
            BodyComponent bodyComponent = pm.get(getPlayer());
            float dst = position.dst(bodyComponent.getPosition());
            float volume = (500 - dst) / 1000f;
            if (volume > 0)
                sound.play(volume * VOLUME);
        }
    }

    public void playSound(Sound sound) {
        if (sound != null) {
            sound.play(VOLUME);
        }
    }



}
