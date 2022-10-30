package net.artux.pda.map.engine;

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
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.entities.EntityBuilder;
import net.artux.pda.map.engine.systems.ArtifactSystem;
import net.artux.pda.map.engine.systems.BattleSystem;
import net.artux.pda.map.engine.systems.CameraSystem;
import net.artux.pda.map.engine.systems.ClicksSystem;
import net.artux.pda.map.engine.systems.DataSystem;
import net.artux.pda.map.engine.systems.DeadCheckerSystem;
import net.artux.pda.map.engine.systems.Drawable;
import net.artux.pda.map.engine.systems.InteractionSystem;
import net.artux.pda.map.engine.systems.MapLoggerSystem;
import net.artux.pda.map.engine.systems.MapOrientationSystem;
import net.artux.pda.map.engine.systems.MessagesSystem;
import net.artux.pda.map.engine.systems.MovementTargetingSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.StatesSystem;
import net.artux.pda.map.engine.systems.VisionSystem;
import net.artux.pda.map.engine.systems.WorldSystem;
import net.artux.pda.map.engine.world.helpers.AnomalyHelper;
import net.artux.pda.map.engine.world.helpers.ControlPointsHelper;
import net.artux.pda.map.engine.world.helpers.QuestPointsHelper;
import net.artux.pda.map.model.input.GameMap;
import net.artux.pda.map.platform.PlatformInterface;
import net.artux.pda.map.states.PlayState;
import net.artux.pda.model.quest.story.StoryDataModel;
import net.artux.pda.model.user.UserModel;

import java.beans.PropertyChangeListener;

public class EngineManager extends InputListener implements Drawable, Disposable {

    private GameMap map;
    private UserModel userModel;

    private Engine engine;
    private final DataRepository dataRepository;

    private boolean controlPoints = true;
    private boolean questPoints = true;
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
                engine.getSystem(PlayerSystem.class).updateData(dataModel);
                engine.getSystem(PlayerSystem.class).savePreferences();
            }
        }
    };

    public EngineManager(AssetsFinder assetsFinder, Stage stage, PlayState playState) {
        this.engine = new Engine();
        dataRepository = playState.getDataRepository();
        PlatformInterface platformInterface = playState.getGSM().getPlatformInterface();
        this.map = dataRepository.getGameMap();
        this.userModel = dataRepository.getUserModel();
        StoryDataModel gdxData = dataRepository.getStoryDataModel();
        long loadTime = TimeUtils.millis();

        engine.addSystem(new MapOrientationSystem(assetsFinder, map));
        engine.addSystem(new WorldSystem(assetsFinder.getManager()));
        EntityBuilder entityBuilder = engine.getSystem(WorldSystem.class).getEntityBuilder();
        engine.addEntity(entityBuilder.player(map.getPlayerPosition(), gdxData, userModel));


        engine.addSystem(new ClicksSystem());
        engine.addSystem(new CameraSystem(stage.getCamera()));
        engine.addSystem(new SoundsSystem(assetsFinder.getManager()));

        engine.addSystem(new DataSystem(map, userModel));
        engine.addSystem(new InteractionSystem(stage, playState.getUserInterface()));
        engine.addSystem(new PlayerSystem(assetsFinder.getManager()));

        engine.addSystem(new MessagesSystem(playState.getUserInterface()));
        engine.addSystem(new ArtifactSystem());
        engine.addSystem(new MapLoggerSystem());
        engine.addSystem(new RenderSystem(stage, assetsFinder));
        engine.addSystem(new BattleSystem(assetsFinder.getManager(), platformInterface));

        engine.addSystem(new MovementTargetingSystem());
        engine.addSystem(new VisionSystem(assetsFinder.getManager()));
        engine.addSystem(new MovingSystem());
        engine.addSystem(new DeadCheckerSystem(playState.getUserInterface(), dataRepository, assetsFinder.getManager()));

        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(engine, entityBuilder, assetsFinder.getManager());
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(engine, assetsFinder.getManager(), platformInterface);
        if (anomalies)
            AnomalyHelper.createAnomalies(engine, assetsFinder.getManager());

        engine.addSystem(new StatesSystem());
        engine.getSystem(PlayerSystem.class).updateData(gdxData);

        stage.addListener(this);
        syncCameraPosition(stage);
        Gdx.app.log("Engine", "Engine loading took " + (TimeUtils.millis() - loadTime) + " ms.");
    }

    private void syncCameraPosition(Stage stage) {
        Camera camera = stage.getViewport().getCamera();
        camera.position.x = map.getPlayerPosition().x;
        camera.position.y = map.getPlayerPosition().y;
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
}
