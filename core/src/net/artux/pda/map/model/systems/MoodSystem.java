package net.artux.pda.map.model.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.model.components.PositionComponent;
import net.artux.pda.map.model.components.MoodComponent;
import net.artux.pda.map.model.components.StatesComponent;
import net.artux.pda.map.model.components.TargetMovingComponent;
import net.artux.pda.map.model.components.WeaponComponent;

public class MoodSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;

    private ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        entities = engine.getEntitiesFor(Family.all(MoodComponent.class, PositionComponent.class).get());

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < entities.size()-1; i++) {
            Entity entity1 = entities.get(i);

            MoodComponent moodComponent1 = mm.get(entity1);

            for (int j = i+1; j < entities.size(); j++) {
                Entity entity2 = entities.get(j);

                float dst = pm.get(entity1).getPosition().dst(pm.get(entity2).getPosition());
                MoodComponent moodComponent2 = mm.get(entity2);

                if (dst < 60){
                    if (moodComponent1.enemy==null && moodComponent1.isEnemy(moodComponent2) ){
                        moodComponent1.setEnemy(entity2);
                    }
                }

                if(moodComponent1.enemy == entity2)
                    moodComponent2.setEnemy(entity1);

            }

            if (moodComponent1.enemy != null && pm.get(entity1).getPosition().dst(pm.get(moodComponent1.enemy).getPosition()) > 100)
                moodComponent1.setEnemy(null);
        }
    }
}
