package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SoundComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

@PerGameMap
public class SoundsSystem extends BaseSystem {

    private final List<Sound> detections = new ArrayList<>();
    private final List<Sound> weapons = new ArrayList<>();
    private final List<Music> backgrounds = new ArrayList<>();
    private Sound anomaly;
    private final Random random = new Random();
    private final AssetManager assetManager;
    private static float VOLUME = 1f;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Inject
    public SoundsSystem(AssetManager assetManager) {
        super(Family.all(SoundComponent.class).get());
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        detections.add(assetManager.get("audio/sounds/contact_0.ogg", Sound.class));
        detections.add(assetManager.get("audio/sounds/contact_1.ogg", Sound.class));

        weapons.add(assetManager.get("audio/sounds/ak74_shoot_0.ogg", Sound.class));
        weapons.add(assetManager.get("audio/sounds/ak74_shoot_1.ogg", Sound.class));
        anomaly = assetManager.get("audio/sounds/d-beep.ogg", Sound.class);

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
        m.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                startBackgroundMusic();
            }
        });
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

    public void playShoot(Vector2 position) {
        PositionComponent positionComponent = pm.get(getPlayer());
        float dst = position.dst(positionComponent.getPosition());
        float volume = (500 - dst) / 500;

        int i = random.nextInt(weapons.size());
        weapons.get(i).play(volume * VOLUME);
    }

    public void playSound() {
        anomaly.play(VOLUME);
    }

    public void playSoundAtDistance(SoundComponent soundComponent) {

    }

}
