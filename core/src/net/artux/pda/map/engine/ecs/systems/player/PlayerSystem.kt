package net.artux.pda.map.engine.ecs.systems.player

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.engine.ecs.components.*
import net.artux.pda.map.engine.ecs.components.player.PlayerComponent
import net.artux.pda.map.engine.ecs.systems.BaseSystem
import net.artux.pda.map.view.UserInterface
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject

@PerGameMap
class PlayerSystem @Inject constructor(
    val dataRepository: DataRepository,
    val userInterface: UserInterface
) :
    BaseSystem(Family.one().get()), Disposable {
    private val pm = ComponentMapper.getFor(
        Position::class.java
    )
    private val mm = ComponentMapper.getFor(
        MoodComponent::class.java
    )
    private val sm = ComponentMapper.getFor(
        SpriteComponent::class.java
    )
    private val hm = ComponentMapper.getFor(
        HealthComponent::class.java
    )
    private val pmm = ComponentMapper.getFor(
        PlayerComponent::class.java
    )
    private val vcm = ComponentMapper.getFor(
        VelocityComponent::class.java
    )
    lateinit var lastDataModel: StoryDataModel

    init {
        CoroutineScope(Dispatchers.Main).launch {
          dataRepository.storyDataModelFlow.collect{
              lastDataModel = it
          }
        }
    }

    override fun removedFromEngine(engine: Engine) {
        super.removedFromEngine(engine)
        userInterface.gameZone.isVisible = false
    }

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        loadPreferences()
    }

    public override fun getPlayer(): Entity? {
        return super.getPlayer()
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        if (isPlayerActive) {
            val moodComponent = mm[player]
            val playerPosition = pm[player]
            val spriteComponent = sm[player]
            val enemy = moodComponent.enemy
            val direction: Vector2
            direction = if (enemy == null) {
                vcm[player].velocity
            } else {
                val enemyPosition = pm[enemy]
                enemyPosition.position.cpy().sub(playerPosition.position)
            }
            val degrees = (Math.atan2(
                -direction.x.toDouble(),
                direction.y
                    .toDouble()
            ) * 180.0 / Math.PI).toFloat()
            val currentRotation = spriteComponent.rotation - 90
            val alternativeRotation: Float
            alternativeRotation =
                if (currentRotation > 0) currentRotation - 360 else currentRotation + 360
            var difference = currentRotation - degrees
            if (Math.abs(alternativeRotation - degrees) < Math.abs(difference)) difference =
                alternativeRotation - degrees
            if (direction.x != 0f && direction.y != 0f) {
                val step = difference * deltaTime * 20
                spriteComponent.rotation = spriteComponent.rotation - step
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}
    val playerComponent: PlayerComponent
        get() = pmm[player]
    val healthComponent: HealthComponent
        get() = hm[player]
    val position: Vector2
        get() = pm[player]

    override fun dispose() {
        savePreferences()
    }

    private fun savePreferences() {
        if (isPlayerActive) {
            val preferences = Gdx.app.getPreferences("player")
            val healthComponent = healthComponent
            preferences.putFloat("health", healthComponent.value)
            preferences.putFloat("radiation", healthComponent.radiation)
            preferences.flush()
        }
    }

    private fun loadPreferences() {
        if (isPlayerActive) {
            val preferences = Gdx.app.getPreferences("player")
            val healthComponent = healthComponent
            healthComponent.value = preferences.getFloat("health", 100f)
            if (healthComponent.isDead) healthComponent.value = 50f
            healthComponent.radiation = preferences.getFloat("radiation", 0f)
        }
    }
}