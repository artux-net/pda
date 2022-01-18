package net.artux.pda.map.model.systems;

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

import net.artux.pda.map.model.components.HealthComponent;
import net.artux.pda.map.model.components.InteractiveComponent;
import net.artux.pda.map.model.components.PlayerComponent;
import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.states.GameStateManager;
import net.artux.pda.map.ui.Logger;
import net.artux.pda.map.ui.UserInterface;

public class LogSystem extends EntitySystem {

    private UserInterface userInterface;

    public LogSystem(UserInterface userInterface) {
        this.userInterface = userInterface;
    }

    private ImmutableArray<Entity> players;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private ComponentMapper<InteractiveComponent> im = ComponentMapper.getFor(InteractiveComponent.class);

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
            Logger.LogData.posX = playerPosition.getX();
            Logger.LogData.posY = playerPosition.getY();
        }
    }
}
