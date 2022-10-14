package net.artux.pda.map.engine;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

import net.artux.pda.map.DataRepository;
import net.artux.pda.map.engine.components.HealthComponent;
import net.artux.pda.map.engine.components.MoodComponent;
import net.artux.pda.map.engine.components.PositionComponent;
import net.artux.pda.map.engine.components.SpriteComponent;
import net.artux.pda.map.engine.components.VelocityComponent;
import net.artux.pda.map.engine.components.WeaponComponent;
import net.artux.pda.map.engine.components.player.PlayerComponent;
import net.artux.pda.map.engine.components.player.UserVelocityInput;
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
import net.artux.pda.map.engine.systems.MoodSystem;
import net.artux.pda.map.engine.systems.MovementTargetingSystem;
import net.artux.pda.map.engine.systems.MovingSystem;
import net.artux.pda.map.engine.systems.PlayerSystem;
import net.artux.pda.map.engine.systems.RenderSystem;
import net.artux.pda.map.engine.systems.SoundsSystem;
import net.artux.pda.map.engine.systems.StatesSystem;
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

public class EngineManager extends InputListener implements Drawable, Disposable, GestureDetector.GestureListener {

    private GameMap map;
    private UserModel userModel;

    private Engine engine;
    private final DataRepository dataRepository;

    private final ClicksSystem clicksSystem;
    private final CameraSystem cameraSystem;

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
        dataRepository.addPropertyChangeListener(storyDataListener);
        long loadTime = TimeUtils.millis();

        //player
        Entity player = new Entity();
        UserVelocityInput velocityComponent = new UserVelocityInput();
        Camera camera = stage.getViewport().getCamera();
        camera.position.x = map.getPlayerPosition().x;
        camera.position.y = map.getPlayerPosition().y;
        player.add(new PositionComponent(map.getPlayerPosition()))
                .add(new VelocityComponent())
                .add(new SpriteComponent(velocityComponent, assetsFinder.getManager().get("gg.png", Texture.class), 32, 32))
                .add(new WeaponComponent(gdxData))
                .add(new MoodComponent(userModel))
                .add(new HealthComponent())
                .add(velocityComponent)
                .add(new PlayerComponent(camera, userModel, gdxData));
        engine.addEntity(player);

        engine.addSystem(new MapOrientationSystem(assetsFinder, map));
        engine.addSystem(new CameraSystem());
        engine.addSystem(new SoundsSystem(assetsFinder.getManager()));
        engine.addSystem(new WorldSystem(assetsFinder.getManager()));
        engine.addSystem(new DataSystem(map, userModel));
        engine.addSystem(new InteractionSystem(stage, playState.getUserInterface()));
        engine.addSystem(new PlayerSystem(assetsFinder.getManager()));


        if (controlPoints)
            ControlPointsHelper.createControlPointsEntities(engine, assetsFinder.getManager());
        if (questPoints)
            QuestPointsHelper.createQuestPointsEntities(engine, assetsFinder.getManager(), platformInterface);
        if (anomalies)
            AnomalyHelper.createAnomalies(engine, assetsFinder.getManager());

        engine.addSystem(new MessagesSystem(playState.getUserInterface()));
        engine.addSystem(new ArtifactSystem());
        engine.addSystem(new ClicksSystem());
        engine.addSystem(new MapLoggerSystem());
        engine.addSystem(new RenderSystem(stage, assetsFinder));
        engine.addSystem(new BattleSystem(assetsFinder.getManager(), platformInterface));
        engine.addSystem(new StatesSystem());
        engine.addSystem(new MovementTargetingSystem());
        engine.addSystem(new MoodSystem(assetsFinder.getManager()));
        engine.addSystem(new MovingSystem());
        engine.addSystem(new DeadCheckerSystem(playState.getUserInterface(), platformInterface, assetsFinder.getManager()));

        clicksSystem = engine.getSystem(ClicksSystem.class);
        cameraSystem = engine.getSystem(CameraSystem.class);

        stage.addListener(this);
        Gdx.app.log("Engine", "Engine loading took " + (TimeUtils.millis() - loadTime) + " ms.");
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

    @Override
    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
        if (clicksSystem != null)
            clicksSystem.clicked(x, y);
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        cameraSystem.setDetached(true);
        cameraSystem.moveBy(-deltaX, deltaY);
        return true;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        //Gdx.app.debug("", velocityX + ":" + velocityY);
        //TODO fling
        return false;
    }

    private Vector2 lastPointer;
    private float lastZoom;

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        cameraSystem.setZooming(true);
        cameraSystem.setDetached(true);

        float panAmount = pointer1.dst(pointer2);
        float initAmount = initialPointer1.dst(initialPointer2);
        float newZoom = (initAmount / panAmount) * cameraSystem.getCurrentZoom();

        Vector2 centerPoint = new Vector2((pointer1.x + pointer2.x) / 2, (pointer1.y + pointer2.y) / 2);

        if (lastPointer != null) {
            Vector2 cameraPosition = cameraSystem.getPosition();
            Vector2 cameraShift = new Vector2(centerPoint.x - cameraPosition.x, cameraPosition.y - centerPoint.y);
            Vector2 cameraShiftValue = cameraShift.cpy().scl(lastZoom - newZoom);

            if (cameraSystem.setZoom(newZoom))
                cameraSystem.moveBy(cameraShiftValue.x, cameraShiftValue.y);

            cameraSystem.moveBy(lastPointer.x - centerPoint.x, centerPoint.y - lastPointer.y);
        }

        lastPointer = centerPoint;
        lastZoom = newZoom;

        return false;
    }

    @Override
    public void pinchStop() {
        cameraSystem.setZooming(false);
        lastPointer = null;
    }

    public Engine getEngine() {
        return engine;
    }
}
