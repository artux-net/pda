package net.artux.pda.map.di.modules;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.MapBodyBuilder;

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
        System.out.println(engine);
        System.out.println(engine.getSystems().toString());
        return engine;
    }

    @Provides
    @PerGameMap
    public World getWorld() {
        return new World(new Vector2(), true);
    }

    @Provides
    @PerGameMap
    public TiledMap getTiledMap(World world) {
        TiledMap tiledMap = new TmxMapLoader().load("maps/kordon.tmx");
        MapBodyBuilder.buildShapes(tiledMap, 1, world);
        return tiledMap;
    }

    @Provides
    @PerGameMap
    public OrthogonalTiledMapRenderer getRenderer(@Named("gameStage") Stage stage, TiledMap tiled) {
        return new OrthogonalTiledMapRenderer(tiled, 1, stage.getBatch());
    }


}
