package net.artux.pda.map.engine.components.map;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.engine.components.GroupComponent;
import net.artux.pda.map.engine.components.GroupTargetMovingComponent;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.SpawnModel;

public class SpawnComponent implements Component, GroupTargetMovingComponent.Targeting {

    private final SpawnModel spawnModel;
    private GroupComponent groupComponent;
    private boolean actionsDone;

    public SpawnComponent(SpawnModel spawnModel) {
        this.spawnModel = spawnModel;
    }

    public void setGroup(GroupComponent groupComponent) {
        this.groupComponent = groupComponent;
        groupComponent.setTargeting(this);
    }

    public GroupComponent getGroupComponent() {
        return groupComponent;
    }

    public boolean isEmpty() {
        return groupComponent == null || groupComponent.getEntities().size() < 1;
    }

    public Vector2 getPosition() {
        return Mappers.vector2(spawnModel.getPos());
    }

    public void setActionsDone(boolean actionsDone) {
        this.actionsDone = actionsDone;
    }

    public boolean isActionsDone() {
        return actionsDone;
    }

    public SpawnModel getSpawnModel() {
        return spawnModel;
    }


    @Override
    public Vector2 getTarget() {
        double r = (double) spawnModel.getR() / 2 + random.nextInt(spawnModel.getR());

        double angle = random.nextInt(360);

        Vector2 basePosition = Mappers.vector2(spawnModel.getPos());
        float x = (float) (Math.cos(angle) * r);
        float y = (float) (Math.sin(angle) * r);
        return new Vector2(basePosition.x + x, basePosition.y + y);
    }

    public String desc() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Объект: ").append(spawnModel.getTitle());
        stringBuilder.append('\n');
        if (isEmpty())
            stringBuilder.append("Не занята");
        else
            stringBuilder.append("Занята");
        stringBuilder.append('\n');
        stringBuilder.append(getGroupComponent().toString());
        return stringBuilder.toString();

    }
}