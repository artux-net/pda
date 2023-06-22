package net.artux.pda.map.utils.di.modules;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.engine.utils.MapBodyBuilder;
import net.artux.pda.map.utils.MapInfo;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.map.GameMap;

import java.util.Set;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

@Module(includes = EngineSystemsModule.class)
public class EngineModule {

    @PerGameMap
    @Provides
    public Engine getEngine(Set<EntitySystem> systems) {
        Engine engine = new Engine();

        for (EntitySystem system : systems)
            engine.addSystem(system);
        return engine;
    }

    @Provides
    @PerGameMap
    public Preferences getPreferences() {
        return Gdx.app.getPreferences("quest-prefs");
    }

    @Provides
    @PerGameMap
    public World getWorld() {
        return new World(new Vector2(), true);
    }

    @Provides
    @PerGameMap
    public TiledMap getTiledMap(GameMap gameMap, World world) {
        TiledMap tiledMap = new TmxMapLoader().load("maps/" + gameMap.getTmx());
        MapBodyBuilder.buildShapes(tiledMap, 1, world);
        return tiledMap;
    }

    @Provides
    @PerGameMap
    public MapInfo getMapInfo(TiledMap map) {
        TiledMapTileLayer tileLayer = (TiledMapTileLayer) map.getLayers().get("tiles");

        int mapWidth = tileLayer.getWidth() * tileLayer.getTileWidth();
        int mapHeight = tileLayer.getHeight() * tileLayer.getTileHeight();
        return new MapInfo(mapWidth, mapHeight);
    }

    @Provides
    @PerGameMap
    public OrthogonalTiledMapRenderer getRenderer(@Named("gameStage") Stage stage, TiledMap tiled) {
        return new OrthogonalTiledMapRenderer(tiled, 1, stage.getBatch());
    }


}
