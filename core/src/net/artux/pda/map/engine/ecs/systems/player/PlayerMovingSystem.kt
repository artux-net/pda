package net.artux.pda.map.engine.ecs.systems.player

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.pathfinding.TiledNode
import net.artux.pda.map.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.engine.ecs.components.BodyComponent
import net.artux.pda.map.engine.ecs.components.HealthComponent
import net.artux.pda.map.engine.ecs.components.PassivityComponent
import net.artux.pda.map.engine.ecs.components.VelocityComponent
import net.artux.pda.map.engine.ecs.systems.BaseSystem
import net.artux.pda.map.engine.ecs.systems.MapOrientationSystem
import org.apache.commons.lang3.tuple.ImmutablePair
import java.util.*
import javax.inject.Inject

@PerGameMap
class PlayerMovingSystem @Inject constructor(
    val assetManager: AssetManager,
    val mapOrientationSystem: MapOrientationSystem,
    val dataRepository: DataRepository
) : BaseSystem(
    Family
        .all(VelocityComponent::class.java, BodyComponent::class.java)
        .exclude(PassivityComponent::class.java).get()
) {

    private val pm = ComponentMapper.getFor(BodyComponent::class.java)
    private val vm = ComponentMapper.getFor(VelocityComponent::class.java)
    private val hm = ComponentMapper.getFor(HealthComponent::class.java)

    private val MOVEMENT = 20f
    private val RUN_MOVEMENT = 30f
    private val PLAYER_MULTIPLICATION = 6f

    private var weightCoefficient = 0f
    private var stepSounds: HashMap<Int, ImmutablePair<Sound, Sound>>
    private var left = false
    private val stepVolume = 0.15f
    private val oneSoundDistance = 5.7f
    private var stepsDistance = 0f
    private var random: Random

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val entity = player
        val position = pm[entity]
        val velocityComponent = vm[entity]
        velocityComponent.velocity = velocityComponent.cpy()

        if (alwaysRun) velocityComponent.isRunning = true
        val stepVector: Vector2
        val currentVelocity = velocityComponent.cpy().scl(weightCoefficient)
        if (speedup) currentVelocity.scl(PLAYER_MULTIPLICATION)
        val healthComponent = hm[entity]
        var staminaDifference = 0f
        if (velocityComponent.isRunning && healthComponent.stamina > 0) {
            stepVector = currentVelocity.scl(deltaTime).scl(RUN_MOVEMENT)
            if (!alwaysRun && entity === player) staminaDifference = -0.1f
        } else {
            if (healthComponent.stamina < 100) staminaDifference = 0.06f
            stepVector = currentVelocity.scl(deltaTime).scl(MOVEMENT)
        }
        healthComponent.stamina += staminaDifference
        if (!stepVector.isZero) {
            position.getBody().applyLinearImpulse(
                stepVector.x * MOVEMENT,
                stepVector.y * MOVEMENT,
                position.x,
                position.y,
                true
            )
        }
        stepsDistance += stepVector.len()
        if (stepsDistance >= oneSoundDistance) {
            stepsDistance = 0f
            var type = mapOrientationSystem.mapBorder.getTileType(position.x, position.y)
            if (!stepSounds.containsKey(type) || random.nextInt(4) == 0) type =
                TiledNode.TILE_EMPTY
            if (left) stepSounds[type]!!.left.play(stepVolume * currentVelocity.len()) else stepSounds[type]!!.right.play(
                stepVolume * currentVelocity.len()
            )
            left = !left
        }

    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}

    companion object {
        var playerWalls = true
        var speedup = false
        var alwaysRun = false
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                val storyDataModel = it
                weightCoefficient = 1.5f - storyDataModel.totalWeight / 60
            }
        }

        weightCoefficient = 1.5f - dataRepository.storyDataModel.totalWeight / 60
        if (weightCoefficient < 0.1f) weightCoefficient = 0.1f
        stepSounds = HashMap()
        random = Random()
        val prefix = "audio/sounds/steps/"
        stepSounds[TiledNode.TILE_EMPTY] =
            ImmutablePair.of(
                assetManager.get(prefix + "empty1.ogg"),
                assetManager.get(prefix + "empty2.ogg")
            )
        stepSounds[TiledNode.TILE_ROAD] = ImmutablePair.of(
            assetManager.get(prefix + "road1.ogg"),
            assetManager.get(prefix + "road2.ogg")
        )
        stepSounds[TiledNode.TILE_GRASS] = ImmutablePair.of(
            assetManager.get(prefix + "grass1.ogg"),
            assetManager.get(prefix + "grass2.ogg")
        )
        stepSounds[TiledNode.TILE_SWAMP] =
            ImmutablePair.of(
                assetManager.get(prefix + "swamp1.ogg"),
                assetManager.get(prefix + "swamp2.ogg")
            )
    }
}