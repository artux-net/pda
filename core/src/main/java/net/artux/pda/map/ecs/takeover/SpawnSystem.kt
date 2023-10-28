package net.artux.pda.map.ecs.takeover

import com.badlogic.ashley.core.*
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.ecs.physics.BodyComponent
import net.artux.pda.map.ecs.ai.StalkerGroup
import net.artux.pda.map.ecs.interactive.PassivityComponent
import net.artux.pda.map.ecs.vision.VisionComponent
import net.artux.pda.map.ecs.interactive.map.SpawnComponent
import net.artux.pda.map.ecs.systems.BaseSystem
import net.artux.pda.map.ecs.creation.EntityProcessorSystem
import net.artux.pda.map.controller.notification.NotificationController
import net.artux.pda.map.di.scope.PerGameMap
import javax.inject.Inject

@PerGameMap
class SpawnSystem @Inject constructor(
    private val notificationController: NotificationController,
    private val entityProcessorSystem: EntityProcessorSystem,
    private val dataRepository: DataRepository
) : BaseSystem(Family.all(VisionComponent::class.java, BodyComponent::class.java).exclude(
    PassivityComponent::class.java).get()) {
    lateinit var spawns: ImmutableArray<Entity>
    private var groupEntities: ImmutableArray<Entity>? = null
    private val sm = ComponentMapper.getFor(SpawnComponent::class.java)
    private val pm = ComponentMapper.getFor(BodyComponent::class.java)
    private val gm = ComponentMapper.getFor(StalkerGroup::class.java)

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        spawns = engine.getEntitiesFor(
            Family
                .all(SpawnComponent::class.java, BodyComponent::class.java)
                .exclude(PassivityComponent::class.java)
                .get()
        )
        val stalkerGroupFamily = Family.all(StalkerGroup::class.java, BodyComponent::class.java).get()

        groupEntities = engine.getEntitiesFor(stalkerGroupFamily)

        engine.addEntityListener(stalkerGroupFamily, object : EntityListener {
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
        for (i in 0 until spawns.size()) {
            val spawnComponent = sm[spawns[i]]
            val spawnBodyComponent = pm[spawns[i]]

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
                    notificationController.addMessage("Спасибо, уже отправили группу на занятие позиции.")
                    takingSpawm == null
                    val gang = dataRepository.initDataModel.gang
                    spawnComponent.stalkerGroup = entityProcessorSystem.generateNewGroup(gang)
                }
            } else if (takingSpawm == spawnComponent)
                takingSpawm = null
            else {
                var minDstStalkerGroup: StalkerGroup? = null
                for (entity in groupEntities!!) {
                    val group = gm[entity]
                    if (group.targeting !is SpawnComponent) {
                        val dst = group.centerPoint.dst(spawnBodyComponent.position)
                        if (minDstStalkerGroup == null || dst < minDst) {
                            minDstStalkerGroup = group
                            minDst = dst
                        }
                    }
                }
                if (minDstStalkerGroup != null)
                    spawnComponent.stalkerGroup = minDstStalkerGroup
            }
        }
    }

    override fun processEntity(entity: Entity, deltaTime: Float) {}

    val emptySpawn: Entity?
        get() {
            for (entity in spawns) {
                val spawnComponent = sm[entity]
                if (spawnComponent.isEmpty) {
                    return entity
                }
            }
            return null
        }

    val randomSpawn: Entity?
        get() = spawns.random()
}