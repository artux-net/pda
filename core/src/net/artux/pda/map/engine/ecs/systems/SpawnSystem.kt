package net.artux.pda.map.engine.ecs.systems

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.core.EntityListener
import com.badlogic.ashley.core.Family
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.ashley.utils.ImmutableArray
import com.badlogic.gdx.Gdx
import net.artux.pda.map.DataRepository
import net.artux.pda.map.engine.ecs.components.*
import net.artux.pda.map.engine.ecs.components.map.SpawnComponent
import net.artux.pda.map.utils.di.scope.PerGameMap
import javax.inject.Inject

@PerGameMap
class SpawnSystem @Inject constructor(private val dataRepository: DataRepository) : IteratingSystem(
    Family.all(
        VisionComponent::class.java, BodyComponent::class.java
    ).exclude(
        PassivityComponent::class.java
    ).get()
) {
    private var spawns: ImmutableArray<Entity>? = null
    private var groupEntities: ImmutableArray<Entity>? = null
    private val sm = ComponentMapper.getFor(
        SpawnComponent::class.java
    )
    private val pm = ComponentMapper.getFor(
        BodyComponent::class.java
    )
    private val gm = ComponentMapper.getFor(
        GroupComponent::class.java
    )

    override fun addedToEngine(engine: Engine) {
        super.addedToEngine(engine)
        spawns = engine.getEntitiesFor(
            Family
                .all(SpawnComponent::class.java, BodyComponent::class.java)
                .exclude(PassivityComponent::class.java)
                .get()
        )
        val groupFamily = Family.all(GroupComponent::class.java, BodyComponent::class.java).get()

        groupEntities = engine.getEntitiesFor(groupFamily)

        engine.addEntityListener(groupFamily, object : EntityListener{
            override fun entityAdded(entity: Entity?) {
            }

            override fun entityRemoved(entity: Entity?) {
                gm[entity].removeEntity(entity)
            }

        })
    }

    override fun update(deltaTime: Float) {
        super.update(deltaTime)
        for (i in 0 until spawns!!.size()) {
            val spawnComponent = sm[spawns!![i]]
            val spawnBodyComponent = pm[spawns!![i]]

            if (spawnComponent.isEmpty) {
                if (!spawnComponent.isActionsDone) {
                    Gdx.app.applicationLogger.log("Spawn actions", "Actions sent")
                    dataRepository.applyActions(spawnComponent.spawnModel.actions)
                    spawnComponent.isActionsDone = true
                }
                var minDstGroup: GroupComponent? = null
                var minDst = 50f
                for (entity in groupEntities!!) {
                    val group = gm[entity]
                    if (group.targeting !is SpawnComponent) {
                        val dst = group.centerPoint.dst(spawnBodyComponent.position)
                        if (minDstGroup == null
                            || dst < minDst
                        ) {
                            minDstGroup = group
                            minDst = dst
                        }
                        spawnComponent.setGroup(group)
                    }
                }
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