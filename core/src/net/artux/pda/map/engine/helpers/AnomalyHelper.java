package net.artux.pda.map.engine.helpers;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.ai.TileType.ANOMALY;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ai.TileType;
import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.engine.ecs.components.AnomalyComponent;
import net.artux.pda.map.engine.ecs.components.BodyComponent;
import net.artux.pda.map.engine.ecs.components.ClickComponent;
import net.artux.pda.map.engine.ecs.components.HealthComponent;
import net.artux.pda.map.engine.ecs.components.SpriteComponent;
import net.artux.pda.map.engine.ecs.components.effects.Effects;
import net.artux.pda.map.engine.ecs.systems.EffectsSystem;
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem;
import net.artux.pda.map.engine.ecs.systems.RenderSystem;
import net.artux.pda.map.model.Anomaly;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class AnomalyHelper {

    private static final String[] names = {"Жарка", "Электра", "Трамплин", "Туман", "Огниво"};

    private static ComponentMapper<BodyComponent> pm = ComponentMapper.getFor(BodyComponent.class);
    private static ComponentMapper<HealthComponent> hcm = ComponentMapper.getFor(HealthComponent.class);
    private static ComponentMapper<Effects> ecm = ComponentMapper.getFor(Effects.class);

    public static void createAnomalies(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        AssetManager assetManager = coreComponent.getAssetsManager();
        TiledMap tiledMap = coreComponent.getTiledMap();

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("tiles");
        Set<Vector2> anomalyPotentialPositions = new HashSet<>();

        int tileWidth = layer.getTileWidth();
        int tileHeight = layer.getTileHeight();

        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (TileType.get(cell.getTile().getId()) == ANOMALY) {
                        anomalyPotentialPositions
                                .add(new Vector2((float) (tileWidth * x + tileWidth / 2),
                                        (float) (tileHeight * y + tileHeight / 2)));
                    }

                }
            }
        }

        Array<Vector2> anomaliesArr = new Array<>(anomalyPotentialPositions.size());
        for (Vector2 c : anomalyPotentialPositions) {
            anomaliesArr.add(c);
        }

        for (int i = 0; i < random(5, 9); i++) {
            if (anomaliesArr.size < 1)
                break;
            int randomIndex = random(anomaliesArr.size - 1);
            Vector2 position = anomaliesArr.get(randomIndex);
            anomaliesArr.removeIndex(randomIndex);

            Entity anomaly = new Entity();

            int size = random(20, 30);
            final AnomalyComponent anomalyComponent = new AnomalyComponent(Anomaly.values()[random(0, Anomaly.values().length - 1)], size);
            size *= 2;

            final Vector2 finalPosition = position;
            anomaly.add(new BodyComponent(finalPosition, coreComponent.getWorld()))

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
