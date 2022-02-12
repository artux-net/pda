package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.InteractiveComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.UserInterface;
import net.artux.pdalib.Member;

public class LogSystem extends EntitySystem {

    private UserInterface userInterface;

    public LogSystem(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    private ImmutableArray<Entity> players;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<PlayerComponent> pmm = ComponentMapper.getFor(PlayerComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class, HealthComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int j = 0; j < players.size(); j++) {
            PositionComponent playerPosition = pm.get(players.get(j));
            HealthComponent healthComponent = hm.get(players.get(j));

            Logger.LogData.health = healthComponent.value;
            Logger.LogData.position = playerPosition.getPosition();
        }
    }

    public Vector2 getPlayerPosition(){
        for (int j = 0; j < players.size(); j++) {
            PositionComponent playerPosition = pm.get(players.get(j));
            return playerPosition.getPosition();
        }
        return Vector2.Zero;
    }

    public Member getPlayerMember(){
        for (int j = 0; j < players.size(); j++) {
            PlayerComponent playerComponent = pmm.get(players.get(j));
            return playerComponent.member;
        }
        return null;
    }
}
