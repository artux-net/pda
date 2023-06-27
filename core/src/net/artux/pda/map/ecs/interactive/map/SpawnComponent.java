package net.artux.pda.map.ecs.interactive.map;

import static com.badlogic.gdx.math.MathUtils.random;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector2;

import net.artux.pda.map.ecs.ai.StalkerGroup;
import net.artux.pda.map.ecs.ai.TargetMovingComponent;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.SpawnModel;

import org.apache.commons.lang3.StringUtils;

public class SpawnComponent implements Component, TargetMovingComponent.Targeting {

    private final SpawnModel spawnModel;
    private boolean actionsDone;
    private StalkerGroup stalkerGroup;

    public SpawnComponent(SpawnModel spawnModel) {
        this.spawnModel = spawnModel;
    }

    public void setStalkerGroup(StalkerGroup stalkerGroup) {
        this.stalkerGroup = stalkerGroup;
        stalkerGroup.setTargeting(this);
    }

    public StalkerGroup getStalkerGroup() {
        return stalkerGroup;
    }

    public String getTitle(){
        return spawnModel.getTitle();
    }

    public boolean isEmpty() {
        return stalkerGroup == null || stalkerGroup.getEntities().size < 1;
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
        if (!StringUtils.isBlank(spawnModel.getTitle()))
            stringBuilder.append("Объект: ").append(spawnModel.getTitle());
        else
            stringBuilder.append("Контрольная точка.");
        stringBuilder.append('\n');
        if (isEmpty())
            stringBuilder.append("Не занята");
        else
            stringBuilder.append("Занята");
        stringBuilder.append('\n');
        stringBuilder.append(getStalkerGroup().toString());
        return stringBuilder.toString();
    }

    @Override
    public int hashCode() {
        return spawnModel.getTitle().hashCode();
    }
}