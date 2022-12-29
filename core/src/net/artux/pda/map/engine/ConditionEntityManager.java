package net.artux.pda.map.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.ConditionComponent;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.model.QuestUtil;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.beans.PropertyChangeListener;

import javax.inject.Inject;

@PerGameMap
public class ConditionEntityManager {

    private final Engine engine;
    private final ComponentMapper<ConditionComponent> cm = ComponentMapper.getFor(ConditionComponent.class);

    @Inject
    public ConditionEntityManager(Engine engine, DataRepository dataRepository) {
        this.engine = engine;
        PropertyChangeListener storyDataListener = propertyChangeEvent -> {
            if (propertyChangeEvent.getPropertyName().equals("storyData")) {
                StoryDataModel dataModel = (StoryDataModel) propertyChangeEvent.getNewValue();
                update(dataModel);
            }
        };
        dataRepository.addPropertyChangeListener(storyDataListener);
    }

    public void update(StoryDataModel dataModel) {
        ImmutableArray<Entity> currentEntities = engine
                .getEntitiesFor(Family.one(ConditionComponent.class).get());

        for (Entity e : currentEntities)
            if (QuestUtil.check(cm.get(e), dataModel))
                e.remove(PassivityComponent.class);
            else
                e.add(new PassivityComponent());
    }
}
