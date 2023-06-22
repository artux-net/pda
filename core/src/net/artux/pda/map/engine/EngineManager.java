package net.artux.pda.map.engine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.repository.DataRepository;
import net.artux.pda.map.content.AnomalyHelper;
import net.artux.pda.map.content.ControlPointsHelper;
import net.artux.pda.map.content.QuestPointsHelper;
import net.artux.pda.map.content.RandomSpawnerHelper;
import net.artux.pda.map.content.SecretHelper;
import net.artux.pda.map.content.entities.EntityBuilder;
import net.artux.pda.map.engine.ecs.systems.Drawable;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;
import net.artux.pda.map.engine.ecs.systems.player.InteractionSystem;
import net.artux.pda.map.engine.ecs.systems.player.MissionsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerMovingSystem;
import net.artux.pda.map.managers.ConditionEntityManager;
import net.artux.pda.map.repository.EngineSaver;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.map.utils.di.components.MapComponent;
import net.artux.pda.map.utils.di.scope.PerGameMap;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

@PerGameMap
public class EngineManager extends InputListener implements Drawable, Disposable {

    private final GameMap map;
    private final Engine engine;
    private final DataRepository dataRepository;
    private final EngineSaver engineSaver;

    private final boolean controlPoints = true;
    private final boolean questPoints = true; //
    private final boolean anomalies = true;

    @Inject
    public EngineManager(MapComponent mapComponent, MissionsSystem missionsSystem,
                         EngineSaver engineSaver,
                         ConditionEntityManager conditionEntityManager) {
        this.dataRepository = mapComponent.getDataRepository();
        this.map = dataRepository.getGameMap();
        this.engine = mapComponent.getEngine();
        this.engineSaver = engineSaver;

        Stage stage = mapComponent.gameStage();

        long loadTime = TimeUtils.millis();

        EntityBuilder entityBuilder = mapComponent.getEntityBuilder();
        Entity player = entityBuilder.player(Mappers.vector2(map.getDefPos()), dataRepository);

        engine.addEntity(player);

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(mapComponent);
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(mapComponent);
        if (anomalies)
            AnomalyHelper.createAnomalies(mapComponent);
        SecretHelper.createAnomalies(mapComponent);
        conditionEntityManager.update();
        RandomSpawnerHelper.init(mapComponent);

        missionsSystem.setActiveMission(missionsSystem.getActiveMission()); // finds points

        stage.addListener(this);
        syncCameraPosition(stage);
        Gdx.app.getApplicationLogger().log("Engine", "Engine loading took " + (TimeUtils.millis() - loadTime) + " ms.");
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
    }

    public Engine getEngine() {
        return engine;
    }

    public void updateOnlyPlayer() {
        engine.getSystem(InteractionSystem.class).setProcessing(true);
        if (dataRepository.getUpdated()) {
            Vector2 pos = Mappers.vector2(dataRepository.getGameMap().getDefPos());
            engine.getSystem(PlayerMovingSystem.class).setPosition(pos);
            dataRepository.setUpdated(false);
        }
    }

    public void save() {
        engineSaver.save(engine);
    }
}
