package net.artux.pda.map.controller

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import com.badlogic.gdx.ApplicationLogger
import com.badlogic.gdx.Gdx
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.ecs.interactive.PassivityComponent
import net.artux.pda.map.ecs.interactive.map.ConditionComponent
import net.artux.pda.map.repository.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.ecs.player.MissionsSystem
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject

@PerGameMap
class ConditionEntityManager @Inject constructor(
    private val engine: Engine,
    val missionsSystems: MissionsSystem,
    val dataRepository: DataRepository,
    val logger: ApplicationLogger
) {
    private val cm = ComponentMapper.getFor(ConditionComponent::class.java)
    private val pcm = ComponentMapper.getFor(PassivityComponent::class.java)

    fun update() {
        update(dataRepository.initDataModel)
    }

    fun update(dataModel: StoryDataModel?) {
        val currentEntities =
            engine.getEntitiesFor(Family.one(ConditionComponent::class.java).get())
        logger.log("Points", "Update conditional-points")
        for (i in 0 until currentEntities.size()) {
            val e = currentEntities.get(i)

            if (QuestUtil.check(cm[e], dataModel!!))
                e.remove(PassivityComponent::class.java)
            else
                if (!pcm.has(e))
                    e.add(PassivityComponent())
        }
        missionsSystems.updatePoints()
    }

    fun disableConditions() {
        val currentEntities =
            engine.getEntitiesFor(Family.one(ConditionComponent::class.java).get())

        for (i in 0 until currentEntities.size()) {
            val e = currentEntities.get(i)
            e.remove(PassivityComponent::class.java)
        }
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                Gdx.app.postRunnable { update(it) }
            }
        }
    }
}