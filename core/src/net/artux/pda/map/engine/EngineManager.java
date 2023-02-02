package net.artux.pda.map.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.di.core.MapComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.components.Position;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.Drawable;
import net.artux.pda.map.engine.systems.player.CameraSystem;
import net.artux.pda.map.engine.systems.player.InteractionSystem;
import net.artux.pda.map.engine.systems.player.MissionsSystem;
import net.artux.pda.map.engine.systems.player.PlayerSystem;
import net.artux.pda.map.engine.world.helpers.ControlPointsHelper;
import net.artux.pda.map.engine.world.helpers.QuestPointsHelper;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.story.StoryDataModel;

import java.beans.PropertyChangeListener;

import javax.inject.Inject;

@PerGameMap
public class EngineManager extends InputListener implements Drawable, Disposable {

    private GameMap map;
    private Entity player;
    private Engine engine;
    private final DataRepository dataRepository;

    private ComponentMapper<Position> pm = ComponentMapper.getFor(Position.class); //todo remove from here

    private boolean controlPoints = true;
    private boolean questPoints = true; //
    private boolean anomalies = true;

    private final PropertyChangeListener storyDataListener = propertyChangeEvent -> {
        if (propertyChangeEvent.getPropertyName().equals("storyData") && engine != null) {
            StoryDataModel oldDataModel = (StoryDataModel) propertyChangeEvent.getOldValue();
            StoryDataModel dataModel = (StoryDataModel) propertyChangeEvent.getNewValue();
            PlayerSystem playerSystem = engine.getSystem(PlayerSystem.class);
            if (playerSystem==null)
                return;
            Entity player = playerSystem.getPlayer();
            if (player != null) {
                PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
                WeaponComponent weaponComponent = player.getComponent(WeaponComponent.class);

                playerComponent.gdxData = dataModel;
                weaponComponent.updateData(dataModel);
                engine.getSystem(MissionsSystem.class).updateData(oldDataModel);
            }
        }
    };

    @Inject
    public EngineManager(MapComponent mapComponent, MissionsSystem missionsSystem) {
        this.dataRepository = mapComponent.getDataRepository();
        this.map = dataRepository.getGameMap();
        this.engine = mapComponent.getEngine();

        Stage stage = mapComponent.gameStage();

        StoryDataModel gdxData = dataRepository.getCurrentStoryDataModel();
        long loadTime = TimeUtils.millis();

        EntityBuilder entityBuilder = mapComponent.getEntityBuilder();
        player = entityBuilder.player(Mappers.vector2(map.getDefPos()), dataRepository);
        engine.addEntity(player);

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(mapComponent);
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(mapComponent);
      /*  if (anomalies)
            AnomalyHelper.createAnomalies(mapComponent);*/

//        RandomSpawnerHelper.init(coreComponent);
        mapComponent.getConditionManager()
                .update(gdxData);
        missionsSystem.setActiveMission(missionsSystem.getActiveMission()); // finds points

        /*dataRepository.getStoryDataModelFlow().collect(new FlowCollector<StoryDataModel>() {
            @Nullable
            @Override
            public Object emit(StoryDataModel storyDataModel, @NotNull Continuation<? super Unit> continuation) {
                return null;
            }
        });*/
        stage.addListener(this);
        syncCameraPosition(stage);
        Gdx.app.log("Engine", "Engine loading took " + (TimeUtils.millis() - loadTime) + " ms.");
    }

    private void syncCameraPosition(Stage stage) {
        Camera camera = stage.getViewport().getCamera();
        camera.position.x = Mappers.vector2(map.getDefPos()).x;
        camera.position.y = Mappers.vector2(map.getDefPos()).y;
    }

    public GestureDetector.GestureListener getGestureListener() {
        return engine.getSystem(CameraSystem.class);
    }

    public void update(float dt) {
        engine.update(dt);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for (EntitySystem s : engine.getSystems()) {
            if (s instanceof Drawable) ((Drawable) s).draw(batch, parentAlpha);
        }
    }

    @Override
    public void dispose() {
        for (EntitySystem s : engine.getSystems()) {
            if (s instanceof Disposable) ((Disposable) s).dispose();
        }
        //dataRepository.removePropertyChangeListener(storyDataListener);
    }

    public Engine getEngine() {
        return engine;
    }

    public void updateOnlyPlayer() {
        pm.get(player).set(Mappers.vector2(dataRepository.getGameMap().getDefPos()));
        engine.getSystem(InteractionSystem.class).setProcessing(true);
    }
}
