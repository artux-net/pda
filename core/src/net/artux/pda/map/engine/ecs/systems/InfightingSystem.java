package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.InfightingComponent;
import net.artux.pda.map.engine.ecs.components.MoodComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class InfightingSystem extends BaseSystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<InfightingComponent> wm = ComponentMapper.getFor(InfightingComponent.class);

    @Inject
    public InfightingSystem() {
        super(Family.all(HealthComponent.class, VisionComponent.class,
                MoodComponent.class, BodyComponent.class, InfightingComponent.class).exclude(PlayerComponent.class, PassivityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        InfightingComponent infightingComponent = wm.get(entity);
        BodyComponent bodyComponent = pm.get(entity);
        MoodComponent moodComponent = mm.get(entity);
        VisionComponent visionComponent = vm.get(entity);
        infightingComponent.update(deltaTime);

        if (moodComponent.hasEnemy() && visionComponent.isSeeing(moodComponent.getEnemy())) {
            BodyComponent enemyBody = pm.get(moodComponent.getEnemy());
            float dst = bodyComponent.getPosition().dst(enemyBody.getPosition());
            if (dst > infightingComponent.getDistance() || !infightingComponent.canDamageSomebody())
                return;
            HealthComponent enemyHealth = hm.get(moodComponent.getEnemy());
            enemyHealth.damage(infightingComponent.getDamage());
            //todo make sound
        }
    }

}
