package net.artux.pda.map.di.modules;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import net.artux.pda.map.di.scope.PerGameMap;

import java.util.Set;
import java.util.Timer;

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
    public Timer getTimer() {
        return new Timer();
    }


}
