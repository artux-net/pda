package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;

import javax.inject.Inject;

@PerGameMap
public class ClicksSystem extends IteratingSystem {

    private ComponentMapper<ClickComponent> cm = ComponentMapper.getFor(ClickComponent.class);
    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);

    @Inject
    public ClicksSystem() {
        super(Family.all(ClickComponent.class, Position.class).exclude(PassivityComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public boolean clicked(float x, float y) {
        for (int i = 0; i < getEntities().size(); i++) {
            ClickComponent clickComponent = cm.get(getEntities().get(i));
            Position position = pm.get(getEntities().get(i));

            if (position.getPosition().epsilonEquals(x, y, clickComponent.clickRadius)) {
                clickComponent.clickListener.clicked();
                return true;
            }
        }
        return false;
    }
}
