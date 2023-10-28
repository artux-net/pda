package net.artux.pda.map.ecs.physics

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.audio.Sound
import com.badlogic.gdx.math.Vector2
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.engine.pathfinding.TiledNavigator
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.ecs.characteristics.HealthComponent
import net.artux.pda.map.engine.entities.ai.TileType
import net.artux.pda.map.ecs.systems.BaseSystem
import net.artux.pda.map.di.scope.PerGameMap
import org.apache.commons.lang3.tuple.ImmutablePair
import java.util.*
import javax.inject.Inject

@PerGameMap
class PlayerMovingSystem @Inject constructor(
    val assetManager: AssetManager,
    private val tiledNavigator: TiledNavigator,
    val dataRepository: DataRepository
) : BaseSystem(Family.one().get()) {

    private val pm = ComponentMapper.getFor(BodyComponent::class.java)
    private val hm = ComponentMapper.getFor(HealthComponent::class.java)

    private val MOVEMENT_FORCE = 37f // H per step
    private val RUN_MOVEMENT = MOVEMENT_FORCE * 2.3f
    private val PLAYER_MULTIPLICATION = 6f

    private var stepSounds: EnumMap<TileType, ImmutablePair<Sound, Sound>>
    private var left = false
    private val stepVolume = 0.15f
    private val oneStepDistance = 0.36f
    private val oneRunStepDistance = 2.7f
    private var stepsDistance = 0f
    private var random: Random
    private var lastPosition = Vector2()

    val velocity = Vector2()
    private var isRunning: Boolean = false

    fun setRunning(isRunning: Boolean){
        if (velocity.isZero)
            return
        else this.isRunning = isRunning
    }

    private val operationalVelocity: Vector2 = Vector2()

    override fun update(deltaTime: Float) {
        super.update(deltaTime)

        val entity = player
        val position = pm[entity]
        operationalVelocity.set(velocity)
        if (alwaysRun)
            isRunning = true

        val stepVector: Vector2

        if (speedup)
            operationalVelocity.scl(PLAYER_MULTIPLICATION)

        val healthComponent = hm[entity]
        var staminaDifference = 0f
        if (isRunning && healthComponent.stamina > 0) {
            stepVector = operationalVelocity.scl(deltaTime).scl(RUN_MOVEMENT)
            if (!alwaysRun && entity === player)
                staminaDifference = -0.03f
        } else {
            if (healthComponent.stamina < 100)
                staminaDifference = 0.06f
            stepVector = operationalVelocity.scl(deltaTime).scl(MOVEMENT_FORCE)
        }
        healthComponent.stamina(staminaDifference)
        if (!stepVector.isZero) {
            position.getBody().applyLinearImpulse(
                stepVector.x * MOVEMENT_FORCE,
                stepVector.y * MOVEMENT_FORCE,
                position.x,
                position.y,
                true
            )
        }

        stepsDistance += lastPosition.dst2(position.position)
        lastPosition.set(position.position)

        val limit = if (isRunning)
            oneRunStepDistance
        else
            oneStepDistance

        if (stepsDistance >= limit) {
            stepsDistance = 0f
            var type = tiledNavigator.getTileType(position.x, position.y)
            if (!stepSounds.containsKey(type) || random.nextInt(4) == 0)
                type = TileType.EMPTY

            val sound: Sound = if (left)
                stepSounds[type]!!.left
            else
                stepSounds[type]!!.right
            left = !left

            sound.play(stepVolume * velocity.len())
        }

    }

    fun setVelocity(x: Float, y: Float) {
        velocity.set(x, y)
    }

    fun setPosition(vector2: Vector2) {
        pm[player].body.setTransform(vector2, 0f)
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
                if (isPlayerActive)
                    pm[player].body.massData.mass = storyDataModel.totalWeight
            }
        }

        //pm[player].body.massData.mass = dataRepository.storyDataModel.totalWeight

        stepSounds = EnumMap(TileType::class.java)
        random = Random()
        val prefix = "audio/sounds/steps/"
        stepSounds[TileType.EMPTY] =
            ImmutablePair.of(
                assetManager.get(prefix + "empty1.ogg"),
                assetManager.get(prefix + "empty2.ogg")
            )
        stepSounds[TileType.GROUND] =
            ImmutablePair.of(
                assetManager.get(prefix + "empty1.ogg"),
                assetManager.get(prefix + "empty2.ogg")
            )
        stepSounds[TileType.ROAD] = ImmutablePair.of(
            assetManager.get(prefix + "road1.ogg"),
            assetManager.get(prefix + "road2.ogg")
        )
        stepSounds[TileType.GRASS] = ImmutablePair.of(
            assetManager.get(prefix + "grass1.ogg"),
            assetManager.get(prefix + "grass2.ogg")
        )
        stepSounds[TileType.SWAMP] =
            ImmutablePair.of(
                assetManager.get(prefix + "swamp1.ogg"),
                assetManager.get(prefix + "swamp2.ogg")
            )
    }
}