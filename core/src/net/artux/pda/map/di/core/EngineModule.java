package net.artux.pda.map.di.core;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;

import java.util.Set;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = EngineSystemsModule.class)
public class EngineModule {

    @Singleton
    @Provides
    public Engine getEngine(Set<EntitySystem> systems) {
        Engine engine = new Engine();
        for (EntitySystem system : systems)
            engine.addSystem(system);
        System.out.println(engine);
        System.out.println(engine.getSystems().toString());
        return engine;
    }

}
