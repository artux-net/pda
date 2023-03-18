package net.artux.pda.map.engine.ecs.systems;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.PassivityComponent;
import net.artux.pda.map.engine.ecs.components.VisionComponent;
import net.artux.pda.map.engine.ecs.components.effects.Effect;
import net.artux.pda.map.engine.ecs.components.effects.Effects;

import javax.inject.Inject;

@PerGameMap
public class EffectsSystem extends BaseSystem {

    private ComponentMapper<Effects> ecm = ComponentMapper.getFor(Effects.class);

    @Inject
    public EffectsSystem() {
        super(Family.all(Effects.class, BodyComponent.class).exclude(PassivityComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        ecm.get(entity).getEffects().forEach(effect -> effect.affect(deltaTime, entity));
    }

    public void addEffect(Entity entity, Effect effect, int seconds) {
        if (!ecm.has(entity))
            return;

        ecm.get(entity).add(effect);
        effect.begin(entity);
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                effect.end(entity);
                ecm.get(entity).remove(effect);
            }
        }, seconds);
    }

}