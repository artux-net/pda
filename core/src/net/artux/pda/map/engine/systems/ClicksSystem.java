package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;

public class ClicksSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<ClickComponent> cm = ComponentMapper.getFor(ClickComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(ClickComponent.class, PositionComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

    }

    public boolean clicked(float x, float y){
        for (int i = 0; i < entities.size(); i++) {
            ClickComponent clickComponent = cm.get(entities.get(i));
            PositionComponent positionComponent = pm.get(entities.get(i));
            SpriteComponent spriteComponent = sm.get(entities.get(i));

            if (positionComponent.getPosition().epsilonEquals(x, y, spriteComponent.sprite.getHeight() / 2)) {
                clickComponent.clickListener.clicked();
                return true;
            }
        }
        return false;
    }
}
