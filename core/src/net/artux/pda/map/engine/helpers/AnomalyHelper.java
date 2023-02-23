package net.artux.pda.map.engine.helpers;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.engine.pathfinding.TiledNode.TILE_WALL;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.data.GlobalData;
import net.artux.pda.map.engine.ecs.components.AnomalyComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;

import java.util.Calendar;
import java.util.Random;

public class AnomalyHelper {

    private static final String[] names = {"Жарка", "Электра", "Трамплин", "Туман", "Огниво"};

    public static void createAnomalies(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        AssetManager assetManager = coreComponent.getAssetsManager();
        Random random = new Random(Calendar.getInstance().get(Calendar.MINUTE) / 5);
        MapOrientationSystem mapOrientationSystem = engine.getSystem(MapOrientationSystem.class);

        for (int i = 0; i < random(5, 9); i++) {
            Vector2 position = new Vector2(random.nextInt(GlobalData.mapWidth), random.nextInt(GlobalData.mapHeight));
            if (mapOrientationSystem.isGraphActive())
                while (mapOrientationSystem.getWorldGraph().getNodeInPosition(position.x, position.y).type == TILE_WALL)
                    position = new Vector2(random.nextInt(GlobalData.mapWidth), random.nextInt(GlobalData.mapHeight));

            Entity anomaly = new Entity();

            int size = random(20, 30);
            final AnomalyComponent anomalyComponent = new AnomalyComponent(names[random(0, names.length - 1)], size, 0.3f, 30);
            size *= 2;

            final Vector2 finalPosition = position;
            anomaly.add(new BodyComponent(finalPosition, coreComponent.getWorld()))
                    .add(new SpriteComponent(assetManager.get("controlPoint.png", Texture.class), size, size))
                    .add(anomalyComponent)
                    .add(new ClickComponent(size / 2, new ClickComponent.ClickListener() {
                        @Override
                        public void clicked() {
                            engine.getSystem(RenderSystem.class)
                                    .showText(anomalyComponent.desc(), finalPosition);
                        }
                    }));
            engine.addEntity(anomaly);
        }

    }

}
