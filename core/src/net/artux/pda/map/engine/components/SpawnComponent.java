package net.artux.pda.map.engine.components;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.SpawnModel;

import java.util.List;

public class SpawnComponent implements Component {

    private final MessageDispatcher dispatcher;
    private final SpawnModel spawnModel;
    private final TargetMovingComponent.Targeting targeting;
    private final Integer[] relations;
    private final List<Entity> entities;

    private boolean actionsDone;

    public SpawnComponent(SpawnModel spawnModel, Integer[] relations, List<Entity> entities) {
        this.relations = relations;
        this.entities = entities;
        this.dispatcher = new MessageDispatcher();
        dispatcher.setDebugEnabled(true);
        this.spawnModel = spawnModel;
        this.targeting = () -> {
            double r = (double) spawnModel.getR() / 2 + random.nextInt(spawnModel.getR());

            double angle = random.nextInt(360);

            Vector2 basePosition = Mappers.vector2(spawnModel.getPos());
            float x = (float) (Math.cos(angle) * r);
            float y = (float) (Math.sin(angle) * r);
            return new Vector2(basePosition.x + x, basePosition.y + y);
        };
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setActionsDone(boolean actionsDone) {
        this.actionsDone = actionsDone;
    }

    public boolean isActionsDone() {
        return actionsDone;
    }

    public MessageDispatcher getDispatcher() {
        return dispatcher;
    }

    public SpawnModel getSpawnModel() {
        return spawnModel;
    }

    public int getGroup() {
        return spawnModel.getId();
    }

    public Integer[] getRelations() {
        return relations;
    }

    public TargetMovingComponent.Targeting getTargeting() {
        return targeting;
    }
}