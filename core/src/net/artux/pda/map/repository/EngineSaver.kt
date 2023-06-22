package net.artux.pda.map.repository

import com.badlogic.ashley.core.Engine
import com.badlogic.gdx.Preferences
import net.artux.pda.map.engine.ecs.systems.SpawnSystem
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.utils.di.scope.PerGameMap
import javax.inject.Inject

@PerGameMap
class EngineSaver @Inject constructor(
    val platformInterface: PlatformInterface,
    var entitySaver: EntitySaver,
    val preferences: Preferences
) {

    fun save(engine: Engine) {
        val entities = engine.getSystem(SpawnSystem::class.java).entities
        for (i in 0 until entities.size()) {
            val entity = entities[i]

        }
    }

    fun restore(engine: Engine){

    }


}