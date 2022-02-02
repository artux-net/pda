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
import net.artux.pda.map.engine.components.player.PlayerComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SoundsSystem extends EntitySystem implements Disposable {

    private ImmutableArray<Entity> players;

    private List<Music> detections = new ArrayList<>();
    private List<Music> weapons = new ArrayList<>();
    private Random random = new Random();
    private AssetManager assetManager;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);


    public SoundsSystem(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        detections.add(assetManager.get("contact_0.ogg", Music.class));
        detections.add(assetManager.get("contact_1.ogg", Music.class));

        weapons.add(assetManager.get("ak74_shoot_0.ogg", Music.class));
        weapons.add(assetManager.get("ak74_shoot_1.ogg", Music.class));
        for (Music m : weapons) {
            m.setVolume(0.01f);
        }

        players = engine.getEntitiesFor(Family.all(PositionComponent.class, PlayerComponent.class).get());
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

    @Override
    public void dispose() {
        for (Music music: detections) {
            music.dispose();
        }
    }
}
