package net.artux.pda.map.content;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.entities.ai.TileType.ANOMALY;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ecs.anomaly.AnomalyComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.ecs.sound.MusicComponent;
import net.artux.pda.map.engine.entities.ai.TileType;
import net.artux.pda.map.engine.entities.model.Anomaly;
import net.artux.pda.map.di.components.MapComponent;

import java.util.HashSet;
import java.util.Set;

public class AnomalyHelper {

    public static void createAnomalies(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        TiledMap tiledMap = coreComponent.getTiledMap();
        AssetManager assetManager = coreComponent.getAssetsManager();
        String prefix = "audio/music/anomalies/";

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

            int size = random(20, 30);
            final Vector2 finalPosition = position;

            Entity anomalyEntity = new Entity()
                    .add(new BodyComponent(finalPosition, coreComponent.getWorld()))
                    .add(new AnomalyComponent(Anomaly.values()[random(0, Anomaly.values().length - 1)], size));

            String name = anomalyEntity.getComponent(AnomalyComponent.class)
                    .getAnomaly()
                    .name()
                    .toLowerCase();

            anomalyEntity.add(new MusicComponent(assetManager.get(prefix + name + "/idle.ogg"), true));

            engine.addEntity(anomalyEntity);
        }

    }

}
