package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
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

    private final List<Music> detections = new ArrayList<>();
    private final List<Music> weapons = new ArrayList<>();
    private final List<Music> backgrounds = new ArrayList<>();
    private Music anomaly;
    private final Random random = new Random();
    private final AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Inject
    public SoundsSystem(AssetManager assetManager) {
        super(Family.all(SoundComponent.class).get());
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        detections.add(assetManager.get("sounds/contact_0.ogg", Music.class));
        detections.add(assetManager.get("sounds/contact_1.ogg", Music.class));

        weapons.add(assetManager.get("sounds/ak74_shoot_0.ogg", Music.class));
        weapons.add(assetManager.get("sounds/ak74_shoot_1.ogg", Music.class));
        anomaly = assetManager.get("sounds/d-beep.ogg", Music.class);
        for (Music m : weapons) {
            m.setVolume(0.01f);
        }

        backgrounds.add(assetManager.get("sounds/music/1.ogg", Music.class));
        backgrounds.add(assetManager.get("sounds/music/2.ogg", Music.class));
        backgrounds.add(assetManager.get("sounds/music/3.ogg", Music.class));
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


    public void playStalkerDetection() {
        boolean playing = false;
        for (Music m : detections) {
            if (!playing)
                playing = m.isPlaying();
        }
        if (!playing)
            detections.get(random.nextInt(detections.size())).play();
    }

    public void playShoot(Vector2 position) {
        PositionComponent positionComponent = pm.get(getPlayer());
        float dst = position.dst(positionComponent.getPosition());
        float volume = (500 - dst) / 500;
        if (volume > 0)
            for (Music m : weapons) {
                m.setVolume(volume);
            }

        int i = random.nextInt(weapons.size());
        if (weapons.get(i).isPlaying())
            weapons.get(i).setPosition(0);
        else weapons.get(i).play();
    }

    public void playSound() {
        anomaly.play();
    }

    public void playSoundAtDistance(SoundComponent soundComponent) {

    }

}
