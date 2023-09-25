package net.artux.pda.map.ecs.battle;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;

import net.artux.pda.map.ecs.effects.EffectsSystem;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.characteristics.HealthComponent;
import net.artux.pda.map.ecs.interactive.PassivityComponent;
import net.artux.pda.map.ecs.sound.AudioSystem;
import net.artux.pda.map.ecs.systems.BaseSystem;
import net.artux.pda.map.ecs.vision.VisionComponent;
import net.artux.pda.map.ecs.characteristics.PlayerComponent;
import net.artux.pda.map.di.scope.PerGameMap;

import javax.inject.Inject;

@PerGameMap
public class InfightingSystem extends BaseSystem {

    private final ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private final ComponentMapper<VisionComponent> vm = ComponentMapper.getFor(VisionComponent.class);
    private final ComponentMapper<HealthComponent> hm = ComponentMapper.getFor(HealthComponent.class);
    private final ComponentMapper<MoodComponent> mm = ComponentMapper.getFor(MoodComponent.class);
    private final ComponentMapper<InfightingComponent> wm = ComponentMapper.getFor(InfightingComponent.class);

    private final AudioSystem audioSystem;
    private final EffectsSystem effectsSystem;

    @Inject
    public InfightingSystem(AudioSystem audioSystem, EffectsSystem effectsSystem) {
        super(Family.all(HealthComponent.class, VisionComponent.class,
                MoodComponent.class, BodyComponent.class, InfightingComponent.class).exclude(PlayerComponent.class, PassivityComponent.class).get());
        this.audioSystem = audioSystem;
        this.effectsSystem = effectsSystem;
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
            if (infightingComponent.getAdditionalEffect() != null)
                effectsSystem.addEffect(moodComponent.enemy, infightingComponent.getAdditionalEffect(), infightingComponent.getEffectTime());

            audioSystem.playSoundAtDistance(infightingComponent.getSounds().random(), bodyComponent.getPosition());
        }
    }

}
