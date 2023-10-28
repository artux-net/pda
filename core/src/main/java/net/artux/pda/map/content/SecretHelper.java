package net.artux.pda.map.content;

import static com.badlogic.gdx.math.MathUtils.random;
import static net.artux.pda.map.engine.entities.ai.TileType.SEARCH;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ecs.interactive.map.SecretComponent;
import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.map.engine.entities.ai.TileType;
import net.artux.pda.map.di.components.MapComponent;

import java.util.HashSet;
import java.util.Set;

public class SecretHelper {

    public static void createAnomalies(MapComponent coreComponent) {
        Engine engine = coreComponent.getEngine();
        TiledMap tiledMap = coreComponent.getTiledMap();

        TiledMapTileLayer layer = (TiledMapTileLayer) tiledMap.getLayers().get("tiles");
        Set<Vector2> searchPotential = new HashSet<>();

        int tileWidth = layer.getTileWidth();
        int tileHeight = layer.getTileHeight();

        for (int y = 0; y < layer.getHeight(); y++) {
            for (int x = 0; x < layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (TileType.get(cell.getTile().getId()) == SEARCH) {
                        searchPotential
                                .add(new Vector2((float) (tileWidth * x + tileWidth / 2),
                                        (float) (tileHeight * y + tileHeight / 2)));
                    }

                }
            }
        }

        Array<Vector2> secretArr = new Array<>(searchPotential.size());
        for (Vector2 c : searchPotential) {
            secretArr.add(c);
        }

        for (int i = 0; i < random(1, 2); i++) {
            if (secretArr.size < 1)
                break;
            int randomIndex = random(secretArr.size - 1);
            Vector2 position = secretArr.get(randomIndex);
            secretArr.removeIndex(randomIndex);
            Entity secret = new Entity()
                    .add(new BodyComponent(position, coreComponent.getWorld()))
                    .add(new SecretComponent());
            engine.addEntity(secret);
        }

    }

}
