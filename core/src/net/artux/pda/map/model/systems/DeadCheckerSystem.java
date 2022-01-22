package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.components.HealthComponent;
import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.model.components.VelocityComponent;
import net.artux.pda.map.model.components.player.PlayerComponent;

public class DeadCheckerSystem extends EntitySystem {

    private ImmutableArray<Entity> players;

    private ComponentMapper<PlayerComponent> cm = ComponentMapper.getFor(PlayerComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, HealthComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < players.size(); i++) {
            Entity entity = players.get(i);

            HealthComponent healthComponent = hm.get(entity);

            if (healthComponent.isDead()){

            }
        }
    }

}
