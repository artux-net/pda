package net.artux.pda.map.engine.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.model.BotType;

import java.util.List;
import java.util.Set;

public class GroupComponent implements Component {

    ComponentMapper<PositionComponent> positionComponentComponentMapper = ComponentMapper.getFor(PositionComponent.class);

    private final BotType botType;
    private final MessageDispatcher dispatcher;
    private final MoodComponent mood;
    private final List<Entity> entities;
    private TargetMovingComponent.Targeting targeting;
    private Set<String> params;

    public GroupComponent(BotType botType, List<Entity> entities, Set<String> params) {
        this.botType = botType;
        this.mood = botType.getMood(params);
        this.entities = entities;
        this.params = params;
        this.dispatcher = new MessageDispatcher();
        dispatcher.setDebugEnabled(true);
    }

    public Set<String> getParams() {
        return params;
    }

    public void setTargeting(TargetMovingComponent.Targeting targeting) {
        this.targeting = targeting;
    }

    public Vector2 getCenterPoint() {
        Vector2 centerPoint = new Vector2();
        for (Entity e :
                entities) {
            PositionComponent pos = positionComponentComponentMapper.get(e);
            centerPoint.add(pos);
        }
        centerPoint.scl(1f / entities.size());
        return centerPoint;
    }

    public MoodComponent getMood() {
        return mood;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public TargetMovingComponent.Targeting getTargeting() {
        return targeting;
    }

    @Override
    public String toString() {
        return botType.name + '\n' + "Количество: " + entities.size();
    }
}