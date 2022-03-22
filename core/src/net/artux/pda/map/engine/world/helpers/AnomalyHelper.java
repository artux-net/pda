package net.artux.pda.map.engine.world.helpers;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.pathfinding.TiledNode.TILE_WALL;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.AnomalyComponent;
import net.artux.pda.map.engine.components.ClickComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.RenderSystem;

public class AnomalyHelper {

    private static final String[] names = {"Жарка", "Электра", "Трамплин", "Туман", "Огниво"};

    public static void createAnomalies(final Engine engine, AssetManager assetManager) {
        MapOrientationSystem mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

        for (int i = 0; i < random(5, 9); i++) {
            Vector2 position = new Vector2(random(GlobalData.mapWidth), random(GlobalData.mapHeight));
            if (mapOrientationSystem.isGraphActive())
                while (mapOrientationSystem.getWorldGraph().getNodeInPosition(position.x, position.y).type == TILE_WALL)
                    position = new Vector2(random(GlobalData.mapWidth), random(GlobalData.mapHeight));

            Entity anomaly = new Entity();

            int size = random(20, 30);
            final AnomalyComponent anomalyComponent = new AnomalyComponent(names[random(0,names.length-1)], size,0.3f, 30);
            size*=2;

            final Vector2 finalPosition = position;
            anomaly.add(new PositionComponent(finalPosition))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size))
                    .add(anomalyComponent)
                    .add(new ClickComponent(new ClickComponent.ClickListener() {
                        @Override
                        public void clicked() {
                            engine.getSystem(RenderSystem.class)
                                    .showText(anomalyComponent.desc(),finalPosition);
                        }
                    }));
            engine.addEntity(anomaly);
        }

    }

}
