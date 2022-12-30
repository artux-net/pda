package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.PointComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

@PerGameMap
public class MissionsSystem extends BaseSystem implements Disposable {

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class);
    private ComponentMapper<PointComponent> qcm = ComponentMapper.getFor(PointComponent.class);

    private final float pixelsPerMeter = 3f;
    private final DataRepository dataRepository;
    private MissionModel activeMission;
    private Entity targetEntity;

    @Inject
    public MissionsSystem(DataRepository dataRepository) {
        super(Family.all(PositionComponent.class, PointComponent.class).exclude(PassivityComponent.class).get());
        this.dataRepository = dataRepository;
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        loadPreferences();
    }

    public String[] getParams() {
        StoryDataModel storyDataModel = dataRepository.getStoryDataModel();
        return storyDataModel.getParameters().stream()
                .map(ParameterModel::getKey).toArray(String[]::new);
    }

    public List<MissionModel> getMissions() {
        return dataRepository.getStoryModel().getCurrentMissions(getParams());
    }

    public List<PointComponent> getPoints() {
        LinkedList<PointComponent> points = new LinkedList<>();
        for (Entity entity : getEntities()) {
            points.add(qcm.get(entity));
        }
        return points;
    }

    public void setActiveMission(String id) {
        List<MissionModel> missionModels = getMissions();
        for (MissionModel m : missionModels) {
            if (m.getName().equals(id)) {
                activeMission = m;
            }
        }
        if (activeMission == null && getEntities().size() > 0) {
            if (missionModels.size() > 0)
                activeMission = missionModels.get(0);
            else
                targetEntity = getEntities().get(0);
        }
        if (activeMission != null) {
            CheckpointModel currentCheckpoint = activeMission.getCurrentCheckpoint(getParams());
            int chapter = currentCheckpoint.getChapter();
            int stage = currentCheckpoint.getChapter();
            for (Entity entity : getEntities()) {
                if (qcm.get(entity).hashCode() == 31 * chapter * stage) {
                    setTargetEntity(entity); //TODO count transfers and other maps
                }
            }
        }
    }


    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    public Vector2 getPosition() {
        PositionComponent playerPosition = pm.get(getPlayer());
        return playerPosition.getPosition();
    }

    public int getTargetDistance() {
        if (targetEntity != null) {
            Vector2 targetPosition = pm.get(targetEntity);
            if (!targetPosition.isZero()) {
                return (int) (getPosition().dst(targetPosition) / pixelsPerMeter);
            }
        }
        return -1;
    }

    public double getTargetAngle() {
        if (targetEntity != null) {
            Vector2 targetPosition = pm.get(targetEntity);
            if (!targetPosition.isZero()) {
                Vector2 pos = getPosition();
                return Math.atan2(
                        pos.y - targetPosition.y,
                        pos.x - targetPosition.x
                ) * 180.0d / Math.PI + 90;
            }
        }
        return 0;
    }

    private void setTargetEntity(Entity entity) {
        targetEntity = entity;
    }

    @Override
    public void dispose() {
        savePreferences();
    }

    public void savePreferences() {
        if (getPlayer() != null) {
            Preferences preferences = Gdx.app.getPreferences("missions");
            if (activeMission != null)
                preferences.putString("active", activeMission.getName());
            preferences.flush();
        }
    }

    private void loadPreferences() {
        if (getPlayer() != null) {
            Preferences preferences = Gdx.app.getPreferences("missions");

            String name = preferences.getString("active");
            setActiveMission(name);
        }
    }

}
