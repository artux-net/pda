package net.artux.pda.map.engine.systems.player;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.PassivityComponent;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.map.QuestComponent;
import net.artux.pda.map.engine.pathfinding.own.Connection;
import net.artux.pda.map.engine.pathfinding.own.Digraph;
import net.artux.pda.map.engine.pathfinding.own.DijkstraPathFinder;
import net.artux.pda.map.engine.pathfinding.own.GraphPath;
import net.artux.pda.map.engine.pathfinding.own.Node;
import net.artux.pda.map.engine.systems.BaseSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.ui.blocks.MessagesPlane;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.map.Point;
import net.artux.pda.model.quest.CheckpointModel;
import net.artux.pda.model.quest.MissionModel;
import net.artux.pda.model.quest.StoryModel;
import net.artux.pda.model.quest.story.ParameterModel;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

@PerGameMap
public class MissionsSystem extends BaseSystem implements Disposable {

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class);
    private ComponentMapper<QuestComponent> qcm = ComponentMapper.getFor(QuestComponent.class);

    private final float pixelsPerMeter = 3f;
    private final DataRepository dataRepository;
    private final CameraSystem cameraSystem;
    private final SoundsSystem soundsSystem;
    private final MessagesPlane messagesPlane;
    private final Digraph<GameMap> mapDigraph;
    private final DijkstraPathFinder<GameMap> pathFinder;
    private final Sound missionUpdatedSound;

    private MissionModel activeMission;
    private Vector2 targetPosition;

    @Inject
    public MissionsSystem(MessagesPlane messagesPlane, AssetManager assetManager, DataRepository dataRepository, SoundsSystem soundsSystem, CameraSystem cameraSystem) {
        super(Family.all(Position.class, QuestComponent.class).exclude(PassivityComponent.class).get());
        this.dataRepository = dataRepository;
        this.cameraSystem = cameraSystem;
        this.messagesPlane = messagesPlane;
        this.soundsSystem = soundsSystem;

        missionUpdatedSound = assetManager.get("audio/sounds/pda/pda_objective.ogg");

        mapDigraph = buildSystemFromStory();
        pathFinder = new DijkstraPathFinder<>();
    }

    @Override
    public void addedToEngine(Engine engine) {
        super.addedToEngine(engine);
        setActiveMissionByName("");
        loadPreferences();
    }

    public void updateData(StoryDataModel oldDataModel) {
        Set<String> oldParams = getParams(oldDataModel);
        Set<String> params = getParams(dataRepository.getStoryDataModel());

        params.removeIf(oldParams::contains);
        String[] paramArr = params.toArray(new String[]{});
        List<MissionModel> updatedMissions = getMissions(paramArr);
        for (MissionModel m :
                updatedMissions) {
            CheckpointModel checkpointModel = m.getCurrentCheckpoint(paramArr);
            messagesPlane.addMessage("avatars/a0.jpg", "Задание обновлено: " + m.getTitle(),
                    "Новая цель: " + checkpointModel.getTitle(), MessagesPlane.Length.SHORT);
            soundsSystem.playSound(missionUpdatedSound);
        }
    }

    public Set<String> getParams(StoryDataModel dataModel) {
        return dataModel.getParameters().stream()
                .map(ParameterModel::getKey).collect(Collectors.toSet());
    }

    public String[] getParams() {
        StoryDataModel dataModel = dataRepository.getStoryDataModel();
        return dataModel.getParameters().stream()
                .map(ParameterModel::getKey).toArray(String[]::new);
    }

    public List<MissionModel> getMissions() {
        return getMissions(getParams());
    }

    public List<MissionModel> getMissions(String[] params) {
        return dataRepository.getStoryModel().getCurrentMissions(params);
    }

    public List<QuestComponent> getPoints() {
        LinkedList<QuestComponent> points = new LinkedList<>();
        for (Entity entity : getEntities()) {
            points.add(qcm.get(entity));
        }
        return points;
    }

    public void setActiveMissionByName(String name) {
        List<MissionModel> missionModels = getMissions();
        for (MissionModel m : missionModels) {
            if (m.getName().equals(name)) {
                setActiveMission(activeMission);
            }
        }
        if (activeMission == null) {
            if (missionModels.size() > 0)
                setActiveMission(missionModels.get(0));
            else if (getEntities().size() > 0)
                setTargetPosition(pm.get(getEntities().first()));
        }
    }

    public MissionModel getActiveMission() {
        return activeMission;
    }

    public void setActiveMission(MissionModel activeMission) {
        this.activeMission = activeMission;

        if (activeMission == null)
            return;

        CheckpointModel currentCheckpoint = activeMission.getCurrentCheckpoint(getParams());
        int chapter = currentCheckpoint.getChapter();
        int stage = currentCheckpoint.getStage();
        boolean found = false;
        for (Entity questEntity : getEntities()) {
            QuestComponent questComponent = qcm.get(questEntity);
            Position position = pm.get(questEntity);
            if (questComponent.contains(chapter, stage)) {
                found = true;
                setTargetPosition(position);
            }
        }
        if (!found) {
            GameMap currentMap = dataRepository.getGameMap();
            GameMap requiredMap = getRequiredMap(chapter, stage);
            if (requiredMap == currentMap || requiredMap == null)
                return;

            GraphPath<GameMap> path = pathFinder.find(mapDigraph, mapDigraph.offer(currentMap), mapDigraph.offer(requiredMap));
            if (path == null)
                return;

            Optional<Connection<GameMap>> connection = path.getConnections().stream().findFirst();
            connection.ifPresent(gameMapConnection -> setTargetPosition(Mappers
                    .vector2(((Point) gameMapConnection.getUserObject()).getPos())));
        }

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

    }

    private GameMap getRequiredMap(int chapter, int stage) {
        StoryModel storyModel = dataRepository.getStoryModel();
        for (GameMap map : storyModel.getMaps().values()) {
            for (Point point : map.getPoints()) {
                Map<String, String> currentData = point.getData();
                String chapterString = currentData.get("chapter");
                String stageString = currentData.get("stage");
                if (chapterString != null && stageString != null) {
                    int stageId = Integer.parseInt(stageString);
                    int chapterId = Integer.parseInt(chapterString);
                    if (chapterId == chapter && stageId == stage)
                        return map;
                }
            }
        }
        return null;
    }

    private Digraph<GameMap> buildSystemFromStory() {
        StoryModel storyModel = dataRepository.getStoryModel();
        Digraph<GameMap> mapDigraph = new Digraph<>();
        for (GameMap map : storyModel.getMaps().values()) {
            Node<GameMap> node = mapDigraph.offer(map);
            for (Point point : map.getPoints()) {
                if (point.getType() == 7) {
                    Map<String, String> currentData = point.getData();
                    String chapterString = currentData.get("chapter");
                    String stageString = currentData.get("stage");
                    String targetMap = currentData.get("map");//with map
                    if (chapterString != null && stageString != null) {
                        //with chapter
                        int stage = Integer.parseInt(stageString);
                        Map<String, String> data = storyModel
                                .getChapter(chapterString)
                                .getStage(stage).getData();
                        if (data != null && data.containsKey("map"))
                            targetMap = data.get("map");
                    }
                    if (targetMap != null) {
                        GameMap targetGameMap = storyModel.getMap(Long.parseLong(targetMap));
                        if (targetGameMap != null) {
                            Node<GameMap> gameMapNode = mapDigraph.offer(targetGameMap);
                            Connection<GameMap> connection = node.addConnection(gameMapNode, 1);
                            connection.setUserObject(point);
                        }
                    }
                }
            }
        }
        return mapDigraph;
    }

    public Vector2 getPosition() {
        Position playerPosition = pm.get(getPlayer());
        return playerPosition.getPosition();
    }

    public int getTargetDistance() {
        if (targetPosition != null) {
            if (!targetPosition.isZero()) {
                return (int) (getPosition().dst(targetPosition) / pixelsPerMeter);
            }
        }
        return -1;
    }

    public double getTargetAngle() {
        if (targetPosition != null) {
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

    public void setTargetPosition(Vector2 position) {
        if (position != null) {
            targetPosition = position;

            cameraSystem.setDetached(true);
            cameraSystem.getCamera().position.x = position.x;
            cameraSystem.getCamera().position.y = position.y;
        }
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
            setActiveMissionByName(name);
        }
    }

}
