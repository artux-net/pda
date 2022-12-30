package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.ai.pfa.DefaultConnection;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.PathSmoother;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.GraphMotionComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.pathfinding.FlatTiledNode;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.StoryModel;

import java.util.Collection;
import java.util.Map;

public class PlayerTargetingSystem extends BaseSystem {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<VelocityComponent> vm = ComponentMapper.getFor(VelocityComponent.class);
    private ComponentMapper<GraphMotionComponent> gmm = ComponentMapper.getFor(GraphMotionComponent.class);

    DefaultGraphPath<DefaultConnection<GameMap>> path;
    IndexedAStarPathFinder<GameMap> pathFinder;
    PathSmoother<FlatTiledNode, Vector2> pathSmoother;
    MapOrientationSystem mapOrientationSystem;

    public PlayerTargetingSystem() {
        super(Family.all(VelocityComponent.class, PositionComponent.class, GraphMotionComponent.class).get());
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);

        mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

        /*heuristic = mapOrientationSystem.heuristic;
        pathFinder = mapOrientationSystem.pathFinder;
        pathSmoother = mapOrientationSystem.pathSmoother;*/
    }

    void buildSystemFromStory(StoryModel storyModel) {
        Collection<GameMap> maps = storyModel.getMaps().values();
        for (GameMap map : maps){
            for (Point point : map.getPoints()){
                if (point.getType() == 7){
                    String chapterString = point.getData().get("chapter");
                    String stageString = point.getData().get("stage");
                    if (chapterString != null && stageString != null){
                        int stage = Integer.parseInt(stageString);
                        Map<String, String> data = storyModel
                                .getChapter(chapterString)
                                .getStage(stage).getData();
                        String targetMap = data.get("map");

                    }
                }//point.getType()
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (int i = 0; i < getEntities().size(); i++) {
            Entity entity = getEntities().get(i);
        }
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }
}
