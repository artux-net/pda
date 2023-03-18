package net.artux.pda.map.engine;

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
import net.artux.pda.map.di.components.MapComponent;
import net.artux.pda.map.di.scope.PerGameMap;
import net.artux.pda.map.engine.ecs.systems.Drawable;
import net.artux.pda.map.engine.ecs.systems.player.CameraSystem;
import net.artux.pda.map.engine.ecs.systems.player.InteractionSystem;
import net.artux.pda.map.engine.ecs.systems.player.MissionsSystem;
import net.artux.pda.map.engine.ecs.systems.player.PlayerSystem;
import net.artux.pda.map.engine.helpers.AnomalyHelper;
import net.artux.pda.map.engine.helpers.ControlPointsHelper;
import net.artux.pda.map.engine.helpers.EntityBuilder;
import net.artux.pda.map.engine.helpers.QuestPointsHelper;
import net.artux.pda.map.utils.Mappers;
import net.artux.pda.model.map.GameMap;

import javax.inject.Inject;

@PerGameMap
public class EngineManager extends InputListener implements Drawable, Disposable {

    private GameMap map;
    private Entity player;
    private Engine engine;
    private final DataRepository dataRepository;

    private boolean controlPoints = true;
    private boolean questPoints = true; //
    private boolean anomalies = true;

    @Inject
    public EngineManager(MapComponent mapComponent, MissionsSystem missionsSystem) {
        this.dataRepository = mapComponent.getDataRepository();
        this.map = dataRepository.getGameMap();
        this.engine = mapComponent.getEngine();

        Stage stage = mapComponent.gameStage();

        long loadTime = TimeUtils.millis();

        EntityBuilder entityBuilder = mapComponent.getEntityBuilder();
        player = entityBuilder.player(Mappers.vector2(map.getDefPos()), dataRepository);
        engine.addEntity(player);

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(mapComponent);
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(mapComponent);
        if (anomalies)
            AnomalyHelper.createAnomalies(mapComponent);

//        RandomSpawnerHelper.init(coreComponent);

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
        engine.getSystem(PlayerSystem.class).getPosition().set(Mappers.vector2(dataRepository.getGameMap().getDefPos()));
        engine.getSystem(InteractionSystem.class).setProcessing(true);
    }
}
