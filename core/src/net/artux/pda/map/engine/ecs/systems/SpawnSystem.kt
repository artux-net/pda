package net.artux.pda.map.engine.ecs.systems

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.engine.ecs.components.BodyComponent
import net.artux.pda.map.engine.ecs.components.Group
import net.artux.pda.map.engine.ecs.components.PassivityComponent
import net.artux.pda.map.engine.ecs.components.VisionComponent
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent
import net.artux.pda.map.managers.notification.NotificationController
import net.artux.pda.map.utils.di.scope.PerGameMap
import javax.inject.Inject

@PerGameMap
class SpawnSystem @Inject constructor(
    private val notificationController: NotificationController,
    private val entityProcessorSystem: EntityProcessorSystem,
    private val dataRepository: DataRepository
) : BaseSystem(Family.all(VisionComponent::class.java, BodyComponent::class.java).exclude(PassivityComponent::class.java).get()) {
    private var spawns: ImmutableArray<Entity>? = null
    private var groupEntities: ImmutableArray<Entity>? = null
    private val sm = ComponentMapper.getFor(SpawnComponent::class.java)
    private val pm = ComponentMapper.getFor(BodyComponent::class.java)
    private val gm = ComponentMapper.getFor(Group::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        spawns = engine.getEntitiesFor(
            Family
                .all(SpawnComponent::class.java, BodyComponent::class.java)
                .exclude(PassivityComponent::class.java)
                .get()
        )
        val groupFamily = Family.all(Group::class.java, BodyComponent::class.java).get()

        groupEntities = engine.getEntitiesFor(groupFamily)

        engine.addEntityListener(groupFamily, object : EntityListener {
            override fun entityAdded(entity: Entity?) {
            }

            override fun entityRemoved(entity: Entity?) {
                gm[entity].removeEntity(entity)
            }

        })
    }

    var timer: Float = 0f
    var secsToTakeSpawn: Int = 10
    var takingSpawm: SpawnComponent? = null

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        for (i in 0 until spawns!!.size()) {
            val spawnComponent = sm[spawns!![i]]
            val spawnBodyComponent = pm[spawns!![i]]

            if (!spawnComponent.isEmpty)
                continue

            if (!spawnComponent.isActionsDone) {
                Gdx.app.applicationLogger.log("Spawn actions", "Actions sent")
                dataRepository.applyActions(spawnComponent.spawnModel.actions)
                spawnComponent.isActionsDone = true
            }


            var minDst = 120f
            val dstToPlayer = pm.get(player).position.dst(spawnBodyComponent.position)
            if (dstToPlayer < minDst) {
                if (takingSpawm == null) {
                    takingSpawm = spawnComponent
                    timer = secsToTakeSpawn.toFloat()
                }
                notificationController.setTitle("Захват позиции через " + timer.toInt() + " cекунд")
                timer-=deltaTime
                if (timer < 1) {
                    notificationController.setTitle("Позиция захвачена!")
                    notificationController.addMessage("Бля спасибо")
                    takingSpawm == null
                    val gang = dataRepository.storyDataModel.gang
                    spawnComponent.setGroup(entityProcessorSystem.generateGroup(gang))
                }
            } else if (takingSpawm == spawnComponent)
                takingSpawm = null
            else {
                var minDstGroup: Group? = null
                for (entity in groupEntities!!) {
                    val group = gm[entity]
                    if (group.targeting !is SpawnComponent) {
                        val dst = group.centerPoint.dst(spawnBodyComponent.position)
                        if (minDstGroup == null || dst < minDst) {
                            minDstGroup = group
                            minDst = dst
                        }
                    }
                }
                if (minDstGroup != null)
                    spawnComponent.setGroup(minDstGroup)
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}

    val emptySpawn: Entity?
        get() {
            for (entity in spawns!!) {
                val spawnComponent = sm[entity]
                if (spawnComponent.isEmpty) {
                    return entity
                }
            }
            return null
        }
    val randomSpawn: Entity
        get() = spawns!!.random()
}