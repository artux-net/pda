package net.artux.pda.map.engine.ecs.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.model.user.Gang;

import java.util.List;
import java.util.Set;

public class GroupComponent implements Component {

    ComponentMapper<BodyComponent> positionComponentComponentMapper = ComponentMapper.getFor(BodyComponent.class);

    private final Gang gang;
    private final MessageDispatcher dispatcher;
    private final MoodComponent mood;
    private final List<Entity> entities;
    private GroupTargetMovingComponent.Targeting targeting;
    private final Set<String> params;

    public GroupComponent(Gang gang, Integer[] relations, List<Entity> entities, Set<String> params) {
        this.gang = gang;
        this.entities = entities;
        this.params = params;
        this.mood = new MoodComponent(gang.getId(), relations, params);
        this.dispatcher = new MessageDispatcher();
        dispatcher.setDebugEnabled(true);
    }

    public Set<String> getParams() {
        return params;
    }

    public void setTargeting(GroupTargetMovingComponent.Targeting targeting) {
        this.targeting = targeting;
    }

    public Vector2 getCenterPoint() {
        Vector2 centerPoint = new Vector2();
        for (Entity e :
                entities) {
            BodyComponent pos = positionComponentComponentMapper.get(e);
            centerPoint.add(pos.getPosition());
        }
        centerPoint.scl(1f / entities.size());
        return centerPoint;
    }

    public Gang getGang() {
        return gang;
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

    public GroupTargetMovingComponent.Targeting getTargeting() {
        return targeting;
    }

    @Override
    public String toString() {
        return gang.toString() + '\n' + "Количество: " + entities.size();
    }
}