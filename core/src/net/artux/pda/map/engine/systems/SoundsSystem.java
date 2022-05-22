package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SoundComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundsSystem extends BaseSystem implements Disposable {

    private ImmutableArray<Entity> players;

    private List<Music> detections = new ArrayList<>();
    private List<Music> weapons = new ArrayList<>();
    private List<Music> backgrounds = new ArrayList<>();
    private Music anomaly;
    private Random random = new Random();
    private AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);


    public SoundsSystem(AssetManager assetManager) {
        super(Family.all(SoundComponent.class).get());
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        detections.add(assetManager.get("contact_0.ogg", Music.class));
        detections.add(assetManager.get("contact_1.ogg", Music.class));

        weapons.add(assetManager.get("ak74_shoot_0.ogg", Music.class));
        weapons.add(assetManager.get("ak74_shoot_1.ogg", Music.class));
        anomaly = assetManager.get("d-beep.ogg", Music.class);
        for (Music m : weapons) {
            m.setVolume(0.01f);
        }

        backgrounds.add(assetManager.get("music/1.ogg", Music.class));
        backgrounds.add(assetManager.get("music/2.ogg", Music.class));
        backgrounds.add(assetManager.get("music/3.ogg", Music.class));
        for (Music m : backgrounds) {
            m.setVolume(0.51f);
        }
        startBackgroundMusic();
        players = engine.getEntitiesFor(Family.all(PositionComponent.class, PlayerComponent.class).get());
    }

    public void startBackgroundMusic(){
        stopMusic();
        Music m  = backgrounds.get(random.nextInt(backgrounds.size()));
        m.setOnCompletionListener(new Music.OnCompletionListener() {
            @Override
            public void onCompletion(Music music) {
                startBackgroundMusic();
            }
        });
        m.play();
    }

    public void stopMusic(){
        for (Music m : backgrounds){
            m.stop();
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }


    public void playStalkerDetection(){
        boolean playing = false;
        for (Music m : detections) {
            if (!playing)
                playing = m.isPlaying();
        }
        if (!playing)
            detections.get(random.nextInt(detections.size())).play();
    }

    public void playShoot(Vector2 position){
        for (int i = 0; i < players.size(); i++) {
            PositionComponent positionComponent = pm.get(players.get(i));

            for (Music m : weapons) {
                m.setVolume(0.01f + (1/position.dst(positionComponent.getPosition())));
            }
        }

        int i = random.nextInt(weapons.size());
        if (weapons.get(i).isPlaying())
            weapons.get(i).setPosition(0);
        else weapons.get(i).play();
    }

    public void playSound(){

        anomaly.play();
    }

    public void playSoundAtDistance(SoundComponent soundComponent){

    }

    @Override
    public void dispose() {
        for (Music music: detections) {
            music.dispose();
        }
    }


}
