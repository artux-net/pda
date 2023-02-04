package net.artux.pda.map.engine

import com.badlogic.ashley.core.ComponentMapper
import com.badlogic.ashley.core.Engine
import com.badlogic.ashley.core.Family
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.artux.pda.map.DataRepository
import net.artux.pda.map.di.scope.PerGameMap
import net.artux.pda.map.engine.ecs.components.PassivityComponent
import net.artux.pda.map.engine.ecs.components.map.ConditionComponent
import net.artux.pda.model.QuestUtil
import net.artux.pda.model.quest.story.StoryDataModel
import javax.inject.Inject

@PerGameMap
class ConditionEntityManager @Inject constructor(
    private val engine: Engine,
    dataRepository: DataRepository
) {
    private val cm = ComponentMapper.getFor(
        ConditionComponent::class.java
    )

    fun update(dataModel: StoryDataModel?) {
        val currentEntities = engine.getEntitiesFor(Family.one(ConditionComponent::class.java).get())
        for (e in currentEntities)
            if (QuestUtil.check(cm[e], dataModel))
                e.remove(PassivityComponent::class.java)
            else
                e.add(PassivityComponent())
    }

    init {
        CoroutineScope(Dispatchers.Main).launch {
            dataRepository.storyDataModelFlow.collect {
                update(it)
            }
        }
    }
}