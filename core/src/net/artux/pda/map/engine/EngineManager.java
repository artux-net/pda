package net.artux.pda.map.engine;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
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
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.engine.systems.Drawable;
import net.artux.pda.map.engine.systems.InteractionSystem;
import net.artux.pda.map.engine.world.helpers.AnomalyHelper;
import net.artux.pda.map.engine.world.helpers.ControlPointsHelper;
import net.artux.pda.map.engine.world.helpers.QuestPointsHelper;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.GameMap;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.beans.PropertyChangeListener;

import javax.inject.Inject;

@PerGameMap
public class EngineManager extends InputListener implements Drawable, Disposable {

    private GameMap map;
    private UserModel userModel;
    private Entity player;
    private Engine engine;
    private final DataRepository dataRepository;

    private ComponentMapper<PositionComponent> pm = ComponentMapper.getFor(PositionComponent.class); //todo remove from here

    private boolean controlPoints = true;
    private boolean questPoints = true; //
    private boolean anomalies = true;

    private final PropertyChangeListener storyDataListener = propertyChangeEvent -> {
        if (propertyChangeEvent.getPropertyName().equals("storyData") && engine != null) {
            StoryDataModel dataModel = (StoryDataModel) propertyChangeEvent.getNewValue();
            ImmutableArray<Entity> players = engine.getEntitiesFor(Family.one(PlayerComponent.class).get());
            if (players.size() > 0) {
                Entity player = players.first();
                PlayerComponent playerComponent = player.getComponent(PlayerComponent.class);
                WeaponComponent weaponComponent = player.getComponent(WeaponComponent.class);

                playerComponent.gdxData = dataModel;
                weaponComponent.updateData(dataModel);
            }
        }
    };

    @Inject
    public EngineManager(MapComponent mapComponent) {
        this.dataRepository = mapComponent.getDataRepository();
        this.map = dataRepository.getGameMap();
        this.userModel = dataRepository.getUserModel();
        this.engine = mapComponent.getEngine();

        Stage stage = mapComponent.gameStage();

        StoryDataModel gdxData = dataRepository.getStoryDataModel();
        long loadTime = TimeUtils.millis();

        EntityBuilder entityBuilder = mapComponent.getEntityBuilder();
        player = entityBuilder.player(Mappers.vector2(map.getDefPos()), gdxData, userModel);
        engine.addEntity(player);

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(mapComponent);
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(mapComponent);
        if (anomalies)
            AnomalyHelper.createAnomalies(mapComponent);

//        RandomSpawnerHelper.init(coreComponent);

        mapComponent.getConditionManager()
                .update(gdxData);

        dataRepository.addPropertyChangeListener(storyDataListener);
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
        dataRepository.removePropertyChangeListener(storyDataListener);
    }

    public Engine getEngine() {
        return engine;
    }

    public void updateOnlyPlayer() {
        pm.get(player).set(Mappers.vector2(dataRepository.getGameMap().getDefPos()));
        engine.getSystem(InteractionSystem.class).setProcessing(true);
    }
}
