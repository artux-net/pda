package net.artux.pda.map.repository

import com.badlogic.ashley.core.Engine
import com.google.gson.Gson
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.model.map.GameMap
import javax.inject.Inject

@PerGameMap
class EngineSaver @Inject constructor(
    val gameMap: GameMap,
    val platformInterface: PlatformInterface,
    var spawnController: SpawnController,
    val gson: Gson
) {

    fun save(engine: Engine) {
        spawnController.save(engine)
    }

    fun restore() {
        spawnController.restore()
    }


}