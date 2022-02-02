package net.artux.pda.map.engine.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;

public class RenderSystem extends EntitySystem{

    private Array<Entity> entities;
    private Batch batch;

    private ComponentMapper<SpriteComponent> sm = ComponentMapper.getFor(SpriteComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    public RenderSystem(Batch batch) {
        this.batch = batch;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = new Array<>(engine.getEntitiesFor(Family.all(SpriteComponent.class, PositionComponent.class).get()).toArray());

        engine.addEntityListener(Family.all(SpriteComponent.class, PositionComponent.class).get(), new EntityListener() {
            @Override
            public void entityAdded(Entity entity) {
                entities.add(entity);
            }

            @Override
            public void entityRemoved(Entity entity) {
                entities.removeValue(entity, true);
            }
        });

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size; i++) {
            Entity entity = entities.get(i);

            SpriteComponent spriteComponent = sm.get(entity);
            PositionComponent positionComponent = pm.get(entity);

            Sprite sprite = spriteComponent.sprite;
            batch.draw(sprite, positionComponent.getX()-sprite.getOriginX(), positionComponent.getY()-sprite.getOriginY(), sprite.getOriginX(),
                    sprite.getOriginY(), sprite.getWidth(), sprite.getHeight(), sprite.getScaleX(), sprite.getScaleY(), spriteComponent.getRotation());
        }
    }

}
