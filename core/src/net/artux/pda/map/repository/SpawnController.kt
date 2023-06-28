package net.artux.pda.map.repository

import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import com.google.gson.Gson
import net.artux.pda.map.ecs.ai.StalkerComponent
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.ecs.creation.EntityProcessorSystem
import net.artux.pda.map.ecs.interactive.map.SpawnComponent
import net.artux.pda.map.ecs.takeover.SpawnSystem
import net.artux.pda.map.utils.PlatformInterface
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.model.map.GameMap
import net.artux.pda.model.map.SpawnModel
import net.artux.pda.model.map.Strength
import javax.inject.Inject

@PerGameMap
class SpawnController @Inject constructor(
    val gameMap: GameMap,
    val gson: Gson,
    val logger: ApplicationLogger,
    val entityProcessorSystem: EntityProcessorSystem,
    val platformInterface: PlatformInterface
) {
    val tag = "SpawnController"
    val preferences = Gdx.app.getPreferences("spawns")

    fun save(engine: Engine) {
        val spawns = mutableListOf<SavedSpawn>()
        val entities = engine.getSystem(SpawnSystem::class.java).spawns
        for (i in 0 until entities.size()) {
            val entity = entities[i]
            val spawnComponent = entity.getComponent(SpawnComponent::class.java)
            var stalkers = mutableListOf<Entity>()
            val savedStalkers = mutableListOf<SavedStalker>()
            var strength = Strength.WEAK
            var gang = spawnComponent.spawnModel.group
            if (!spawnComponent.isEmpty) {
                gang = spawnComponent.stalkerGroup.gang
                stalkers = spawnComponent.stalkerGroup.entities.toMutableList()
                strength = spawnComponent.stalkerGroup.strength
            }

            for (stalker in stalkers) {
                val stalkerComponent = stalker.getComponent(StalkerComponent::class.java)
                val health = stalker.getComponent(HealthComponent::class.java).health
                savedStalkers.add(
                    SavedStalker(
                        stalkerComponent.name,
                        stalkerComponent.avatar,
                        health
                    )
                )
            }
            if (gang != null && !spawnComponent.title.isNullOrBlank()) {
                spawns.add(SavedSpawn(spawnComponent.title, gang, strength, savedStalkers))
                logger.log(
                    tag,
                    "${spawnComponent.title}: Saved $gang with ${savedStalkers.size} stalkers"
                )
            }
        }
        val savedGame = SavedMap(gameMap.id, spawns)
        preferences.putString(savedGame.id.toString(), gson.toJson(savedGame))
        preferences.flush()
    }

    fun restore(engine: Engine) {
        val savedKey = gameMap.id.toString()

        val savedMap = if (preferences.contains(savedKey))
            gson.fromJson(preferences.getString(savedKey), SavedMap::class.java)
        else
            null

        val spawns: List<SpawnModel> = gameMap.spawns ?: return

        logger.log(tag, "Start to restore ${spawns.size} spawns")
        for (i in 0..spawns.size - 1) {
            val spawnModel = spawns[i]
            logger.log(tag, "Start to restore spawns")
            val withSprite = !spawnModel.getParams().contains("hide")
            var groupCreated = false

            val spawnEntity = entityProcessorSystem.generateNewSpawn(spawnModel, withSprite)
            val spawnComponent = spawnEntity.getComponent(SpawnComponent::class.java)
            if (savedMap?.spawns != null
                && !spawnModel.title.isNullOrBlank()) {
                val savedSpawn = savedMap.spawns.find { it.title == spawnModel.title }
                if (savedSpawn != null) {
                    val stalkerGroup = entityProcessorSystem.restoreGroup(spawnComponent.position, savedSpawn)
                    spawnComponent.stalkerGroup = stalkerGroup
                    groupCreated = true
                    logger.log(tag, "${spawnModel.title}: Restored group ${stalkerGroup.gang} with ${stalkerGroup.size()} stalkers"
                    )
                }
            }
            if (!groupCreated && spawnModel.group != null) {
                val stalkerGroup =
                    entityProcessorSystem.generateNewGroup(spawnComponent.position, spawnModel)
                spawnComponent.stalkerGroup = stalkerGroup
                logger.log(tag,
                    "${spawnModel.title}: Created new group ${stalkerGroup.gang} with ${stalkerGroup.size()} stalkers"
                )
            }

        }
    }

}