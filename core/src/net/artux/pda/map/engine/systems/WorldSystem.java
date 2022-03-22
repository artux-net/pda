package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.components.AnomalyComponent;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.ui.UserInterface;

public class WorldSystem extends EntitySystem {

    private ImmutableArray<Entity> anomalies;
    private ImmutableArray<Entity> entities;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<AnomalyComponent> am = ComponentMapper.getFor(AnomalyComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pcm = ComponentMapper.getFor(PlayerComponent.class);

    private CameraSystem cameraSystem;

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        anomalies = engine.getEntitiesFor(Family.all(AnomalyComponent.class, PositionComponent.class).get());
        entities = engine.getEntitiesFor(Family.all(HealthComponent.class, PositionComponent.class, VelocityComponent.class).get());
        cameraSystem = engine.getSystem(CameraSystem.class);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        boolean player = false;
        for (int i = 0; i < entities.size(); i++) {
            PositionComponent positionComponent = pm.get(entities.get(i));
            VelocityComponent velocityComponent = vm.get(entities.get(i));
            HealthComponent healthComponent = hcm.get(entities.get(i));

            for (int j = 0; j < anomalies.size(); j++) {
                PositionComponent positionComponent1 = pm.get(anomalies.get(j));
                AnomalyComponent anomalyComponent = am.get(anomalies.get(j));
                if (positionComponent1.getPosition().dst(positionComponent.getPosition()) < anomalyComponent.size) {
                    if (velocityComponent.velocity.len() > anomalyComponent.maxVelocity)
                        healthComponent.damage(anomalyComponent.damage);
                    if (!player)
                        player = pcm.has(entities.get(i));
                }
            }
        }
        cameraSystem.setSpecialZoom(player);
    }

    private void removeActor(Array<Actor> actors, String actorName) {
        for (Actor actor : actors) {
            if (actor.getName() != null && actor.getName().equals(actorName)) {
                actor.remove();
            }
        }
    }

}
