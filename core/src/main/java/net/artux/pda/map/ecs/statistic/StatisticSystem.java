package net.artux.pda.map.ecs.statistic;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import net.artux.engine.pathfinding.FlatTiledNode;
import net.artux.engine.pathfinding.TiledSmoothableGraphPath;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.ecs.ai.GraphMotionComponent;
import net.artux.pda.map.ecs.ai.MapOrientationSystem;
import net.artux.pda.map.ecs.render.Drawable;
import net.artux.pda.map.ecs.systems.BaseSystem;

import javax.inject.Inject;
import javax.inject.Named;

@PerGameMap
public class StatisticSystem extends BaseSystem implements Disposable {

    private final ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    float playedTime;

    @Inject
    public StatisticSystem() {
        super(Family.all().get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        playedTime = 0f;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        playedTime += deltaTime;
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    @Override
    public void dispose() {

    }

}
