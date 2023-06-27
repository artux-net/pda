package net.artux.pda.map.ecs.ai;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import net.artux.pda.map.ecs.physics.BodyComponent;
import net.artux.pda.model.map.Strength;
import net.artux.pda.model.user.Gang;

import java.util.Set;

public class StalkerGroup implements Component {

    ComponentMapper<BodyComponent> positionComponentComponentMapper = ComponentMapper.getFor(BodyComponent.class);

    private final Gang gang;
    private final Integer[] relations;
    private final MessageDispatcher dispatcher;
    private final Array<Entity> entities;
    private Strength strength;
    private TargetMovingComponent.Targeting targeting;
    private final Set<String> params;

    public StalkerGroup(Gang gang, Integer[] relations, Strength strength, Set<String> params) {
        this.gang = gang;
        this.relations = relations;
        this.strength = strength;
        this.params = params;
        this.dispatcher = new MessageDispatcher();
        entities = new Array<>(40);
        dispatcher.setDebugEnabled(false);
    }

    public Set<String> getParams() {
        return params;
    }

    public void setTargeting(TargetMovingComponent.Targeting targeting) {
        this.targeting = targeting;
    }

    public Vector2 getCenterPoint() {
        Vector2 centerPoint = new Vector2();
        for (Entity e : entities) {
            BodyComponent pos = positionComponentComponentMapper.get(e);
            centerPoint.add(pos.getPosition());
        }
        centerPoint.scl(1f / entities.size);
        return centerPoint;
    }

    public Gang getGang() {
        return gang;
    }

    public int size() {
        return entities.size;
    }

    public void setStrength(Strength strength) {
        this.strength = strength;
    }

    public void addEntity(Entity e) {
        entities.add(e);
    }

    public void removeEntity(Entity e) {
        entities.removeValue(e, false);
    }

    public Integer[] getRelations() {
        return relations;
    }

    public Strength getStrength() {
        return strength;
    }

    public Array<Entity> getEntities() {
        return entities;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public TargetMovingComponent.Targeting getTargeting() {
        return targeting;
    }

}